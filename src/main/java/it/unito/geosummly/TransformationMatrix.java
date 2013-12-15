package it.unito.geosummly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * M*N Transformation matrix creation.
 * M is the number of bounding box cell.
 * N is the total number of categories found in the bounding box.
 * 
 * In the not-normalized matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the latitude and longitude values for the (M_i)th cell.
 * The cell C_ij, 0<i<M-1 and 2<j<N-1, let OCC_j be the occurrence of the (N_j)th category for the (M_i)th cell and
 * let TOT_j be the total number of categories found in the (N_j)th column. So, C_ij will contain a frequency value given by
 * (OCC_j/TOT_j). This means that all these values are intra-feature normalized.
 * 
 * In the normalized-matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the normalized latitude and longitude values in [0,1] for the (M_i)th cell.
 * For a cell C_ij, 0<i<M-1 and 2<j<N-1, let FREQ_j be the intra-feature normalized frequency of the (N_j)th category
 * for the (M_i)th cell and let AREA_i be the area value for M_i. So, C_ij will contain a density value given by (FREQ_J)/(AREA_i),
 * normalized in [0,1].
 *   
 */
public class TransformationMatrix {
	private ArrayList<ArrayList<Double>> frequencyMatrix; //data structure with frequencies
	private ArrayList<ArrayList<Double>> densityMatrix; //data structure with densities
	private ArrayList<ArrayList<Double>> normalizedMatrix; //data structure with densities after normalization
	private ArrayList<String> header;
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationMatrix(){}
	
	
	public ArrayList<ArrayList<Double>> getFrequencyMatrix() {
		return frequencyMatrix;
	}

	public void setFrequencyMatrix(ArrayList<ArrayList<Double>> frequencyMatrix) {
		this.frequencyMatrix = frequencyMatrix;
	}


	public ArrayList<ArrayList<Double>> getDensityMatrix() {
		return densityMatrix;
	}

	public void setDensityMatrix(ArrayList<ArrayList<Double>> densityMatrix) {
		this.densityMatrix = densityMatrix;
	}
	
	public ArrayList<ArrayList<Double>> getNormalizedMatrix() {
		return normalizedMatrix;
	}

	public void setNormalizedMatrix(ArrayList<ArrayList<Double>> normalizedMatrix) {
		this.normalizedMatrix = normalizedMatrix;
	}
	
	public ArrayList<String> getHeader() {
		return header;
	}

	public void setHeader(ArrayList<String> header) {
		this.header = header;
	}
}
