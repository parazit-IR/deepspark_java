package org.acl.deepspark.utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.NDArrayFactory;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndexAll;
import org.nd4j.linalg.indexing.NDArrayIndexEmpty;
import org.nd4j.linalg.util.NDArrayUtil;

public class ArrayUtils {
	public static final int FULL_CONV = 0;
	public static final int SAME_CONV = 1;
	public static final int VALID_CONV = 2;
	
    public static INDArray makeColumnVector(INDArray data) {
		INDArray d = data.dup();
		return d.reshape(data.length(), 1);
	}
    
    public static INDArray rot90(INDArray toRotate) {
        if (!toRotate.isMatrix())
            throw new IllegalArgumentException("Only rotating matrices");

        INDArray start = toRotate.transpose();
        for (int i = 0; i < start.rows(); i++)
            start.putRow(i, reverse(start.getRow(i)));
        return start;
    }
    
    public static INDArray reverse(INDArray reverse) {
        INDArray rev = reverse.linearView();
        INDArray ret = Nd4j.create(rev.shape());
        int count = 0;
        for (int i = rev.length() - 1; i >= 0; i--) {
            ret.putScalar(count++, rev.getFloat(i));
        }
        return ret.reshape(reverse.shape());
    }
    
    public static int argmax(INDArray arr) {
    	double[] data = arr.data().asDouble();
    	double maxVal = Double.NEGATIVE_INFINITY;
    	int maxIdx = -1;
    	
    	for(int i = 0; i < data.length; i++) {
    		if(data[i] > maxVal) {
    			maxVal = data[i];
    			maxIdx = i;
    		}
    	}
    	return maxIdx;
    }

	// 2D
	public static INDArray subMatrix(INDArray arr, int[] offset, int[] shape) {
		assert offset.length == shape.length;
		INDArrayIndex[] sub = new INDArrayIndex[shape.length];
		for(int i = 0; i < sub.length; i++) {
			sub[i] = NDArrayIndex.interval(offset[i], shape[i], false);
		}

		//arr.get(NDArrayIndex.createCoveringShape())
		return null;
	}


    
    public static INDArray convolution(INDArray data, INDArray filter, int option) {
		INDArray result;
		INDArray input;


		int nCols, nRows;
		switch(option) {
			case FULL_CONV:
				nRows = data.rows() + filter.rows() -1;
				nCols = data.columns() + filter.columns() -1;
				input = Nd4j.zeros(nRows+ filter.rows() -1, nCols + filter.columns() - 1);
				for (int i = 0; i < data.rows(); i++) {
					for (int j = 0; j < data.columns(); j++) {
						input.put(filter.rows() - 1 + i, filter.columns() -1 + j, data.getDouble(i, j));
					}
				}
				break;
			case SAME_CONV:
				nRows = data.rows();
				nCols = data.columns();
				input = Nd4j.zeros(nRows+ filter.rows() - 1, nCols + filter.columns() - 1);
				input.put(new INDArrayIndex[] {NDArrayIndex.interval(filter.rows() / 2, filter.rows() / 2 + data.rows()),
						NDArrayIndex.interval(filter.columns() / 2, filter.columns() /2  + data.columns())},
						data);
				break;
			case VALID_CONV:
				nRows = data.rows() - filter.rows() + 1;
				nCols = data.columns() - filter.columns() + 1;
				input = data;
				break;
			default:
				return null;
		}

		result = Nd4j.zeros(nRows, nCols);
		for(int r = 0; r < nRows ; r++) {
			for(int c = 0 ; c < nCols ; c++) {
				double sum = 0;
				for(int i = 0; i < filter.rows(); i++) {
					for(int j = 0; j < filter.columns();j++) {
						sum += input.getDouble(r+i, c+j) * filter.getDouble(i,j);
					}
				}
				result.put(r,c, sum);
			}
		}
		return result;
	}
}
