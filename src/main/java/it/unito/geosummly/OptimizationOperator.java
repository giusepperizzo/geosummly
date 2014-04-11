package it.unito.geosummly;

import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.LogDataIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OptimizationOperator {
	
	public void execute(String inGeo, String inLog, String output, ArrayList<Double> weights, int top) throws IOException{
		
		//Read input files
		GeoJSONReader geoReader=new GeoJSONReader();
		ArrayList<ArrayList<Integer>> infos=geoReader.decodeForOptimization(inGeo);
		LogDataIO logReader=new LogDataIO();
		ArrayList<Integer> samplingInfos=logReader.readSamplingLog(inLog);
		
		//Function 1: Spatial Coverage
		//for each cluster: #obj_of_cluster / #obj_of_bbox
		ArrayList<Double> spatialCoverage=new ArrayList<Double>();
		for(Integer i: infos.get(0))
			spatialCoverage.add(((double)i)/samplingInfos.get(0));
		
		//Function 2: Density
		//for each cluster: #venues_of_cluster / #obj_of_cluster
		//#obj_of_cluster represents the dimension of the cluster
		ArrayList<Double> density= new ArrayList<Double>();
		for(int i=0;i<infos.get(1).size();i++)
			density.add(((double) infos.get(1).get(i))/infos.get(0).get(i));
		
		//Function 3: Heterogeneity
		//for each cluster: #categories_of_cluster / #total_categories
		//#categories_of_cluster is the number of cluster labels
		//#total_categories is the number of categories found in the sampling state
		ArrayList<Double> heterogeneity= new ArrayList<Double>();
		for(Integer i: infos.get(2))
			heterogeneity.add(((double) infos.get(2).get(i))/samplingInfos.get(1));
		
		ArrayList<Double> fZeroes=new ArrayList<Double>();
		for(int i=0;i<spatialCoverage.size();i++) {
			fZeroes.add(
						(spatialCoverage.get(i)*weights.get(0)) + 
						(density.get(i)*weights.get(1)) + 
						(heterogeneity.get(i)*weights.get(2)) 
						);
		}
		
		//Sort in decreasing order
		Comparator<Double> comparator=Collections.reverseOrder();
		Collections.sort(fZeroes, comparator);
		
		for(Double d: fZeroes)
			System.out.println(d);
	}

}
