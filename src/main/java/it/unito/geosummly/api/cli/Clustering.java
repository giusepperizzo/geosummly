package it.unito.geosummly.api.cli;

import java.io.IOException;

import it.unito.geosummly.ClusteringOperator;

public class Clustering {

	public void run(String[] args) throws IOException {
		ClusteringOperator co=new ClusteringOperator();
		co.execute("C:/Users/James/Documents/Università/Torino/Tesi/Git/geosummly/datasets/milan/20140227/normalized-transformation-matrix.csv", 
				"C:/Users/James/Documents/Università/Torino/Tesi/Git/geosummly/datasets/milan/20140227/deltad-values.csv", 
				"", "", "");
	}
	

}
