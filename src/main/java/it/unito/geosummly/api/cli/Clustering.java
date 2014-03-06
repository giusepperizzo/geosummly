package it.unito.geosummly.api.cli;

import java.io.IOException;

import it.unito.geosummly.ClusteringOperator;

public class Clustering {

	public void run(String[] args) throws IOException {
		ClusteringOperator co=new ClusteringOperator();
		co.execute("", "", "", "", "", "");
	}
	

}
