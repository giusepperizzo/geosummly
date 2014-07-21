package it.unito.geosummly.tools;

import it.unito.geosummly.clustering.subspace.GEOSUBCLU;
import it.unito.geosummly.clustering.subspace.InMemoryDatabase;
import it.unito.geosummly.utils.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.csv.CSVRecord;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SUBCLU;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class ClusteringTools {
	
	private StringBuilder log;
	
	/**
	 * Constructor method 
	*/
	public ClusteringTools() {
		log=new StringBuilder();
	}
	
	/**
	 * Set the log StringBuilder 
	*/
	public void setLog(StringBuilder log) {
		this.log=log;
	}
	
	/**
	 * Get the log StringBuilder 
	*/
	public StringBuilder getLog() {
		return log;
	}
	
	/**Fill in the matrix of normalized values from a list of CSV records.
	 * The header won't be considered.
	 * The column of timestamp values won't be considered.
	*/
	public ArrayList<ArrayList<Double>> buildNormalizedFromCSV(List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		for(CSVRecord r: list) {
			//we exclude the header
			if(!r.get(0).contains("Timestamp")) {
				ArrayList<Double> record=new ArrayList<Double>();
				//we don't have to consider timepstamp values, so i=1
				for(int i=1;i<r.size();i++)
					record.add(Double.parseDouble(r.get(i)));
				matrix.add(record);
			}
		}
		return matrix;
	}
	
	/**Fill in the matrix of normalized values from a list of list of double values.
	 * The column of timestamp values won't be considered.
	*/
	public ArrayList<ArrayList<Double>> buildNormalizedFromList(ArrayList<ArrayList<Double>> inputMatrix) {
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record;
		for(ArrayList<Double> array: inputMatrix) {
			record=new ArrayList<Double>();
			for(int j=0;j<array.size();j++) {
				record.add(array.get(j));
			}
			matrix.add(record);
		}
		return matrix;
	}
	
	/**Fill in the feature hashmap from a list of CSV records.
	 * Only single features will be considered, so feature combinations (i.e. with 'AND') will be excluded.
	*/
	public HashMap<Integer, String> getFeaturesMapFromDeltad(List<CSVRecord> list) {
		HashMap<Integer, String> features=new HashMap<Integer, String>();
		for(CSVRecord r: list) {		
			String f=(String) r.get(0).replace("deltad", "").replaceAll("\\(", "").replaceAll("\\)", ""); //take only feature name
			if(!f.contains("AND")) {
				int mSize=features.size();
				features.put(mSize+2, f); //keys start from 2
			}
		}
		return features;
	}
	
	/**Fill in the feature hashmap from a list of labels
	 * Only single features will be considered, so feature combinations (i.e. with 'AND') will be excluded.
	*/
	public HashMap<Integer, String> getFeaturesMap(ArrayList<String> labels) {
		HashMap<Integer, String> features=new HashMap<Integer, String>();
		for(String s: labels) {
			if(!s.contains("AND")) {
				int mSize=features.size();
				features.put(mSize+2, s); //keys start from 2
			}
		}
		return features;
	}
	
	/**Fill in the deltad hashmap from a list of CSV records.
	 * Will be considered only that values which are greater than 0 and whose feature is in the features hashmap.*/
	public  HashMap<String, Double> getValuesMapFromDeltad(List<CSVRecord> list) {
		HashMap<String, Double> map=new HashMap<String, Double>();
		ArrayList<String> toExclude=new ArrayList<String>(); //list of excluded features
	    boolean excluded=false;
	    boolean isFound=false;
		for(CSVRecord r: list) {
			String feature=(String) r.get(0).replace("deltad", "").replaceAll("\\(", "").replaceAll("\\)", ""); //take only feature name
			excluded=false;
			isFound=false;
			for(int i=0;i<toExclude.size() && !isFound;i++) {
				if(feature.contains(toExclude.get(i))) {
					toExclude.add(feature);
					excluded=true;
					isFound=true;
				}
			}
			double d=Math.floor(Double.parseDouble(r.get(1))); //floor of deltad value
			if(d >= 0 && !excluded) {
				map.put(feature, d);
			}
			else if(!excluded)
				toExclude.add(feature);
		}
		return map;
	}
	
	/**Fill in the deltad hashmap from a list of minpts values taken as strings.*/
	public  HashMap<String, Double> getDeltadMap(ArrayList<String> labels, ArrayList<String> minpts) {
		HashMap<String, Double> map=new HashMap<String, Double>();
		for(int i=0;i<labels.size();i++)
			map.put(labels.get(i), Double.parseDouble(minpts.get(i)));
		return map;
	}
	
	/**
	 * Fill in the cells hashmap with the cells of a cluster.
	 * Each entry of the map will be a couple: key, list_of_triple (cell_id, lat, lng).
	*/
	public HashMap<Integer, ArrayList<ArrayList<Double>>> putCompleteCellsOfCluster(
										HashMap<Integer, ArrayList<ArrayList<Double>>> cellsOfCluster, 
										Cluster<?> cluster, 
										int index, 
										List<CSVRecord> listDens) {
		int cellId=0;
		String cellLat="";
		String cellLng="";
		ArrayList<Double> cellRecord;
		ArrayList<ArrayList<Double>> cells=new ArrayList<ArrayList<Double>>();
		
		for(DBIDIter iter=cluster.getIDs().iter(); iter.valid(); iter.advance()) {
			cellId=Integer.parseInt(DBIDUtil.toString(iter));
			cellLat=listDens.get(cellId).get(1); //latitude
			cellLng=listDens.get(cellId).get(2); //longitude
			cellRecord=new ArrayList<Double>();
			cellRecord.add((double) cellId);
			cellRecord.add(Double.parseDouble(cellLat));
			cellRecord.add(Double.parseDouble(cellLng));
			cells.add(cellRecord); //add a triple: id, lat, lng
		}
		cellsOfCluster.put(index, cells);
		
		return cellsOfCluster;
	}
	
	/**
	 * Fill in the cells hashmap with the cell_ids of a cluster.
	 * Each entry of the map will be a couple: key, list of cell_ids.
	*/
	public HashMap<Integer, ArrayList<Integer>> putIdCellsOfCluster(HashMap<Integer, ArrayList<Integer>> cellsOfCluster, Cluster<?> cluster, int index) {
		int cellId=0;
		ArrayList<Integer> cells=new ArrayList<Integer>();
		for(DBIDIter iter=cluster.getIDs().iter(); iter.valid(); iter.advance()) {
			cellId=Integer.parseInt(DBIDUtil.toString(iter));
			cells.add(cellId); //add a triple: id, lat, lng
		}
		cellsOfCluster.put(index, cells);
		return cellsOfCluster;
	}
	
	/**
	 * Fill in the venues hashmap with the venues of the cells of a cluster.
	 * Each entry of the map will be a couple: key=clusterId, list of lists of venue_info.
	 * Only venues of the same label of the cluster will be included.
	*/
	public  HashMap<Integer, ArrayList<ArrayList<String>>> putVenuesOfCells(
														String clusterName,
														int index,
														HashMap<Integer, ArrayList<ArrayList<String>>> venuesOfCell, 
														ArrayList<ArrayList<Double>> cells, 
														List<CSVRecord> listSingles) {

		double cellLat = 0.0;
		double cellLng = 0.0;
		ArrayList<ArrayList<String>> venuesInfo=new ArrayList<ArrayList<String>>();
		ArrayList<String> venueRecord;
	
		//clean cluster name
		String str= clusterName.substring(2, clusterName.length()-1); //keep only category names
		String[] str_array= str.split(","); //all labels of the cluster
	
	
		//get the header of singles in order to get the correct venue category name
		ArrayList<String> features=new ArrayList<String>();
		for(int i=7;i<listSingles.get(0).size();i++)
			features.add(listSingles.get(0).get(i));
		
		for(ArrayList<Double> array: cells) {
			cellLat=array.get(1);
			cellLng=array.get(2);
			
			//go through the venue dataset. i=1 because we don't have to consider the header
			for(int i=1;i<listSingles.size();i++) {
			
				//check if the venue belong to the cell
				CSVRecord r = listSingles.get(i);
				
				Double f_lat=Double.parseDouble(r.get(5));
				Double f_lng=Double.parseDouble(r.get(6));
				
				if(f_lat==cellLat && f_lng==cellLng) {
					venueRecord=getVenueRecord(str_array, features, r);
					if(venueRecord.size()>0) {
						venuesInfo.add(venueRecord);
					}
				}
			}
		}
		//add venue_id only if the venue exists in the cell
		if(venuesInfo.size()>0)
			venuesOfCell.put(index, venuesInfo);
						
		return venuesOfCell;
	}
	
	/**
	 * Get the infos of a venue record.
	 * This method is exploit by the method putVenuesOfCell
	*/
	public ArrayList<String> getVenueRecord(String[] str_array, 
											ArrayList<String> features,
											CSVRecord r) {
	
		ArrayList<String> venueRecord=new ArrayList<String>();
		String venueLabel=checkVenueLabel(r, str_array, features);
		
		if(venueLabel!=null) {
			venueRecord.add(r.get(0)); //timestamp
			venueRecord.add(r.get(1)); //beenHere
			venueRecord.add(r.get(2)); //venue id
			venueRecord.add(r.get(3)); //venue lat
			venueRecord.add(r.get(4)); //venue lat
			venueRecord.add(r.get(5)); //focal lat
			venueRecord.add(r.get(6)); //focal lng
			venueRecord.add(venueLabel); //category of the venue
		}
		
		return venueRecord;
	}
	
	/**
	 * Check whether a venue has the same label of the cluster.
	 * This method is exploit by getVenueRecord 
	*/
	public String checkVenueLabel(CSVRecord r, String[] str_array, ArrayList<String> features) {

		boolean found = false;
		boolean matched = false;
		String venueLabel = null;
		
		for(int h=7;h<r.size() && !matched;h++) {
		
			if(r.get(h).equals("1")) {
				matched=true;
		
				//keep only venues of the same labels of the cluster
				for(int k=0; k<str_array.length && !found; k++) {
					if(features.get(h-7).equals(str_array[k].trim())) {
						venueLabel=features.get(h-7);
						found=true;
					}
				}
			}
		}
		
		return venueLabel;
	}
	
	/*public  HashMap<Integer, ArrayList<ArrayList<String>>> putVenuesOfCells(String clusterName, HashMap<Integer, ArrayList<ArrayList<String>>> venuesOfCell, ArrayList<ArrayList<Double>> cells, List<CSVRecord> listSingles) {
		
		int cellId=0;
		String cellLat="";
		String cellLng="";
		String venueLabel="";
		ArrayList<ArrayList<String>> venuesInfo;
		ArrayList<String> venueRecord;
		boolean found=false;
		boolean added=false;
		boolean catFound=false;
		
		//clean cluster name
		String str= clusterName.substring(2, clusterName.length()-1); //keep only category names
		String[] str_array= str.split(",");
		
		
		//get the header of singles in order to get the correct venue category name
		ArrayList<String> features=new ArrayList<String>();
		for(int i=7;i<listSingles.get(0).size();i++)
			features.add(listSingles.get(0).get(i));
		
		//get the venues
		for(ArrayList<Double> array: cells) {
			cellId=array.get(0).intValue();
			cellLat=array.get(1)+"";
			cellLng=array.get(2)+"";
			venuesInfo=new ArrayList<ArrayList<String>>();
			found=false;
			added=false;
			for(int i=0;i<listSingles.size() && !found;i++) {
				CSVRecord r=listSingles.get(i); //venue information
				//we don't have to consider the header
				if(!r.get(0).contains("Timestamp")) {
					
					venueLabel="";
					catFound=false;
					//check if the venue has the same label of the cluster
					for(int h=7;h<r.size() && !catFound;h++) {
						if(r.get(h).equals("1.0")) {
							//keep only venues of the same labels of the cluster
							for(String s: str_array) {
								if(features.get(h-7).equals(s.trim())) {
									venueLabel=features.get(h-7);
									catFound=true;
								}
							}
						}
					}
					
					//check if the venue belong to the cell
					if(r.get(5).equals(cellLat) && r.get(6).equals(cellLng) && catFound) {
						venueRecord=new ArrayList<String>();
						venueRecord.add(r.get(0)); //timestamp
						venueRecord.add(r.get(1)); //beenHere
						venueRecord.add(r.get(2)); //venue id
						venueRecord.add(r.get(3)); //venue lat
						venueRecord.add(r.get(4)); //venue lat
						venueRecord.add(r.get(5)); //focal lat
						venueRecord.add(r.get(6)); //focal lng
						venueRecord.add(venueLabel); //category of the venue
						
						venuesInfo.add(venueRecord); //add the venue informations
						added=true;
					} else if(added) 
						found=true; //since venues of the same cell are consecutive, we stop the loop once we found different focal coordinate values
				}
			}
			//add venue_id only if the venue exists in the cell
			if(added)
				venuesOfCell.put(cellId, venuesInfo);
		}
		
		return venuesOfCell;
	}*/
	
	/**
	 * Get the Calendar of a timestamp value from a list of CSV records.  
	*/
	public Calendar getCalendar(List<CSVRecord> list) {
		Calendar cal=GregorianCalendar.getInstance();
		long timestamp=Long.parseLong(list.get(1).get(0)); //get the timestamp
		Date d=new Date(timestamp);
		cal.setTime(d);
		return cal;
	}
	
	/**
	 * Get a properly eps value for the given dataset 
	*/
	public double getEps(ArrayList<ArrayList<Double>> dataset) {
		double side=Math.sqrt(dataset.size());
		double eps=Math.sqrt(2)*(1/side);
		return eps;
	}
	
	/**
	 * Get all the distinct cluster labels.
	 */
	public TreeSet<String> getClusterLabels(HashMap<Integer, String> clusters) {
		ArrayList<Integer> keys=new ArrayList<Integer>(clusters.keySet());
	    TreeSet<String> tree =new TreeSet<String>();
	    for(Integer i: keys) {
	    	tree.add(clusters.get(i));
	    }
	    return tree;
	}
	
	/**
	 * Get all the cell corresponding to the cluster label.
	 */
	public ArrayList<TreeSet<Integer>> getCellsOfClusters(HashMap<Integer, String> clusters, HashMap<Integer, ArrayList<Integer>> cells, TreeSet<String> tree) {
	    Iterator<String> iter=tree.iterator();
	    ArrayList<Integer> keys=new ArrayList<Integer>(clusters.keySet());
	    ArrayList<TreeSet<Integer>> allCells =new ArrayList<TreeSet<Integer>>();
	    TreeSet<Integer> cellIndex;
	    String label="";
	    while(iter.hasNext()) {
	    	label=iter.next();
	    	cellIndex=new TreeSet<Integer>();
	    	for(Integer i: keys) {
	    		if(clusters.get(i).equals(label)) {
	    			cellIndex.addAll(cells.get(i));
	    		}
	    	}
	    	allCells.add(cellIndex);
	    }
	    return allCells;
	}
	
	/**
	 * Create an hashmap for the holdout used to compute Jaccard evaluation.
	 * each entry of the map will be a couple: cluster_name, list_of_cells 
	*/
	public  HashMap<String, Vector<Integer>> buildHoldoutMap(TreeSet<String> tree, ArrayList<TreeSet<Integer>> cells, int length) {
		HashMap<String, Vector<Integer>> holdout=new HashMap<String, Vector<Integer>>();
	    Vector<Integer> vector;
	    Iterator<String> distinctIter=tree.iterator();
        Iterator<TreeSet<Integer>> cellsIter=cells.iterator();
        while(distinctIter.hasNext() && cellsIter.hasNext()) {
        	String label=distinctIter.next();
        	Iterator<Integer> treeIter = cellsIter.next().iterator();
        	vector=new Vector<Integer>();
        	while(treeIter.hasNext()) {
        		vector.add(treeIter.next()-length);
        	}
        	holdout.put(label, vector);
        }
        
        return holdout;
	}
	
	/**Set SUBCLU parameters and run the algorithm*/
    public Clustering<?> runSUBCLU (Database db, double eps, int minpts) {
        ListParameterization params = new ListParameterization();
        params.addParameter(SUBCLU.EPSILON_ID, eps);
        params.addParameter(SUBCLU.MINPTS_ID, minpts);
        
        // setup algorithm
        SUBCLU<DoubleVector> subclu = ClassGenericsUtil.parameterizeOrAbort(SUBCLU.class, params);

        // run SUBCLU on database
        Clustering<SubspaceModel<DoubleVector>> result = subclu.run(db);
        return result;
    }
	
	/**Set GEOSUBCLU parameters and run the algorithm*/
    public Clustering<?> runGEOSUBCLU (
										Database db,
										ArrayList<Pair<Double,Double>> boundaries,
										HashMap<Integer, 
										String> map, 
										HashMap<String, 
										Double>deltad, 
										int density, 
										double eps, 
										StringBuilder sb
    								) 
    {
        ListParameterization params = new ListParameterization();
        
        // setup algorithm
        GEOSUBCLU<DoubleVector> geosubclu = ClassGenericsUtil.parameterizeOrAbort(GEOSUBCLU.class, params);
        geosubclu.setFeatureMapper(map);
        geosubclu.setDeltad(deltad);
        geosubclu.setDensity(density);
        geosubclu.setEpsValue(eps);
        geosubclu.setSbLog(sb);
        geosubclu.setBoundaries(boundaries);

        // run GEOSUBCLU on database
        Clustering<SubspaceModel<DoubleVector>> result = geosubclu.run(db);
        this.log=geosubclu.getSbLog();
        
        return result;
    }
	
	/**Build a Database from the matrix of normalized density values*/
    public <T> Database buildDatabaseFromMatrix (ArrayList<ArrayList<Double>> matrix) {       
        double[][] data = new double[matrix.size()][];
        for (int i=0; i<matrix.size(); i++) {
            data[i] = new double[matrix.get(i).size()];
            for(int j=0; j<matrix.get(i).size(); j++) {
                data[i][j] = (matrix.get(i)).get(j);
            }
        }
        
        List<Class<?>> filterlist = new ArrayList<>();
        filterlist.add(FixedDBIDsFilter.class);
        Database db = new InMemoryDatabase(new ArrayAdapterDatabaseConnection(data), null);        
        db.initialize();
        return db;
    }
    
    @SuppressWarnings("unchecked")
	public <V extends NumberVector<?>> double getDistance(	
											Database db, 
											Cluster<?> cluster,
											HashMap<Integer, String> featuresMap, 
											List<CSVRecord> listDens 
										) 
	{
	
    	double distance=0.0;
    	Iterator<Relation<?>> iter=db.getRelations().iterator();
		iter.next();
		Relation<V> relation=(Relation<V>) iter.next();
	    
 		double sum_distance = 0.0;
    	double total_number = 0.0;
    			
		Vector<Integer> dimensions = getDimensions(cluster.getName(), featuresMap);  	
    	
		for (DBIDIter i1 = cluster.getIDs().iter(); i1.valid(); i1.advance()) 
		{
			V o1 = relation.get(i1);	
			int cellId1 = Integer.parseInt(DBIDUtil.toString(i1));
			double lat1 = Double.parseDouble( listDens.get(cellId1).get(1) ); //latitude
			double lng1 = Double.parseDouble( listDens.get(cellId1).get(2) ); //longitude
			
			for (DBIDIter i2 = cluster.getIDs().iter(); i2.valid(); i2.advance()) 
			{
				V o2 = relation.get(i2);
				int cellId2 = Integer.parseInt(DBIDUtil.toString(i2));
				double lat2 = Double.parseDouble( listDens.get(cellId2).get(1) ); //latitude
				double lng2 = Double.parseDouble( listDens.get(cellId2).get(2) ); //longitude
				
				double sum = 0.0;
				
				for (int i=0; i<dimensions.size(); i++) {
					double d1 = o1.doubleValue(dimensions.get(i));
					double d2 = o2.doubleValue(dimensions.get(i));
					
					ImportTools tools = new ImportTools();
					//sum += d1*d2* ( tools.getDistance(lat1, lng1, lat2, lng2) ); //convert to Km
					sum += tools.getDistance(lat1, lng1, lat2, lng2);
				}
				
				sum_distance += sum;
			}
			
			total_number++; //total number of points in a cluster
		}
		distance+= sum_distance * 1/(2*total_number);
		
    	return distance;
    }
    
    /*
     *  Get SSE cluster
     */
    @SuppressWarnings("unchecked")
	public <V extends NumberVector<?>> double getClusterSSE(
										Database db, 
										Cluster<?> cluster,
										HashMap<Integer, String> featuresMap ) 
    {
    	double sse=0.0;
    	Iterator<Relation<?>> iter=db.getRelations().iterator();
		iter.next();
		Relation<V> relation=(Relation<V>) iter.next();
	    
 		double sum_distance = 0.0;
    	double total_number = 0.0;
    			
		Vector<Integer> dimensions = getDimensions(cluster.getName(), featuresMap);  	
    	
		for (DBIDIter i1 = cluster.getIDs().iter(); i1.valid(); i1.advance()) 
		{
			V o1 = relation.get(i1);	
			for (DBIDIter i2 = cluster.getIDs().iter(); i2.valid(); i2.advance()) 
			{
				V o2 = relation.get(i2);
				double sum_squared = 0.0;
				
				for (int i=0; i<dimensions.size(); i++) {
					double d1 = o1.doubleValue(dimensions.get(i));
					double d2 = o2.doubleValue(dimensions.get(i));
					
					double dist_lat_pow2 = Math.pow(o1.doubleValue(0) - o2.doubleValue(0), 2);
					double dist_lng_pow2 = Math.pow(o1.doubleValue(1) - o2.doubleValue(1), 2);
					
					sum_squared += d1*d2*dist_lat_pow2 + d1*d2*dist_lng_pow2;
				}
				
//				int dimension = o1.getDimensionality();
//				for (int i=0; i<dimension; i++) 
//				{	
//					double d1 = o1.doubleValue(i);
//					double d2 = o2.doubleValue(i);
//					sum_squared += (d1-d2)*(d1-d2);
//				}
//				for (Integer i : dimensions) 
//				{
//					double d1 = o1.doubleValue(i);
//					double d2 = o2.doubleValue(i);
//					sum_squared += (d1-d2)*(d1-d2);
//				}
				sum_distance += sum_squared;
			}
			
//			for (int i=2; i<dimensions.size(); i++){
//				total_number += o1.doubleValue(dimensions.get(i));
//			}
			total_number++; //total number of points in a cluster
		}
		sse+= sum_distance * 1/(2*total_number);
		
    	return sse;
    }
    
	/**
     * Get the SSE value of the clustering
     * @param featuresMap 
    */
	public <V extends NumberVector<?>> double getClusteringSSE(
									Database db, 
									ArrayList<Clustering<?>> cs, 
									HashMap<Integer, String> featuresMap
									) 
    {
    	double sse=0.0;
    	//int nClusters = 0;
    		
	    for(Clustering<?> c: cs)     	
	    	for(Cluster<?> cluster: c.getAllClusters()) 
	    	{
    			//nClusters ++;
	    		sse += getClusterSSE(db, cluster, featuresMap);
	    	}
	    //return sse/nClusters;
	    return sse;
    }
    
	private static <V, K> Map<V, K> invert(Map<K, V> map) 
	{
	    Map<V, K> inv = new HashMap<V, K>();

	    for (Entry<K, V> entry : map.entrySet())
	        inv.put(entry.getValue(), entry.getKey());

	    return inv;
	}
	
    private Vector<Integer> getDimensions(String clusterName, HashMap<Integer, String> featuresMap) 
    {
    	Vector<Integer> dimensions = new Vector<>();
    	
    	Map<String, Integer> inv = invert(featuresMap);
		String[] features = clusterName.split(",");
		for (String feature : features) {
			String feat = feature
							.replace("c(", "")
							.replace(")", "")
							.trim();
			dimensions.add(inv.get(feat));
		}
		return dimensions;
	}

	public Double getClusterSurface(Cluster<?> cluster, int size) 
	{
		int objects = 0;
		for (DBIDIter i1 = cluster.getIDs().iter(); i1.valid(); i1.advance()) 
			objects ++;
		
		return objects/(1.0 * size);
	}

	public Double getClusterHeterogeneity(Cluster<?> cluster,
										  HashMap<Integer, String> featuresMap)
	{
		int cCategories = cluster.getName().split(",").length;
		return cCategories/(1.0*featuresMap.size());
	}

	public Double getClusterDensity(int i,  double surfaceTotal, Double surfaceClusterPercentage) 
	{
		return i/(1.0 * surfaceClusterPercentage * surfaceTotal);
	}

	public ArrayList<Pair<Double, Double>> getFeatureBoundariesFromCSV(List<CSVRecord> list) 
	{
		ArrayList<Pair<Double,Double>> result=new ArrayList<Pair<Double, Double>>();
		
		//get size of the first element
		int featureNumber = list.get(0).size();
		
		boolean header = (list.get(0).get(0).contains("Timestamp")) ? true : false;
		
		//let's discard the first feature, timestamp not yet used 
		for (int j=1; j<featureNumber; j++)
		{
			Vector<Double> v = new Vector<>();
			int i = (header) ? 1 : 0;
			for (;i<list.size(); i++) 
				v.add(Double.parseDouble( list.get(i).get(j) ));
				
			Collections.sort(v);
			result.add( new Pair<Double, Double>(v.get(0), v.get(v.size()-1)) );
		}
		return result;
	}
}