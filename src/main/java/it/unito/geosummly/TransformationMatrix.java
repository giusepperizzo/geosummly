package it.unito.geosummly;

import java.util.ArrayList;
import java.util.logging.Logger;

public class TransformationMatrix {
	private ArrayList<ArrayList<Double>> matrix; //data structure
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationMatrix(){}
	
	public ArrayList<ArrayList<Double>> getMatrix() {
		return matrix;
	}
	
	public void setMatrix(ArrayList<ArrayList<Double>> matrix) {
		this.matrix = matrix;
	}
	
	public void addRow(ArrayList<Double> row){
		this.matrix.add(row);
	}
	
}
