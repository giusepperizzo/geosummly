package it.unito.geosummly;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * M*N Transformation matrix creation.
 * M is the number of bounding box cell.
 * N is the total number of categories found in the bounding box.
 * 
 * In the frequency matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the latitude and longitude values for the (M_i)th cell.
 * For the cell C_ij, 0<i<M-1 and 2<j<N-1, let OCC_j be the occurrence value of the (N_j)th category for the
 * (M_i)th cell, so C_ij will contain the frequency value OCC_j.
 * 
 * In the density matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the latitude and longitude values for the (M_i)th cell.
 * For the cell C_ij, 0<i<M-1 and 2<j<N-1, let OCC_j be the occurrence of the (N_j)th category for the (M_i)th cell
 * as previously defined and let AREA_i be the area value for M_i, so C_ij will contain a density value given by
 * (FREQ_J)/(AREA_i).
 * 
 * In the normalized matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the shifted latitude and longitude values in [0,1] for the 
 * (M_i)th cell (only if specified in the method, otherwise the coordinates will be not shifted).
 * For a cell C_ij, 0<i<M-1 and 2<j<N-1, let DENS_j be the density values of the (N_j)th category for the (M_i)th cell
 * as previously defined and let TOT_j be the sum of the values of the (N_j)th column, so C_ij will contain a new
 * density value given by (DENS_j/TOT_j). This means that all these values are intra-feature normalized. After this
 * normalization step, all these values are shifted in [0,1].
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
