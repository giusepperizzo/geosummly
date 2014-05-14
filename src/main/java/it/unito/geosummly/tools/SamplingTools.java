package it.unito.geosummly.tools;

import it.unito.geosummly.io.templates.FoursquareObjectTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 *
 * Tools for the sampling state
 *   
 */

public class SamplingTools {
	private int total;
	private ArrayList<String> singlesId;
	private ArrayList<Long> singlesTimestamps;
	private ArrayList<Long> cellsTimestamps;
	private ArrayList<Integer> beenHere;
	private HashMap<String, Integer> map;
	private int totalSecondLevel;
	private HashMap<String, Integer> mapSecondLevel;
	private ArrayList<ArrayList<Double>> matrixSecondLevel;
	
	public static Logger logger = Logger.getLogger(SamplingTools.class.toString());
	
	public SamplingTools() {
		this.total=0;
		this.singlesId=new ArrayList<String>();
		this.singlesTimestamps=new ArrayList<Long>();
		this.cellsTimestamps=new ArrayList<Long>();
		this.beenHere=new ArrayList<Integer>();
		this.map=new HashMap<String, Integer>();
		this.totalSecondLevel=0;
		this.mapSecondLevel=new HashMap<String, Integer>();
		this.matrixSecondLevel=new ArrayList<ArrayList<Double>>();
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public HashMap<String, Integer> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}
	
	public int getTotalSecondLevel() {
		return totalSecondLevel;
	}

	public void setTotalSecondLevel(int totalSecondLevel) {
		this.totalSecondLevel = totalSecondLevel;
	}
	
	public HashMap<String, Integer> getMapSecondLevel() {
		return mapSecondLevel;
	}

	public void setMapSecondoLevel(HashMap<String, Integer> mapSecondLevel) {
		this.mapSecondLevel = mapSecondLevel;
	}
	
	public ArrayList<String> getSinglesId() {
		return singlesId;
	}
	
	public void setSinglesId(ArrayList<String> singlesId) {
		this.singlesId=singlesId;
	}
	
	public ArrayList<Long> getSinglesTimestamps() {
		return singlesTimestamps;
	}
	
	public void setSinglesTimestamps(ArrayList<Long> singlesTimestamps) {
		this.singlesTimestamps=singlesTimestamps;
	}
	
	public ArrayList<Long> getCellsTimestamps() {
		return cellsTimestamps;
	}
	
	public void setCellsTimestamps(ArrayList<Long> cellsTimestamps) {
		this.cellsTimestamps=cellsTimestamps;
	}
	
	public ArrayList<Integer> getBeenHere() {
		return beenHere;
	}
	
	public void setBeenHere(ArrayList<Integer> beenHere) {
		this.beenHere=beenHere;
	}
	
	public ArrayList<ArrayList<Double>> getMatrixSecondLevel() {
		return matrixSecondLevel;
	}
	
	public void setMatrixSecondLevel(ArrayList<ArrayList<Double>> matrixSecondLevel) {
		this.matrixSecondLevel=matrixSecondLevel;
	}

	/**Get a list with all elements equal to zero*/
	public ArrayList<Double> buildListZero(int size) {
		ArrayList<Double> toRet=new ArrayList<Double>();
		int i=0;
		while(i<size) {
			toRet.add(0.0);
			i++;
		}
		return toRet;
	}
	
	/**Update the hash map given as parameter with new string values from a single venue*/
	public HashMap<String, Integer> updateMap(HashMap<String, Integer> map, ArrayList<String> categories) {
		
		if(!map.containsKey(categories.get(0)))
			map.put(categories.get(0), map.size()+2); //first value in the map has to be 2
		
		return map;
	}
	
	/**Get a row of the matrix with latitude, longitude and occurrence value of a single venue*/
	public ArrayList<Double> fillRowWithSingle(HashMap<String, Integer> map, String category, double lat, double lng) {
		int size=map.size()+2;
		ArrayList<Double> row=buildListZero(size);
		row.set(0, lat); //lat, lng and area are in position 0 and 1
		row.set(1, lng);
		int index=map.get(category);
		row.set(index, 1.0);
		return row;
	}
	
	/**Fix the matrix rows giving them the same size*/
	public ArrayList<ArrayList<Double>> fixRowsLength(int totElem, ArrayList<ArrayList<Double>> matrix) {
		int i;
		for(ArrayList<Double> row: matrix) {
			i=row.size();
			while(i<totElem) {
				row.add(0.0);
				i++;
			}
		}
		return matrix;
	}
	
	/**Sort matrix of single venues in alphabetical order (column names)*/
	public ArrayList<ArrayList<Double>> sortMatrixSingles(ArrayList<ArrayList<Double>> matrix, HashMap<String,Integer> map) {
		ArrayList<ArrayList<Double>> sortedMatrix=new ArrayList<ArrayList<Double>>();
		int value;
		ArrayList<Double> sortedRecord;
		ArrayList<String> keys=new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		
		for(ArrayList<Double> row: matrix) {
			sortedRecord=new ArrayList<Double>();
			sortedRecord.add(row.get(0));
			sortedRecord.add(row.get(1));
			sortedRecord.add(row.get(2));
			sortedRecord.add(row.get(3));
			for(String k: keys) {
				value=map.get(k)+2;
				sortedRecord.add(row.get(value));
			}
			sortedMatrix.add(sortedRecord);
		}
		return sortedMatrix;
	}
	
	/**Get the informations of single venues of a cell*/
	public ArrayList<ArrayList<Double>> getInformations(double lat, 
						double lng, ArrayList<ArrayList<Double>> matrix, 
						ArrayList<FoursquareObjectTemplate> cell,
						HashMap<String, String> tree) {
		
		ArrayList<Double> rowOfMatrix=new ArrayList<Double>();
		ArrayList<Double> rowOfMatrixSecondLevel=new ArrayList<Double>();
		
		for(FoursquareObjectTemplate venue: cell) {
			
			if(venue.getCategories().length > 0) {
				String category = getTopCategory(venue.getCategories()[0].getName(), tree);
				String categorySecondLevel = getSubCategory(venue.getCategories()[0].getName(), tree);
				
				if(category != null) { //update the matrix only if the category has a name
					ArrayList<String>aux=new ArrayList<String>();
					aux.add(category);
					ArrayList<String> auxSecondLevel=new ArrayList<String>();
					auxSecondLevel.add(categorySecondLevel);
					updateMap(this.map, aux);//update the hash map
					updateMap(this.mapSecondLevel, auxSecondLevel);
					rowOfMatrix=fillRowWithSingle(this.map, category, lat, lng); //create a consistent row (related to the categories). row of the transformation matrix (one for each venue);
					rowOfMatrixSecondLevel=fillRowWithSingle(this.mapSecondLevel, categorySecondLevel, lat, lng);
					if(this.total<rowOfMatrix.size())
						this.total=rowOfMatrix.size(); //update the overall number of categories
					if(this.totalSecondLevel<rowOfMatrixSecondLevel.size());
						this.totalSecondLevel=rowOfMatrixSecondLevel.size();
					rowOfMatrix.add(0, venue.getLatitude());
					rowOfMatrix.add(1, venue.getLongitude());
					rowOfMatrixSecondLevel.add(0, venue.getLatitude());
					rowOfMatrixSecondLevel.add(1, venue.getLongitude());
					this.singlesId.add(venue.getVenueId()); //memorize venue id
					this.singlesTimestamps.add(venue.getTimestamp()); //memorize timestamp
					this.beenHere.add(venue.getCheckinsCount()); //memorize check-ins count
					matrix.add(rowOfMatrix);
					this.matrixSecondLevel.add(rowOfMatrixSecondLevel);
				}
			}
		}
		return matrix;
	}
	
	/**
	 * Get the corresponding top category of the category "name" 
	*/
	public String getTopCategory(String name, HashMap<String, String> map) {
		
		String tmp=map.get(name); //Get the value corresponding to the key "name"
		String category=map.get(tmp); //Check for more general category of the previous value
		if(category == null)
			return tmp;
		else
			return category;
	}
	
	/**
	 * Get the corresponding sub category of the category "name" 
	*/
	public String getSubCategory(String name, HashMap<String, String> map) {
		
		String tmp=map.get(name); //Get the value corresponding to the key "name"
		String category=map.get(tmp); //Check for more general category of the previous value
		if(category == null)
			return name;
		else
			return tmp;
	}
	
	
	/**Sort the features in alphabetical order*/
	public ArrayList<String> sortFeatures(HashMap<String,Integer> map) {
		ArrayList<String> sortedFeatures=new ArrayList<String>();
		ArrayList<String> keys= new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		sortedFeatures.add("Latitude");
		sortedFeatures.add("Longitude");
		for(String s: keys)
			sortedFeatures.add(s);
		return sortedFeatures;
	}
	
	/**Get the features list for the dataset of single venues*/
	public ArrayList<String> getFeaturesForSingles(ArrayList<String> features) {
		features.add(0, "Timestamp (ms)");
		features.add(1, "Been Here");
		features.add(2, "Venue Id");
		features.add(3, "Venue Latitude");
		features.add(4, "Venue Longitude");
		features.set(5, "Focal Latitude");
		features.set(6, "Focal Longitude");
		return features;
	}
}
