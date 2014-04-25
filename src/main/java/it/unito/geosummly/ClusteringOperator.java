package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.GeoJSONWriter;
import it.unito.geosummly.io.GeoTurtleWriter;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.tools.ClusteringTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVRecord;

import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.ResultUtil;

public class ClusteringOperator {

	public static Logger logger = Logger.getLogger(ClusteringOperator.class.toString());

	public void execute(ArrayList<Double> coord, String inDens, String inNorm, String inDeltad, String inSingles, String out, double eps, String method) throws IOException {
		
		//Read all the csv files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> listDens=dataIO.readCSVFile(inDens);
		List<CSVRecord> listNorm=dataIO.readCSVFile(inNorm);
		List<CSVRecord> listDeltad=dataIO.readCSVFile(inDeltad);
		List<CSVRecord> listSingles=dataIO.readCSVFile(inSingles);
		
		//Get the bounding box
		double north=coord.get(0);
		double east=coord.get(1);
		double south=coord.get(2);
		double west=coord.get(3);
		BoundingBox bbox=new BoundingBox(north, east, south, west);
		
		ClusteringTools tools=new ClusteringTools();
		
		//fill in the matrix of normalized values
		ArrayList<ArrayList<Double>> normMatrix=tools.buildNormalizedFromCSV(listNorm);
		
		//build the database from the normalized matrix
		Database db=tools.buildDatabaseFromMatrix(normMatrix);
		
		//fill in the feature hashmap only with single features and only if the corresponding value is greater than 0
		HashMap<Integer, String> featuresMap=tools.getFeaturesMapFromDeltad(listDeltad);
		
		//fill in the deltad hashmap with that values which are greater than 0 and whose feature is in the features hashmap
	    HashMap<String, Double> deltadMap=tools.getValuesMapFromDeltad(listDeltad);
	    
	    //get the calendar
	    Calendar cal=tools.getCalendar(listNorm);
		
		//90% of cells
		Double density=normMatrix.size()*0.9;
		
		//Get eps value
		//This means that no eps value has been specified in CLI or the eps specified is negative
		if(eps <= 0.0) {
			eps=tools.getEps(normMatrix);
		}
	    
		//Run GEOSUBCLU algorithm and get the clustering result
	    Clustering<?> result = tools.runGEOSUBCLU(db, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
	    ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
	    HashMap<Integer, String> clustersName=new HashMap<Integer, String>(); //key, cluster name
	    HashMap<Integer, ArrayList<ArrayList<Double>>> cellsOfCluster=new HashMap<Integer, ArrayList<ArrayList<Double>>>(); //key, cell_ids + lat + lng 
	    HashMap<Integer, ArrayList<ArrayList<String>>> venuesOfCell=new HashMap<Integer, ArrayList<ArrayList<String>>>(); //cell_id, venue_record
	    ArrayList<ArrayList<Double>> cells;
	    
	    for(Clustering<?> c: cs) {
	    	for(Cluster<?> cluster: c.getAllClusters()) {
	    		int index=clustersName.size(); //at first clustersName.size()=0
	    		clustersName.put(index, cluster.getName()); //put the cluster name in the map (clustersName.size()++)
	    		cellsOfCluster=tools.putCompleteCellsOfCluster(cellsOfCluster, cluster, index, listDens); //get all the cell_ids for the selected cluster
	    		cells=cellsOfCluster.get(index);
	    		venuesOfCell=tools.putVenuesOfCells(cluster.getName(), index, venuesOfCell, cells, listSingles);
	    	}
	    }
	    
	    //Get the SSE
		double sse=tools.getClusteringSSE(db, cs);
	    
	    //serialize the log
	    LogDataIO lWriter=new LogDataIO();
	    StringBuilder sb=tools.getLog();
	    lWriter.writeClusteringLog(sb, eps, sse, out);
	    
	    
	    //serialize the clustering output to geojson and turtle files
	    GeoJSONWriter jWriter=new GeoJSONWriter();
	    jWriter.writeStream(bbox, clustersName, cellsOfCluster, venuesOfCell, eps, out, cal);
	    GeoTurtleWriter tWriter=new GeoTurtleWriter();
	    tWriter.writeStream(bbox, clustersName, cellsOfCluster, venuesOfCell, eps, out, cal);
	}
    
	public HashMap<String, Vector<Integer>> executeForValidation(ArrayList<ArrayList<Double>> normalized, int length, ArrayList<String> labels, ArrayList<String> minpts, double eps) throws IOException {
				
		ClusteringTools tools=new ClusteringTools();
		
		//build the database from the normalized matrix without considering timestamp values
		ArrayList<ArrayList<Double>> normMatrix=tools.buildNormalizedFromList(normalized);
		Database db=tools.buildDatabaseFromMatrix(normMatrix);
		
		//fill in the feature hashmap only with single features
		HashMap<Integer, String> featuresMap=tools.getFeaturesMap(labels);
		
		//fill in the deltad hashmap
	    HashMap<String, Double> deltadMap=tools.getDeltadMap(labels, minpts);
		
		//% of cells
		Double density=normMatrix.size()*0.9;
	    
		//Run GEOSUBCLU algorithm and get the clustering result
	    Clustering<?> result = tools.runGEOSUBCLU(db, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
	    ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
	    HashMap<Integer, String> clustersName=new HashMap<Integer, String>(); //key, cluster name
	    HashMap<Integer, ArrayList<Integer>> cellsOfCluster=new HashMap<Integer, ArrayList<Integer>>(); //key, cell_ids
	    
	    for(Clustering<?> c: cs) {
	    	for(Cluster<?> cluster: c.getAllClusters()) {
	    		int index=clustersName.size();
	    		//put the cluster name in the map
	    		clustersName.put(index, cluster.getName());
	    		//get all the cell_ids for the selected cluster
	    		cellsOfCluster=tools.putIdCellsOfCluster(cellsOfCluster, cluster, index);
	    	}
	    }
	    
	    //Get the distinct cluster labels
	    TreeSet<String> distinctLabels =tools.getClusterLabels(clustersName);
	    
	    //Associate cells to cluster
	    ArrayList<TreeSet<Integer>> allCells=tools.getCellsOfClusters(clustersName, cellsOfCluster, distinctLabels);
	    
	    //Create holdout map
	    HashMap<String, Vector<Integer>> holdout=tools.buildHoldoutMap(distinctLabels, allCells, length);
	    
	    return holdout;
	}
	
	public double executeForCorrectness(ArrayList<ArrayList<Double>> normalized, ArrayList<String> labels, ArrayList<String> minpts, double eps) throws IOException {
		
		ClusteringTools tools=new ClusteringTools();
		
		//build the database from the normalized matrix without considering timestamp values
		Database db=tools.buildDatabaseFromMatrix(normalized);
		
		//fill in the feature hashmap only with single features
		HashMap<Integer, String> featuresMap=tools.getFeaturesMap(labels);
		
		//fill in the deltad hashmap
	    HashMap<String, Double> deltadMap=tools.getDeltadMap(labels, minpts);
		
		//% of cells
		Double density=normalized.size()*0.9;
		
		//Run GEOSUBCLU algorithm and get the clustering result
	    Clustering<?> result = tools.runGEOSUBCLU(db, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
	    ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
	    
	    //Get the SSE
	    double sse=tools.getClusteringSSE(db, cs);
	    
	    return sse;
	}
}