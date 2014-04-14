package it.unito.geosummly;

import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.tools.OptimizationTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class OptimizationOperator {
	
	@SuppressWarnings("rawtypes")
	public void execute(String inGeo, String inLog, String output, ArrayList<Double> weights, int top) throws IOException{
		
		//Read input files
		GeoJSONReader geoReader=new GeoJSONReader();
		ArrayList<ArrayList<Integer>> geo_infos=geoReader.decodeForOptimization(inGeo);
		LogDataIO logReader=new LogDataIO();
		ArrayList<Integer> s_infos=logReader.readSamplingLog(inLog);
		
		OptimizationTools tools=new OptimizationTools();
		
		//Function 1: Spatial Coverage
		ArrayList<Double> spatialCoverage=tools.getSpatialCoverage(geo_infos.get(0), s_infos.get(0));
		
		//Function 2: Density
		ArrayList<Double> density = tools.getDensity(geo_infos.get(1), geo_infos.get(0));
		
		//Function 3: Heterogeneity
		ArrayList<Double> heterogeneity = tools.getHeterogeneity(geo_infos.get(2), s_infos.get(1));
		
		//Linear combination f0 = w1*f1 + w2*f2 + w3*f3 for each cluster
		Map<Integer, Double> clusterMap = tools.getLinearCombination(spatialCoverage, density, heterogeneity, weights);
		
		//Sort values in decreasing order
		Map<Integer, Double> sortedMap = tools.sortByValue(clusterMap);
		
		System.out.println("Mappa non ordinata");
		for (Map.Entry entry : clusterMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}
		
		System.out.println("\n\nMappa ordinata");
		for (Map.Entry entry : sortedMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}
	}
}
