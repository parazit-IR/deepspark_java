package org.acl.deepspark.nn.async;

import org.acl.deepspark.data.Weight;
import org.acl.deepspark.nn.driver.NeuralNet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ParameterServer {
	private NeuralNet p;
	private int listenPort;
	private int castPort;
	private ServerSocket updateSocket;
	private ServerSocket castSocket;
	
	private boolean stopSign = false;
	private final Object lock = new Object();
	
	private Thread[] threads;
	
	public ParameterServer(NeuralNet net, int minibatch, int[] port) {
		p = net;
		listenPort = port[0];
		castPort = port[1];
		threads = new Thread[2];
	}
	
	public void stopServer() {
		stopSign = true;
		try {
			updateSocket.close();
			castSocket.close();
			threads[0].join();
			threads[1].join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() throws IOException {
		updateSocket = new ServerSocket(listenPort);
		updateSocket.setSoTimeout(15000);
		castSocket = new ServerSocket(castPort);
		castSocket.setSoTimeout(15000);
		
		// update socket
		threads[0] = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!stopSign) {
					try {
						Socket a = castSocket.accept();
						synchronized (lock) {
							ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(a.getOutputStream()));
							os.writeObject(p.getWeights());
						}
						a.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threads[0].start();
		
		threads[1] = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(!stopSign) {
					try {
						Socket a = updateSocket.accept();
						synchronized (lock) {
							ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(a.getInputStream()));
							p.updateWeight((Weight[]) is.readObject());
						}
						a.close();
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
		threads[1].start();	
	}
}
