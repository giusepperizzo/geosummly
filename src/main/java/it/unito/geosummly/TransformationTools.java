package it.unito.geosummly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import fi.foyt.foursquare.api.entities.Category;

/**
 * @author Giacomo Falcone
 *
 * Tools for the transformation matrix creation
 *   
 */

public class TransformationTools {
	private int total;
	private ArrayList<String> singlesId;
	private ArrayList<Long> timestamps;
	private ArrayList<Integer> beenHere;
	private HashMap<String, Integer> map;
	private int totalSecondLevel;
	private HashMap<String, Integer> mapSecondLevel;
	private ArrayList<ArrayList<Double>> matrixSecondLevel;
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationTools() {
		this.total=0;
		this.singlesId=new ArrayList<String>();
		this.timestamps=new ArrayList<Long>();
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
	
	public ArrayList<Long> getTimestamps() {
		return timestamps;
	}
	
	public void setTimestamps(ArrayList<Long> timestamps) {
		this.timestamps=timestamps;
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
	
	/**Update the hash map given as parameter with new string values either from a cell or a single venue*/
	public HashMap<String, Integer> updateMap(InformationType type, HashMap<String, Integer> map, ArrayList<String> categories) {
		switch (type) {
		case SINGLE:
			if(!map.containsKey(categories.get(0)))
				map.put(categories.get(0), map.size()+2); //first value in the map has to be 2
			break;
		case CELL:
			for(String s: categories)
				if(!map.containsKey(s))
					map.put(s, map.size()+2); //first value in the map has to be 2
			break;
		}
		return map;
	}
	
	/**Get a row of the matrix with latitude, longitude and occurrence values for a cell*/
	public ArrayList<Double> fillRowWithCell(HashMap<String, Integer> map, ArrayList<Integer> occurrences, ArrayList<String> distincts, double lat, double lng) {
		int index=0;
		double value=0;
		int size=map.size()+2;
		ArrayList<Double> row=buildListZero(size);
		row.set(0, lat); //lat and lng are in position 0 and 1
		row.set(1, lng);
		Iterator<String> iterD=distincts.iterator();
		Iterator<Integer> iterO=occurrences.iterator();
		while(iterD.hasNext() && iterO.hasNext()) {
			index=map.get(iterD.next()); //get the category corresponding to its occurrence value
			value=(double) iterO.next();
			row.set(index, value); //put the occurrence value in the "right" position
		}
		return row;
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
	
	/**Get the total number of elements of a specific category*/
	public double getSum(ArrayList<ArrayList<Double>> matrix, int index) {
		double sum=0;
		for(ArrayList<Double> record: matrix) {
				sum+=record.get(index);
		}
		return sum;
	}
	
	/**Get the total number of elements of all the categories*/
	public ArrayList<Double> getSumArray(int start, ArrayList<ArrayList<Double>> matrix) {
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		for(int j=start; j<matrix.get(0).size(); j++) {
			sum=getSum(matrix, j);
			sumArray.add(sum);
		}
		return sumArray;
	}
	
	/**Get the min value of a column*/
	public double getMin(ArrayList<ArrayList<Double>> matrix, int index) {
		double min=1*Double.MAX_VALUE;
		double current;
		for(ArrayList<Double> record: matrix) {
			current=record.get(index);
			if(current<min)
				min=current;
		}
		return min;
	}
	
	/**Get min values of all the columns of the matrix*/
	public ArrayList<Double> getMinArray(ArrayList<ArrayList<Double>> matrix){
		ArrayList<Double> minArray=new ArrayList<Double>();
		for(int i=0; i<matrix.get(0).size(); i++) {
			double min=getMin(matrix, i);
			minArray.add(min); //max value of column j
		}
		return minArray;
	}
	
	/**Get the max value of a column*/
	public double getMax(ArrayList<ArrayList<Double>> matrix, int index) {
		double max=-1*Double.MAX_VALUE;
		double current;
		for(ArrayList<Double> record: matrix) {
			current=record.get(index);
			if(current>max)
				max=current;
		}
		return max;
	}
	
	/**Get max values of all the columns of the matrix*/
	public ArrayList<Double> getMaxArray(ArrayList<ArrayList<Double>> matrix){
		ArrayList<Double> maxArray=new ArrayList<Double>();
		for(int i=0; i<matrix.get(0).size(); i++) {
			double max=getMax(matrix, i);
			maxArray.add(max); //max value of column j
		}
		return maxArray;
	}
	
	/**Get an intra-feature normalized row of the matrix*/
	public ArrayList<Double> getIntraFeatureNormalization(ArrayList<Double> record, ArrayList<Double> sumArray) {
		ArrayList<Double> normalizedRecord=new ArrayList<Double>();
		double currentValue=0;
		double denominator=0;
		double normalizedValue=0;
		normalizedRecord.add(record.get(0)); //latitude
		normalizedRecord.add(record.get(1)); //longitude
		for(int j=2;j<record.size();j++) {
			currentValue=record.get(j);
			denominator=sumArray.get(j-2);
			
			if(denominator > 0) { //check if denominator is bigger than 0
				normalizedValue=currentValue/denominator; //intra-feature normalized value
			}
			else {
				normalizedValue=0;
			}
			normalizedRecord.add(normalizedValue);
		}
		return normalizedRecord;
	}
	
	/**Get an intra-feature normalized row of the matrix without considering lat and lng coordinates*/
	public ArrayList<Double> getIntraFeatureNormalizationNoCoord(ArrayList<Double> record, ArrayList<Double> sumArray) {
		ArrayList<Double> normalizedRecord=new ArrayList<Double>();
		double currentValue=0.0;
		double normalizedValue=0.0;
		double denominator=0.0;
		for(int j=0;j<record.size();j++) {
			currentValue=record.get(j); //get the value
			denominator=sumArray.get(j);
			if(denominator>0.0) {//check if denominator is bigger than 0
				normalizedValue=(currentValue/denominator); //intra-feature normalized value
			}
			else {
				normalizedValue=0.0;
			}
			normalizedRecord.add(normalizedValue);
		}
		return normalizedRecord;
	}
	
	/**Normalize a value in [0,1]*/
	public double normalizeValues(double min, double max, double c) {
		double norm_c=0;
		if(max!=0 || min!=0)
			norm_c=(c-min)/(max-min);
		return norm_c;
	}
	
	/**Normalize the values of a row in [0,1] with respect to their own  min and max values*/
	public ArrayList<Double> normalizeRow(CoordinatesNormalizationType type, ArrayList<Double> array, ArrayList<Double> minArray, ArrayList<Double> maxArray) {
		ArrayList<Double> normalizedArray=new ArrayList<Double>();
		double normalizedValue;
		double min=0;
		double max=0;
		switch (type) {
			case NORM:
				for(int i=0;i<array.size();i++) {
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
			case NOTNORM:
				normalizedArray.add(array.get(0)); //latitude
				normalizedArray.add(array.get(1)); //longitude
				for(int i=2;i<array.size();i++) {
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
			case MISSING:
				for(int i=0;i<array.size();i++) {
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
		}
		return normalizedArray;
	}
	
	/**Sort matrix in alphabetical order (column names)*/
	public ArrayList<ArrayList<Double>> sortMatrix(CoordinatesNormalizationType type, ArrayList<ArrayList<Double>> matrix, HashMap<String,Integer> map) {
		ArrayList<ArrayList<Double>> sortedMatrix=new ArrayList<ArrayList<Double>>();
		if(type.equals(CoordinatesNormalizationType.NORM) || type.equals(CoordinatesNormalizationType.NOTNORM)) {
			int value;
			ArrayList<Double> sortedRecord;
			ArrayList<String> keys=new ArrayList<String>(map.keySet());
			
			for(ArrayList<Double> row: matrix) {
				sortedRecord=new ArrayList<Double>();
				sortedRecord.add(row.get(0));
				sortedRecord.add(row.get(1));
				for(String k: keys) {
					value=map.get(k);
					sortedRecord.add(row.get(value));
				}
				sortedMatrix.add(sortedRecord);
			}
		}
		else if(type.equals(CoordinatesNormalizationType.MISSING)) {
			int value;
			ArrayList<Double> sortedRecord;
			ArrayList<String> keys=new ArrayList<String>(map.keySet());
			
			for(ArrayList<Double> row: matrix) {
				sortedRecord=new ArrayList<Double>();
				for(String k: keys) {
					value=map.get(k);
					sortedRecord.add(row.get(value));
				}
				sortedMatrix.add(sortedRecord);
			}
		}
		return sortedMatrix;
	}
	
	/**Get a matrix with density values */
	public ArrayList<ArrayList<Double>> buildDensityMatrix(CoordinatesNormalizationType type, ArrayList<ArrayList<Double>> matrix, ArrayList<Double> area) {
		ArrayList<ArrayList<Double>> densMatrix=new ArrayList<ArrayList<Double>>();
		if(type.equals(CoordinatesNormalizationType.NORM) || type.equals(CoordinatesNormalizationType.NOTNORM)) {
			ArrayList<Double> densRecord;
			for(int i=0;i<matrix.size();i++) {
				densRecord=new ArrayList<Double>();
				densRecord.add(matrix.get(i).get(0)); //latitude
				densRecord.add(matrix.get(i).get(1)); //longitude
				for(int j=2;j<matrix.get(i).size();j++) {
					densRecord.add(matrix.get(i).get(j)/area.get(i));
				}
				densMatrix.add(densRecord);
			}
		}
		else if(type.equals(CoordinatesNormalizationType.MISSING)) {
			ArrayList<Double> densRecord;
			for(int i=0;i<matrix.size();i++) {
				densRecord=new ArrayList<Double>();
				for(int j=0;j<matrix.get(i).size();j++) {
					densRecord.add(matrix.get(i).get(j)/area.get(i));
				}
				densMatrix.add(densRecord);
			}
		}
		return densMatrix;
	}
	
	/**Get a matrix normalized in [0,1]. Before normalization, densities are intra-feature normalized*/
	public ArrayList<ArrayList<Double>> buildNormalizedMatrix(CoordinatesNormalizationType type, ArrayList<ArrayList<Double>> matrix) {
		ArrayList<ArrayList<Double>> intraFeatureMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> normalizedMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sumArray;
		switch (type) {
			case MISSING:
				//get all the sums of the features values per column
				sumArray=getSumArray(0, matrix);
				
				//get an intra-feature normalized matrix
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord=getIntraFeatureNormalizationNoCoord(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
			case NORM:
				//get all the sums of the features values per column
				sumArray=getSumArray(2, matrix); // it starts from index 2 because first two are for lat and lng
				
				//get an intra-feature normalized matrix except for the first two columns (lat and lng)
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord=getIntraFeatureNormalization(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
			case NOTNORM:
				//get all the sums of the features values per column
				sumArray=getSumArray(2, matrix); // it starts from index 2 because first two are for lat and lng
				
				//get an intra-feature normalized matrix except for the first two columns (lat and lng)
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord=getIntraFeatureNormalization(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
		}
		//get the arrays of min and max values
		ArrayList<Double> minArray=getMinArray(intraFeatureMatrix);
		ArrayList<Double> maxArray=getMaxArray(intraFeatureMatrix);
		
		//Shift all the values in [0,1] according to each min and max value of the column
		for(ArrayList<Double> record: intraFeatureMatrix) {
			ArrayList<Double> normalizedRecord=normalizeRow(type, record, minArray, maxArray);
			normalizedMatrix.add(normalizedRecord);	
		}
		return normalizedMatrix;
	}

	/**Return the total number of categories for a bounding box cell*/
	public int getCategoriesNumber(ArrayList<FoursquareDataObject> array) {
		int n=0;
		for(FoursquareDataObject fdo: array) {
			n+=fdo.getCategories().length;
		}
		return n;
	}
	
	/**Create a list with distinct categories for a bounding box cell*/
	public ArrayList<String> createCategoryList(int index, ArrayList<FoursquareDataObject> array) {
		ArrayList<String> categories=new ArrayList<String>();
		for(FoursquareDataObject venue: array) {
			Category[] catArray=venue.getCategories();
			for(int j=0; j<catArray.length;j++){
				String c="";
				if(catArray[j].getParents().length>0) {
					if(index==0)
						c=catArray[j].getParents()[index]; //take the parent category name only if it is set
					else if(index==1) {
						//Take the second level category
						if(catArray[j].getParents().length>1)
							c=catArray[j].getParents()[index];
						else
							c=catArray[j].getName();
					}
				}
				else
					c=catArray[j].getName();
				int k=0;
				boolean found=false;
				while(k<categories.size() && !found) {
					String s=categories.get(k);
					if(c.equals((String) s))
						found=true;
					k++;
				}
				if(!found)
					categories.add(c);
			}
		}
		return categories;
	}
	
	/**Create a list with the number of occurrences for each distinct category*/
	public ArrayList<Integer> getCategoryOccurences(int index, ArrayList<FoursquareDataObject> array, ArrayList<String> categories) {
		int n;
		ArrayList<Integer> occurrences=new ArrayList<Integer>();
		for(String s: categories) {
			n=0;
			for(FoursquareDataObject fdo: array)
				for(Category c: fdo.getCategories()) {
					String str="";
					if(c.getParents().length>0) {
						if(index==0)
							str=c.getParents()[index]; //take the parent category name only if it is set
						else if(index==1) {
							//Take the second level category
							if(c.getParents().length>1)
								str=c.getParents()[index];
							else
								str=c.getName();
						}
					}
					else
						str=c.getName();
					if(str.equals((String) s))
						n++;
				}
			occurrences.add(n);
		}
		return occurrences;
	}
	
	/**Get the informations either of a bounding box cell or of single venues of a cell*/
	public ArrayList<ArrayList<Double>> getInformations(InformationType type, double lat, double lng, ArrayList<ArrayList<Double>> matrix, ArrayList<FoursquareDataObject> cell) {
		ArrayList<Double> rowOfMatrix=new ArrayList<Double>();
		ArrayList<Double> rowOfMatrixSecondLevel=new ArrayList<Double>();
		switch (type) {
		//If we consider single venues (coordinates both of venue and focal points are considered)
		case SINGLE:
			for(FoursquareDataObject venue: cell) {
				String category="";
				String categorySecondLevel="";
				for(Category c: venue.getCategories()) {
					if(c.getParents().length>0) {
						category=c.getParents()[0]; //take the parent category name only if it is set
						//Take the second level category
						if(c.getParents().length>1)
							categorySecondLevel=c.getParents()[1];
						else
							categorySecondLevel=c.getName();
						
					}
					else {
						category=c.getName();
						categorySecondLevel=c.getName();
					}
				}
				if(category.length()>0) { //update the matrix only if the category has a name
					ArrayList<String>aux=new ArrayList<String>();
					aux.add(category);
					ArrayList<String> auxSecondLevel=new ArrayList<String>();
					auxSecondLevel.add(categorySecondLevel);
					updateMap(type, this.map, aux);//update the hash map
					updateMap(type, this.mapSecondLevel, auxSecondLevel);
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
					singlesId.add(venue.getVenueId()); //memorize venue id
					timestamps.add(venue.getTimestamp()); //memorize timestamp
					beenHere.add(venue.getCheckinsCount()); //memorize check-ins count
					matrix.add(rowOfMatrix);
					this.matrixSecondLevel.add(rowOfMatrixSecondLevel);
				}
			}
			break;
		//If we consider a cell of venues
		case CELL:
			ArrayList<String> distinctList; //list of all the distinct categories for the cell
			ArrayList<String> distinctListSecondLevel;
			ArrayList<Integer> occurrencesList; //list of the occurrences of the distinct categories for the cell
			ArrayList<Integer> occurrencesListSecondLevel;
			distinctList=createCategoryList(0, cell);
			distinctListSecondLevel=createCategoryList(1, cell);
			occurrencesList=getCategoryOccurences(0, cell, distinctList);
			occurrencesListSecondLevel=getCategoryOccurences(1, cell, distinctListSecondLevel);
			updateMap(type, this.map, distinctList);//update the hash map
			updateMap(type, this.mapSecondLevel, distinctListSecondLevel);//update the hash map
			rowOfMatrix=fillRowWithCell(this.map, occurrencesList, distinctList, lat, lng); //create a consistent row (related to the categories). row of the transformation matrix (one for each cell);
			rowOfMatrixSecondLevel=fillRowWithCell(this.mapSecondLevel, occurrencesListSecondLevel, distinctListSecondLevel, lat, lng);
			if(this.total<rowOfMatrix.size())
				this.total=rowOfMatrix.size(); //update the overall number of categories
			if(this.totalSecondLevel<rowOfMatrixSecondLevel.size())
				this.totalSecondLevel=rowOfMatrixSecondLevel.size(); //update the overall number of categories
			matrix.add(rowOfMatrix);
			this.matrixSecondLevel.add(rowOfMatrixSecondLevel);
			break;
		}
		return matrix;
	}
	
	/**Group venues occurrences belonging to the same focal points*/
	public ArrayList<Double> groupSinglesToCell(BoundingBox b, ArrayList<ArrayList<Double>> matrix) {
		double value;
		double cLat=b.getCenterLat(); //focal coordinates of the cell
		double cLng=b.getCenterLng();
		ArrayList<Double> toRet=buildListZero(matrix.get(0).size()-2);
		toRet.set(0, cLat); //center latitude and longitude of the cell
		toRet.set(1, cLng);
		//Grouping in cells
		for(ArrayList<Double> record: matrix) {
			//venues of the same cell
			if(record.get(2)==cLat && record.get(3)==cLng) {
				for(int i=4;i<record.size();i++) {
					value=toRet.get(i-2)+record.get(i); //grouping by summing the occurrences
					toRet.set(i-2, value);
				}
			}
		}
		return toRet;
	}
	
	/**Get (as bounding boxes) all the distinct focal coordinates of singles*/
	public ArrayList<BoundingBox> getBoxes(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<BoundingBox> bbox=new ArrayList<BoundingBox>();
		BoundingBox b=new BoundingBox();
		b.setCenterLat(matrix.get(0).get(2));
		b.setCenterLng(matrix.get(0).get(3));
		bbox.add(b);
		double lat;
		double lng;
		
		for(int i=1;i<matrix.size();i++) {
			lat=matrix.get(i).get(2);
			lng=matrix.get(i).get(3);
			if((matrix.get(i-1).get(2)!=lat) || (matrix.get(i-1).get(3)!=lng)) {
				b=new BoundingBox();
				b.setCenterLat(lat);
				b.setCenterLng(lng);
				bbox.add(b);
			}
		}
		return bbox;
	}
	
	/** Haversine formula implementation. It returns the distance between 
	 * two points given latitude and longitude values in meters
	 */
	public double getDistance(double lat1, double lng1, double lat2, double lng2){
		double earthRadius = 6371; //in km
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return Math.floor(dist*1000)/1000;
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
	
	/**Get the feature labeled either for frequency, density or normalized density*/
	public ArrayList<String> getFeaturesLabel(CoordinatesNormalizationType type, String s, ArrayList<String> features) {
		ArrayList<String> featuresLabel=new ArrayList<String>();
		if(type.equals(CoordinatesNormalizationType.NORM) || type.equals(CoordinatesNormalizationType.NOTNORM)) {
			String label="";
			featuresLabel.add(features.get(0)); //Latitude
			featuresLabel.add(features.get(1)); //Longitude
			for(int i=2;i<features.size();i++) {
				label=s+"("+features.get(i)+")";
				featuresLabel.add(label);
			}
		}
		else if(type.equals(CoordinatesNormalizationType.MISSING)) {
			String label="";
			for(int i=2;i<features.size();i++) {
				label=s+"("+features.get(i)+")";
				featuresLabel.add(label);
			}
		}
		return featuresLabel;
	}
	
	/**Change the feature label by replacing 'old' with 'last'*/
	public ArrayList<String> changeFeaturesLabel(String old, String last, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			label=features.get(i).replace(old, last);
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
}
