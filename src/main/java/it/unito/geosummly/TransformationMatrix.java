package it.unito.geosummly;

import java.util.ArrayList;

public class TransformationMatrix {
	private ArrayList<ArrayList<Double>> matrix; //data structure
	
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
