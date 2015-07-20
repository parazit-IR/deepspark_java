package org.acl.deepspark.nn.functions;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.SigmoidDerivative;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.util.NDArrayUtil;

public class ActivatorFactory {
	public static Activator getActivator(ActivatorType t) {
		switch(t) {
		case SIGMOID:
			return new Activator() {
				
				@Override
				public INDArray output(INDArray input) {
					return Transforms.sigmoid(input);
				}
				
				@Override
				public INDArray derivative(INDArray input) {
					INDArray ret = Nd4j.onesLike(input);
					INDArray f = output(input);
					return ret.subi(f).muli(f);
				}
			};
			
		case RECTIFIED_LINEAR:
			return new Activator() {
				@Override
				public INDArray output(INDArray input) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public INDArray derivative(INDArray input) {
					// TODO Auto-generated method stub
					return null;
				}
			};
			
		case SOFTMAX:
			return new Activator() {
				@Override
				public INDArray output(INDArray input) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public INDArray derivative(INDArray input) {
					// TODO Auto-generated method stub
					return null;
				}
			};
		default:
			return null;
		}
		
	}
}