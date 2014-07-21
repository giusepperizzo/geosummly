package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.GeoJSONWriter;
import it.unito.geosummly.io.GeoTurtleWriter;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.tools.ClusteringTools;
import it.unito.geosummly.utils.Pair;

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

	public void execute(	
			ArrayList<Double> coord, 
			String inDens, 
			String inNorm, 
			String inDeltad, 
			String inSingles, 
			String out, 
			double eps, 
			String method
			) 
					throws IOException 
					{
		//Read all the csv files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> listDens=dataIO.readCSVFile(inDens);
		List<CSVRecord> listNorm=dataIO.readCSVFile(inNorm);
		List<CSVRecord> listDeltad=dataIO.readCSVFile(inDeltad);
		List<CSVRecord> listSingles=dataIO.readCSVFile(inSingles);

		//Get the bounding box
		Double north = new Double(coord.get(0));
		Double east = new Double(coord.get(1));
		Double south = new Double(coord.get(2));
		Double west = new Double(coord.get(3));
		BoundingBox bbox=new BoundingBox(north, east, south, west);
		double area = bbox.getArea();

		//get the number of cell
		//-1 because of the header
		int cellNum = listDens.size()-1;

		//get the option -combination
		//that is the maximum number of categories considered in a combination
		int comb = 3;

		ClusteringTools tools=new ClusteringTools();

		//fill in the matrix of normalized values
		//the column timestamp is not included in normMatrix
		ArrayList<ArrayList<Double>> normMatrix=tools.buildNormalizedFromCSV(listNorm);
		ArrayList<Pair<Double, Double>> boundaries = tools.getFeatureBoundariesFromCSV(listDens);


		//build db from the normalized matrix
		Database db=tools.buildDatabaseFromMatrix(normMatrix);

		//fill in the feature hashmap only with single features
		HashMap<Integer, String> featuresMap=tools.getFeaturesMapFromDeltad(listDeltad);

		//fill in the deltad hashmap with that values which are greater than or equal to 0 and whose feature is in the features hashmap
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
		Clustering<?> result = tools
				.runGEOSUBCLU(db, boundaries, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
		ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
		HashMap<Integer, String> clustersName=new HashMap<Integer, String>(); //key, cluster name
		HashMap<Integer, ArrayList<ArrayList<Double>>> cellsOfCluster=new HashMap<Integer, ArrayList<ArrayList<Double>>>(); //key, cell_ids + lat + lng 
		HashMap<Integer, ArrayList<ArrayList<String>>> venuesOfCell=new HashMap<Integer, ArrayList<ArrayList<String>>>(); //cell_id, venue_record
		HashMap<Integer, Double> cDistance = new HashMap<>();
		HashMap<Integer, Double> cSSE = new HashMap<>(); 
		HashMap<Integer, Double> cSurface = new HashMap<>();
		HashMap<Integer, Double> cHeterogeneity = new HashMap<>();
		HashMap<Integer, Double> cDensity = new HashMap<>();

		ArrayList<ArrayList<Double>> cells;

		for(Clustering<?> c: cs) {
			for(Cluster<?> cluster: c.getAllClusters()) {
				int index=clustersName.size(); //at first clustersName.size()=0
				clustersName.put(index, cluster.getName()); //put the cluster name in the map (clustersName.size()++)
				cellsOfCluster=tools.putCompleteCellsOfCluster(cellsOfCluster, cluster, index, listDens); //get all the cell_ids for the selected cluster
				cells=cellsOfCluster.get(index);
				venuesOfCell=tools.putVenuesOfCells(cluster.getName(), index, venuesOfCell, cells, listSingles);

				cDistance.put(index, new Double(tools.getDistance(db, cluster, featuresMap, listDens)));
				cSSE.put(index, tools.getClusterSSE(db, cluster, featuresMap));
				cSurface.put(index, tools.getClusterSurface(cluster, normMatrix.size()));
				cHeterogeneity.put(index, tools.getClusterHeterogeneity(cluster, featuresMap));
				cDensity.put(index, tools.getClusterDensity(venuesOfCell.size(), area, cSurface.get(index)));

			}
		}

		//Get the SSE
		double sse=tools.getClusteringSSE(db, cs, featuresMap);

		//serialize the log
		LogDataIO lWriter=new LogDataIO();
		StringBuilder sb=tools.getLog();
		lWriter.writeClusteringLog(sb, eps, sse, north, east, south, west, cellNum, out);

		//serialize the clustering output to geojson and turtle files
		GeoJSONWriter jWriter=new GeoJSONWriter();

		jWriter.writeStream(bbox, 
				clustersName, 
				cellsOfCluster, 
				venuesOfCell, 
				cDistance,
				cSSE,
				cSurface,
				cHeterogeneity,
				cDensity,
				eps, 
				out, 
				cal);
		GeoTurtleWriter tWriter=new GeoTurtleWriter();
		tWriter.writeStream(bbox, 
				clustersName, 
				cellsOfCluster, 
				venuesOfCell, 
				cDistance,
				cSSE,
				cSurface,
				cHeterogeneity,
				cDensity,	    					
				eps, 
				out, 
				cal);
					}

	public HashMap<String, Vector<Integer>> executeForValidation(
			ArrayList<ArrayList<Double>> normalized, 
			int length, ArrayList<String> labels, 
			ArrayList<String> minpts, 
			double eps
			) 
					throws IOException 
					{			
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
		Clustering<?> result = tools.runGEOSUBCLU(db, null, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
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

	public double executeForCorrectness(
			ArrayList<ArrayList<Double>> normalized, 
			ArrayList<String> labels, 
			ArrayList<String> minpts, 
			double eps
			)
					throws IOException 
					{	
		ClusteringTools tools=new ClusteringTools();

		//build the database from the normalized matrix
		//the column of timestamp values is not present in the variable "normalized"
		Database db=tools.buildDatabaseFromMatrix(normalized);

		//fill in the feature hashmap only with single features
		HashMap<Integer, String> featuresMap=tools.getFeaturesMap(labels);

		//fill in the deltad hashmap
		HashMap<String, Double> deltadMap=tools.getDeltadMap(labels, minpts);

		//% of cells
		Double density=normalized.size()*0.9;

		//Run GEOSUBCLU algorithm and get the clustering result
		Clustering<?> result = tools.runGEOSUBCLU(db, null, featuresMap, deltadMap, density.intValue(), eps, new StringBuilder());
		ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);

		//Get the SSE
		double sse=tools.getClusteringSSE(db, cs, featuresMap);

		return sse;
					}
}