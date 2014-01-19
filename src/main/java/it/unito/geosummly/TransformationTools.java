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
	private HashMap<String, Integer> map;
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationTools() {
		this.total=0;
		this.map=new HashMap<String, Integer>();
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
	
	/**Update the hash map given as parameter with new string values from a cell*/
	public HashMap<String, Integer> updateMapWithCell(HashMap<String, Integer> map, ArrayList<String> categories) {
		for(String s: categories) {
			if(!map.containsKey(s)) {
				map.put(s, map.size()+2); //first value in the map has to be 2
			}
		}
		return map;
	}
	
	/**Update the hash map given as parameter with new string value*/
	public HashMap<String, Integer> updateMapWithSingle(HashMap<String, Integer> map, String category) {
		if(!map.containsKey(category))
			map.put(category, map.size()+2); //first value in the map has to be 2
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
	
	/**Get the same length for all the matrix rows*/
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
	
	/**Sort matrix in alphabetical order for columns*/
	public ArrayList<ArrayList<Double>> sortMatrix(ArrayList<ArrayList<Double>> matrix, HashMap<String,Integer> map) {
		ArrayList<ArrayList<Double>> sortedMatrix=new ArrayList<ArrayList<Double>>();
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
		return sortedMatrix;
	}
	
	/**Get a matrix with density values*/
	public ArrayList<ArrayList<Double>> buildDensityMatrix(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> area) {
		ArrayList<ArrayList<Double>> densMatrix=new ArrayList<ArrayList<Double>>();
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
		return densMatrix;
	}
	
	/**Get a matrix with density values without considering lat and lng coordinates*/
	public ArrayList<ArrayList<Double>> buildDensityMatrixNoCoord(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> area) {
		ArrayList<ArrayList<Double>> densMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> densRecord;
		for(int i=0;i<matrix.size();i++) {
			densRecord=new ArrayList<Double>();
			for(int j=0;j<matrix.get(i).size();j++) {
				densRecord.add(matrix.get(i).get(j)/area.get(i));
			}
			densMatrix.add(densRecord);
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
	
	/**Get the features list for the dataset of single venues, used in the second step of evaluation*/
	public ArrayList<String> getFeaturesForSinglesEvaluation(ArrayList<String> features) {
		features.add(0, "Venue Latitude");
		features.add(1, "Venue Longitude");
		features.set(2, "Focal Latitude");
		features.set(3, "Focal Longitude");
		return features;
	}
	
	/**Get the feature labeled either for frequency, density or normalized density*/
	public ArrayList<String> getFeaturesLabel(String s, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		featuresLabel.add(features.get(0)); //Latitude
		featuresLabel.add(features.get(1)); //Longitude
		for(int i=2;i<features.size();i++) {
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
	
	/**Get the feature labeled either for frequency, density or normalized density, without considering lat and lng coordinates*/
	public ArrayList<String> getFeaturesLabelNoCoord(String s, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		return featuresLabel;
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
	public ArrayList<String> createCategoryList(ArrayList<FoursquareDataObject> array) {
		ArrayList<String> categories=new ArrayList<String>();
		for(FoursquareDataObject venue: array) {
			Category[] catArray=venue.getCategories();
			for(int j=0; j<catArray.length;j++){
				String c;
				if(catArray[j].getParents().length>0)
					c=catArray[j].getParents()[0]; //take the parent category name only if it is set
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
	public ArrayList<Integer> getCategoryOccurences(ArrayList<FoursquareDataObject> array, ArrayList<String> categories) {
		int n;
		ArrayList<Integer> occurrences=new ArrayList<Integer>();
		for(String s: categories) {
			n=0;
			for(FoursquareDataObject fdo: array)
				for(Category c: fdo.getCategories()) {
					String str;
					if(c.getParents().length>0)
						str=c.getParents()[0]; //take the parent category name only if it is set
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
		switch (type) {
		//If we consider single venues
		case SINGLE:
			for(FoursquareDataObject venue: cell) {
				String category="";
				for(Category c: venue.getCategories()) {
					if(c.getParents().length>0)
						category=c.getParents()[0]; //take the parent category name only if it is set
					else
						category=c.getName();
				}
				updateMapWithSingle(this.map, category);//update the hash map
				rowOfMatrix=fillRowWithSingle(this.map, category, venue.getLatitude(), venue.getLongitude()); //create a consistent row (related to the categories) //row of the transformation matrix (one for each cell);
				if(this.total<rowOfMatrix.size())
					this.total=rowOfMatrix.size(); //update the overall number of categories
				matrix.add(rowOfMatrix);
			}
			break;
		//If we consider a cell of venues
		case CELL:
			ArrayList<String> distinctList; //list of all the distinct categories for the cell
			ArrayList<Integer> occurrencesList; //list of the occurrences of the distinct categories for the cell
			distinctList=createCategoryList(cell);
			occurrencesList=getCategoryOccurences(cell, distinctList);
			updateMapWithCell(this.map, distinctList);//update the hash map
			rowOfMatrix=fillRowWithCell(this.map, occurrencesList, distinctList, lat, lng); //create a consistent row (related to the categories) //row of the transformation matrix (one for each cell);
			if(this.total<rowOfMatrix.size())
				this.total=rowOfMatrix.size(); //update the overall number of categories
			matrix.add(rowOfMatrix);
			break;
		}
		return matrix;
	}
	
	/**Get the informations of single venues of a cell considering the focal points of the cell instead of venue's lat and lng*/
	public ArrayList<ArrayList<Double>> getInformationsWithFocalPts(InformationType type, double lat, double lng, ArrayList<ArrayList<Double>> matrix, ArrayList<FoursquareDataObject> cell) {
		if(type.equals(InformationType.SINGLE)) {
			ArrayList<Double> rowOfMatrix=new ArrayList<Double>();
			for(FoursquareDataObject venue: cell) {
				String category="";
				for(Category c: venue.getCategories()) {
					if(c.getParents().length>0)
						category=c.getParents()[0]; //take the parent category name only if it is set
					else
						category=c.getName();
				}
				if(category.length()>0) { //update the matrix only if the category has a name
					updateMapWithSingle(this.map, category);//update the hash map
					rowOfMatrix=fillRowWithSingle(this.map, category, lat, lng); //create a consistent row (related to the categories) //row of the transformation matrix (one for each cell);
					if(this.total<rowOfMatrix.size())
						this.total=rowOfMatrix.size(); //update the overall number of categories
					rowOfMatrix.add(0, venue.getLatitude());
					rowOfMatrix.add(1, venue.getLongitude());
					matrix.add(rowOfMatrix);
				}
			}
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
}
