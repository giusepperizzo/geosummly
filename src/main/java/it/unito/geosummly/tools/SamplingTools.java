package it.unito.geosummly.tools;

import it.unito.geosummly.io.templates.FoursquareObjectTemplate;

import java.math.BigDecimal;
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
	
	//Variables of the 1st category level
	private int total;
	private ArrayList<String> ids;
	private ArrayList<Long> timestamps;
	private ArrayList<Integer> beenHere;
	private HashMap<String, Integer> map;
	
	//Variables of the 2nd category level
	private int totalSecond;
	private ArrayList<String> idsSecond;
	private ArrayList<Long> timestampsSecond;
	private ArrayList<Integer> beenHereSecond;
	private HashMap<String, Integer> mapSecond;
	private ArrayList<ArrayList<BigDecimal>> matrixSecond;
	
	public static Logger logger = Logger.getLogger(SamplingTools.class.toString());
	
	public SamplingTools() {
		
		this.total = 0;
		this.ids = new ArrayList<String>();
		this.timestamps = new ArrayList<Long>();
		this.beenHere = new ArrayList<Integer>();
		this.map = new HashMap<String, Integer>();
		
		this.totalSecond = 0;
		this.idsSecond = new ArrayList<String>();
		this.timestampsSecond = new ArrayList<Long>();
		this.beenHereSecond = new ArrayList<Integer>();
		this.mapSecond = new HashMap<String, Integer>();
		this.matrixSecond = new ArrayList<ArrayList<BigDecimal>>();
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
	
	public ArrayList<String> getIds() {
		return ids;
	}
	
	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
	
	public ArrayList<Long> getTimestamps() {
		return timestamps;
	}
	
	public void setTimestamps(ArrayList<Long> timestamps) {
		this.timestamps = timestamps;
	}
	
	public ArrayList<Integer> getBeenHere() {
		return beenHere;
	}
	
	public void setBeenHere(ArrayList<Integer> beenHere) {
		this.beenHere=beenHere;
	}
	
	public int getTotalSecond() {
		return totalSecond;
	}
	
	public void setTotalSecond(int totalSecond) {
		this.totalSecond = totalSecond;
	}
	
	public ArrayList<String> getIdsSecond() {
		return idsSecond;
	}
	
	public void setIdsSecond(ArrayList<String> idsSecond) {
		this.idsSecond = idsSecond;
	}

	public ArrayList<Long> getTimestampsSecond() {
		return timestampsSecond;
	}
	
	public void setTimestampsSecond(ArrayList<Long> timestampsSecond) {
		this.timestampsSecond = timestampsSecond;
	}

	public ArrayList<Integer> getBeenHereSecond() {
		return beenHereSecond;
	}

	public void setBeenHereSecond(ArrayList<Integer> beenHereSecond) {
		this.beenHereSecond = beenHereSecond;
	}

	public HashMap<String, Integer> getMapSecond() {
		return mapSecond;
	}

	public void setMapSecond(HashMap<String, Integer> mapSecond) {
		this.mapSecond = mapSecond;
	}

	public ArrayList<ArrayList<BigDecimal>> getMatrixSecond() {
		return matrixSecond;
	}

	public void setMatrixSecond(ArrayList<ArrayList<BigDecimal>> matrixSecond) {
		this.matrixSecond = matrixSecond;
	}

	/**Get a list with all elements equal to zero*/
	public ArrayList<BigDecimal> buildListZero(int size) {
		
		ArrayList<BigDecimal> toRet=new ArrayList<BigDecimal>();
		int i=0;
		
		while(i<size) {
			toRet.add(new BigDecimal(0.0));
			i++;
		}
		
		return toRet;
	}
	
	/**Update the hash map given as parameter 
	 * with new string values from a single venue
	*/
	public HashMap<String, Integer> updateMap(HashMap<String, Integer> map, 
											  ArrayList<String> categories) {
		
		if(!map.containsKey(categories.get(0)))
			//first value in the map has to be 2
			map.put(categories.get(0), map.size()+2);
		
		return map;
	}
	
	/**Get a row of the matrix with latitude, longitude 
	 * and occurrence value of a single venue
	*/
	public ArrayList<BigDecimal> fillRowWithSingle(HashMap<String, Integer> map, 
											   	   String category, 
											   	   BigDecimal lat, 
											   	   BigDecimal lng) {
		
		int size=map.size()+2;
		ArrayList<BigDecimal> row=buildListZero(size);
		row.set(0, lat); //lat, lng and area are in position 0 and 1
		row.set(1, lng);
		
		int index=map.get(category);
		row.set(index, new BigDecimal(1.0));
		
		return row;
	}
	
	/**Fix the matrix rows giving them the same size*/
	public ArrayList<ArrayList<BigDecimal>> fixRowsLength(int totElem, 
												ArrayList<ArrayList<BigDecimal>> matrix) {
		
		int i;
		
		for(ArrayList<BigDecimal> row: matrix) {
			i=row.size();
			
			while(i<totElem) {
				row.add(new BigDecimal(0.0));
				i++;
			}
		}
		return matrix;
	}
	
	/**Sort matrix of single venues 
	 * in alphabetical order (column names)
	*/
	public ArrayList<ArrayList<BigDecimal>> sortMatrixSingles(
										ArrayList<ArrayList<BigDecimal>> matrix, 
										HashMap<String,Integer> map) {
		
		ArrayList<ArrayList<BigDecimal>> sortedMatrix = 
							new ArrayList<ArrayList<BigDecimal>>();
		int value;
		ArrayList<BigDecimal> sortedRecord;
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		
		//Put the coordinate values into the sorted record
		for(ArrayList<BigDecimal> row: matrix) {
			
			sortedRecord=new ArrayList<BigDecimal>();
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
	public ArrayList<ArrayList<BigDecimal>> getInformations(BigDecimal lat, 
												BigDecimal lng, 
									            ArrayList<ArrayList<BigDecimal>> matrix, 
									            ArrayList<FoursquareObjectTemplate> cell,
									            HashMap<String, String> tree) {
		
		ArrayList<BigDecimal> rowOfMatrix = 
								new ArrayList<BigDecimal>();
		ArrayList<BigDecimal> rowOfMatrixSecondLevel = 
									new ArrayList<BigDecimal>();
		
		for(FoursquareObjectTemplate venue: cell) {
			
			if(venue.getCategories().length > 0) {
				String category = 
						getTopCategory(venue.getCategories()[0].getName(), tree);
				String categorySecondLevel = 
						getSubCategory(venue.getCategories()[0].getName(), tree);
				
				//update the matrix only if the category has a name
				if(category != null) {
					
					ArrayList<String>aux = new ArrayList<String>();
					aux.add(category);
					
					updateMap(this.map, aux);//update the hash map
					
					//create a consistent row (related to the categories). 
					//one row for each venue;
					rowOfMatrix = fillRowWithSingle(this.map, category, lat, lng);
					
					//update the overall number of categories
					if(this.total < rowOfMatrix.size())
						this.total = rowOfMatrix.size();
					
					//add venue coordinates to the record
					rowOfMatrix.add(0, new BigDecimal(venue.getLatitude()));
					rowOfMatrix.add(1, new BigDecimal(venue.getLongitude()));
					
					this.ids.add(venue.getVenueId()); //memorize venue id
					this.timestamps.add(venue.getTimestamp()); //memorize timestamp
					this.beenHere.add(venue.getCheckinsCount()); //memorize check-ins count
					
					//add the complete record
					matrix.add(rowOfMatrix);
				}
				
				if(categorySecondLevel != null) {
					
					ArrayList<String> auxSecondLevel = new ArrayList<String>();
					auxSecondLevel.add(categorySecondLevel);
					updateMap(this.mapSecond, auxSecondLevel);
					
					rowOfMatrixSecondLevel = 
							fillRowWithSingle(this.mapSecond, categorySecondLevel, lat, lng);
					
					if(this.totalSecond < rowOfMatrixSecondLevel.size());
						this.totalSecond = rowOfMatrixSecondLevel.size();
					
					rowOfMatrixSecondLevel.add(0, new BigDecimal(venue.getLatitude()));
					rowOfMatrixSecondLevel.add(1, new BigDecimal(venue.getLongitude()));
					
					this.idsSecond.add(venue.getVenueId()); //memorize venue id
					this.timestampsSecond.add(venue.getTimestamp()); //memorize timestamp
					this.beenHereSecond.add(venue.getCheckinsCount()); //memorize check-ins count
					
					this.matrixSecond.add(rowOfMatrixSecondLevel);
				}
			}
		}
		return matrix;
	}
	
	/**
	 * Get the corresponding top category of the category "name".
	 * Totally there are 3 category levels. 
	*/
	public String getTopCategory(String name, HashMap<String, String> map) {
		
		//Get the value corresponding to the key "name"
		//2nd or 1st or null category level
		String tmp=map.get(name);
		
		
		if(tmp != null) { //2nd category level
			String category = map.get(tmp);
			
			if(category != null) //1st category level
				return category;
			else //already in the 1st category level
				return tmp;
		}	
		else //already in the 1st category level
			return name;
	}
	
	/**
	 * Get the corresponding sub category of the category "name".
	 * Totally there are 3 category levels. 
	*/
	public String getSubCategory(String name, HashMap<String, String> map) {
		
		//Get the value corresponding to the key "name"
		//2nd or 1st or null category level
		String tmp=map.get(name);
		
		if(tmp !=null) { //2nd category level
			String category = map.get(tmp); 
			
			if(category != null) //1st category level
				return tmp; //return the 2nd category level
			
			else // already in the 2nd category level
				return name;
		}
		else //already in the 1st category level, so return null
			return null;
	}
	
	
	/**Sort the features in alphabetical order*/
	public ArrayList<String> sortFeatures(HashMap<String,Integer> map) {
		
		ArrayList<String> sortedFeatures = new ArrayList<String>();
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		
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
