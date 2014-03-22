package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.GeoJSONDataWriter;
import it.unito.geosummly.tools.ClusteringTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVRecord;

import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.result.ResultUtil;

public class ClusteringOperator<V> {

	public static Logger logger = Logger.getLogger(ClusteringOperator.class.toString());

	public void execute(String inDens, String inNorm, String inDeltad, String inSingles, String out, String method) throws IOException {
		
		//Read all the csv files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> listDens=dataIO.readCSVFile(inDens);
		List<CSVRecord> listNorm=dataIO.readCSVFile(inNorm);
		List<CSVRecord> listDeltad=dataIO.readCSVFile(inDeltad);
		List<CSVRecord> listSingles=dataIO.readCSVFile(inSingles);
		
		ClusteringTools tools=new ClusteringTools();
		
		//fill in the matrix of normalized values
		ArrayList<ArrayList<Double>> normMatrix=tools.buildNormalizedFromCSV(listNorm);
		
		//build the database from the normalized matrix
		Database db=tools.buildDatabaseFromMatrix(normMatrix);
		
		//fill in the feature hashmap only with single features and only if the corresponding value is greater than 0
		HashMap<Integer, String> featuresMap=tools.getFeaturesMapFromDeltad(listDeltad);
		
		//fill in the deltad hashmap with that values which are greater than 0 and whose feature is in the features hashmap
	    HashMap<String, Double> deltadMap=tools.getValuesMapFromDeltad(listDeltad);
		
		//90% of cells
		Double density=normMatrix.size()*0.9;
		
		//eps value used for clustering
		double eps=0.08;
	    
		//Run GEOSUBCLU algorithm and get the clustering result
	    Clustering<?> result = tools.runGEOSUBCLU(db, featuresMap, deltadMap, density.intValue(), eps);
	    ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
	    HashMap<Integer, String> clustersName=new HashMap<Integer, String>(); //key, cluster name
	    HashMap<Integer, ArrayList<ArrayList<Double>>> cellsOfCluster=new HashMap<Integer, ArrayList<ArrayList<Double>>>(); //key, cell_ids + lat + lng 
	    HashMap<Integer, ArrayList<ArrayList<String>>> venuesOfCell=new HashMap<Integer, ArrayList<ArrayList<String>>>(); //cell_id, venue_record
	    ArrayList<ArrayList<Double>> cells;
	    
	    for(Clustering<?> c: cs) {
	    	for(Cluster<?> cluster: c.getAllClusters()) {
	    		int index=clustersName.size();
	    		clustersName.put(index, cluster.getName()); //put the cluster name in the map
	    		cellsOfCluster=tools.putCompleteCellsOfCluster(cellsOfCluster, cluster, index, listDens); //get all the cell_ids for the selected cluster
	    		cells=cellsOfCluster.get(index);
	    		venuesOfCell=tools.putVenuesOfCells(venuesOfCell, cells, listSingles);
	    	}
	    }
	    
	    //serialize to .geojson file
	    GeoJSONDataWriter writer=new GeoJSONDataWriter();
	    writer.writeJsonStream(clustersName, cellsOfCluster, venuesOfCell, eps, out);
	}
    
	public HashMap<String, Vector<Integer>> executeForEvaluation(ArrayList<ArrayList<Double>> normalized, int length, String inDeltad) throws IOException {
		
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> listDeltad=dataIO.readCSVFile(inDeltad);
		
		ClusteringTools tools=new ClusteringTools();
		
		//build the database from the normalized matrix without considering timestamp values
		ArrayList<ArrayList<Double>> normMatrix=tools.buildNormalizedFromList(normalized);
		Database db=tools.buildDatabaseFromMatrix(normMatrix);
		
		//fill in the feature hashmap only with single features and only if the corresponding value is greater than 0
		HashMap<Integer, String> featuresMap=tools.getFeaturesMapFromDeltad(listDeltad);
		
		//fill in the deltad hashmap with that values which are greater than 0 and whose feature is in the features hashmap
	    HashMap<String, Double> deltadMap=tools.getValuesMapFromDeltad(listDeltad);
		
		//90% of cells
		Double density=normMatrix.size()*0.9;
		
		//eps value used for clustering
		double eps=0.09;
	    
		//Run GEOSUBCLU algorithm and get the clustering result
	    Clustering<?> result = tools.runGEOSUBCLU(db, featuresMap, deltadMap, density.intValue(), eps);
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
}