package it.unito.geosummly;

import fr.vergne.pareto.ParetoComparator;
import fr.vergne.pareto.ParetoHelper;
import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.GeoJSONWriter;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.io.templates.FeatureCollectionTemplate;
import it.unito.geosummly.io.templates.VenueTemplate;
import it.unito.geosummly.pareto.ParetoPoint;
import it.unito.geosummly.tools.OptimizationTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class OptimizationOperator {
	
	
	public void executePareto(	String inGeo, 
								String output
							 )
	throws Exception
	{
		//Read input files
		GeoJSONReader geoReader=new GeoJSONReader();
		FeatureCollectionTemplate fct = geoReader.decodeForOptimization(inGeo);
		LogDataIO logIO=new LogDataIO();
		//ArrayList<Integer> s_infos=logIO.readSamplingLog(inLog);
		
		OptimizationTools tools=new OptimizationTools();
		
		//Get the clustering infos
		ArrayList<ArrayList<ArrayList<Double>>> multiPoints = tools.getMultiPoints(fct);
		ArrayList<ArrayList<VenueTemplate>> 	venues = tools.getVenuesOfClusters(fct);
		//ArrayList<ArrayList<ArrayList<Double>>> cells = tools.getObjectsOfClusters(venues);
		ArrayList<String[]> 					labels = tools.getLabelsOfClusters(fct);
		ArrayList<ArrayList<Integer>>	        cellIDs = tools.getCellIDs(fct);
		
		//Function 1: Spatial Coverage
		ArrayList<Double> surface=tools.getSpatialCoverage(fct);//getSpatialCoverage(cells, s_infos.get(0));
		//Function 2: Density
		ArrayList<Double> density = tools.getDensity(fct);
		//Function 3: Heterogeneity
		ArrayList<Double> heterogeneity = tools.getHeterogeneity(fct);
		//Function 4: SSE
		ArrayList<Double> sse = tools.getSSE(fct);
		
		ArrayList<Double> distance = tools.getDistance(fct);
		
		String date=fct.getProperties().getDate();
		double eps=fct.getProperties().getEps();
		BoundingBox bbox=fct.getProperties().getBbox();		
				
		if(surface.size() != density.size() &&
		   surface.size() != heterogeneity.size() &&
		   surface.size() != sse.size())
			throw new Exception("The objective functions are not aligned");
		
		Collection<ParetoPoint> paretoPoints = 
								tools.getParetoPoints(  surface,
										 			    density,
										 			    heterogeneity,
										 			    sse);
			
		ParetoComparator<Integer, ParetoPoint> comparator = new ParetoComparator<Integer, ParetoPoint>();
		comparator.setDimensionComparator(0, new Comparator<ParetoPoint>() {
			public int compare(ParetoPoint o1, ParetoPoint o2) {
				return Double.valueOf(o1.getX()).compareTo(Double.valueOf(o2.getX()));
			}
		});
		comparator.setDimensionComparator(1, new Comparator<ParetoPoint>() {
			public int compare(ParetoPoint o1, ParetoPoint o2) {
				return Double.valueOf(o1.getY()).compareTo(Double.valueOf(o2.getY()));
			}
		});
		comparator.setDimensionComparator(2, new Comparator<ParetoPoint>() {
			public int compare(ParetoPoint o1, ParetoPoint o2) {
				return Double.valueOf(o1.getZ()).compareTo(Double.valueOf(o2.getZ()));
			}
		});
		comparator.setDimensionComparator(3, new Comparator<ParetoPoint>() {
			public int compare(ParetoPoint o1, ParetoPoint o2) {
				return Double.valueOf(o1.getS()).compareTo(Double.valueOf(o2.getS()));
			}
		});
		
		Collection<ParetoPoint> frontier = ParetoHelper
				.<ParetoPoint> getMaximalFrontierOf(paretoPoints, comparator);
				
		List<Integer> selected = tools.listSelected (frontier);
			
		//Get the top clusters infos
		multiPoints = tools.getTopMultiPoints(multiPoints, selected);
		venues = tools.getTopVenues(venues, selected);
		labels = tools.getTopLabels(labels, selected);
		cellIDs = tools.getTopCellIDs(cellIDs, selected);
		
		//Serialize the optimized output to file and create the log
		GeoJSONWriter geoWriter=new GeoJSONWriter();
		geoWriter.writerAfterOptimization( bbox, 
										   selected, 
										   multiPoints, 
										   cellIDs,
										   surface,
										   density,
										   heterogeneity,
										   sse, 
										   distance,
										   venues, 
										   labels, 
										   eps, 
										   date, 
										   output,
										   "pareto"
										  );
		
		logIO.writeParetoLog(paretoPoints, labels, cellIDs, selected, output);

	}
	
	public void executeLinear(	String inGeo, 
								String output, 
								ArrayList<Double> weights, 
								int top
							 ) 
	throws IOException 
	{	
		//Read input files
		GeoJSONReader geoReader=new GeoJSONReader();
		FeatureCollectionTemplate fct = geoReader.decodeForOptimization(inGeo);
		LogDataIO logIO=new LogDataIO();
		//ArrayList<Integer> s_infos=logIO.readSamplingLog(inLog);
		
		OptimizationTools tools=new OptimizationTools();
		
		//Get the clustering infos
		ArrayList<ArrayList<ArrayList<Double>>> multiPoints = tools.getMultiPoints(fct);
		ArrayList<ArrayList<VenueTemplate>> venues = tools.getVenuesOfClusters(fct);
		//ArrayList<ArrayList<ArrayList<Double>>> cells = tools.getObjectsOfClusters(venues);
		ArrayList<String[]> labels = tools.getLabelsOfClusters(fct);
		ArrayList<ArrayList<Integer>>	        cellIDs = tools.getCellIDs(fct);
		
		String date=fct.getProperties().getDate();
		double eps=fct.getProperties().getEps();
		BoundingBox bbox=fct.getProperties().getBbox();
		
		//Function 1: Spatial Coverage
		ArrayList<Double> surface=tools.getSpatialCoverage(fct);
		
		//Function 2: Density
		ArrayList<Double> density = tools.getDensity(fct);
		
		//Function 3: Heterogeneity
		ArrayList<Double> heterogeneity = tools.getHeterogeneity(fct);
		
		//Function 4: SSE
		ArrayList<Double> sse = tools.getSSE(fct);
				
		ArrayList<Double> distance = tools.getDistance(fct);

		
		//Linear combination f0 = w1*f1 + w2*f2 + w3*f3 for each cluster
		Map<Integer, Double> clusterMap = tools.getLinearCombination(surface, 
																	 density, 
																	 heterogeneity, 
																	 weights);
		
		//Sort values in decreasing order
		Map<Integer, Double> sortedMap = tools.sortByValue(clusterMap);
		
		//Check the top value
		if(top>sortedMap.size()) top=sortedMap.size();
		
		//Select the best #top clusters to keep in the fingerprint
		List<Integer> selected = tools.selectTop(sortedMap, top);
		
		//Get the top clusters infos
		multiPoints = tools.getTopMultiPoints(multiPoints, selected);
		venues = tools.getTopVenues(venues, selected);
		labels = tools.getTopLabels(labels, selected);
		cellIDs = tools.getTopCellIDs(cellIDs, selected);
		
		//Serialize the optimized output to file and create the log
		GeoJSONWriter geoWriter=new GeoJSONWriter();
		geoWriter.writerAfterOptimization(
											bbox, 
											selected, 
											multiPoints, 
											cellIDs,
  										    surface,
											density,
											heterogeneity,											
											sse, 
											distance,
											venues, 
											labels, 
											eps, 
											date, 
											output,
											"linear"
   										 );
		logIO.writeOptimizationLog(selected, sortedMap, weights, surface, 
				density, heterogeneity, new ArrayList<Double>(clusterMap.values()), output);
	}
}
