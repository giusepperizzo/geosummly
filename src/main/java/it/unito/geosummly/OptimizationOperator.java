package it.unito.geosummly;

import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.GeoJSONWriter;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.io.templates.FeatureCollectionTemplate;
import it.unito.geosummly.io.templates.VenueTemplate;
import it.unito.geosummly.tools.OptimizationTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OptimizationOperator {
	
	public void execute(String inGeo, String inLog, String output, 
						ArrayList<Double> weights, int top) throws IOException {
		
		//Read input files
		GeoJSONReader geoReader=new GeoJSONReader();
		FeatureCollectionTemplate fct = geoReader.decodeForOptimization(inGeo);
		LogDataIO logIO=new LogDataIO();
		ArrayList<Integer> s_infos=logIO.readSamplingLog(inLog);
		
		OptimizationTools tools=new OptimizationTools();
		
		//Get the clustering infos
		ArrayList<ArrayList<ArrayList<Double>>> cells = tools.getObjectsOfClusters(fct);
		ArrayList<ArrayList<VenueTemplate>> venues = tools.getVenuesOfClusters(fct);
		ArrayList<String[]> labels = tools.getLabelsOfClusters(fct);
		String date=fct.getProperties().getDate();
		double eps=fct.getProperties().getEps();
		BoundingBox bbox=fct.getProperties().getBbox();
		
		//Function 1: Spatial Coverage
		ArrayList<Double> spatialCoverage=tools.getSpatialCoverage(cells, s_infos.get(0));
		
		//Function 2: Density
		ArrayList<Double> density = tools.getDensity(venues, cells);
		
		//Function 3: Heterogeneity
		ArrayList<Double> heterogeneity = tools.getHeterogeneity(labels, s_infos.get(1));
		
		//Linear combination f0 = w1*f1 + w2*f2 + w3*f3 for each cluster
		Map<Integer, Double> clusterMap = tools.getLinearCombination(spatialCoverage, 
																		density, 
																		heterogeneity, 
																		weights);
		
		//Sort values in decreasing order
		Map<Integer, Double> sortedMap = tools.sortByValue(clusterMap);
		
		//Check the top value
		if(top>sortedMap.size())
			top=sortedMap.size();
		
		//Select the best #top clusters to keep in the fingerprint
		List<Integer> selected = tools.selectTop(sortedMap, top);
		
		//Get the top clusters infos
		cells = tools.getTopCells(cells, selected);
		venues = tools.getTopVenues(venues, selected);
		labels = tools.getTopLabels(labels, selected);
		
		//Serialize the optimized output to file and create the log
		GeoJSONWriter geoWriter=new GeoJSONWriter();
		geoWriter.writerAfterOptimization(bbox, cells, venues, labels, eps, date, output);
		logIO.writeOptimizationLog(selected, sortedMap, weights, spatialCoverage, 
				density, heterogeneity, new ArrayList<Double>(clusterMap.values()), output);
	}
}
