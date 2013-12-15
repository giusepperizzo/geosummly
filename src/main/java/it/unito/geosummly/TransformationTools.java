package it.unito.geosummly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Giacomo Falcone
 *
 * Tools for the transformation matrix creation
 *   
 */

public class TransformationTools {
	
	public TransformationTools() {}
	
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
	
	/**Update the hash map given as parameter with new string values*/
	public HashMap<String, Integer> updateMap(HashMap<String, Integer> map, ArrayList<String> categories) {
		for(String s: categories)
			if(!map.containsKey(s)) {
				map.put(s, map.size()+2); //first value in the map has to be 2
		}
		return map;
	}
	
	/**Get a row of the matrix with latitude, longitude and occurrence values*/
	public ArrayList<Double> fillRow(HashMap<String, Integer> map, ArrayList<Integer> occurrences, ArrayList<String> distincts, double lat, double lng) {
		int index=0;
		double value=0;
		int size=map.size()+2;
		ArrayList<Double> row=buildListZero(size);
		row.set(0, lat); //lat, lng and area are in position 0 and 1
		row.set(1, lng);
		Iterator<String> iterD=distincts.iterator();
		Iterator<Integer> iterO=occurrences.iterator();
		/*for(int i=0;i<distincts.size();i++) {
			index=map.get(distincts.get(i));
			value=(double) occurrences.get(i);
			row.set(index, value); //put the occurrence value in the "right" position
		}*/
		while(iterD.hasNext() && iterO.hasNext()) {
			index=map.get(iterD.next()); //get the category corresponding to its occurrence value
			value=(double) iterO.next();
			row.set(index, value); //put the occurrence value in the "right" position
		}
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
		double sum;
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
		double currentValue;
		double denominator;
		double normalizedValue;
		normalizedRecord.add(record.get(0)); //latitude
		normalizedRecord.add(record.get(1)); //longitude
		for(int j=2;j<record.size();j++) {
			currentValue=record.get(j); //get the value
			denominator=sumArray.get(j-2);
			if(denominator>0) //check if denominator is bigger than 0
				normalizedValue=(currentValue/denominator); //intra-feature normalized value
			else
				normalizedValue=0.0;
			normalizedRecord.add(normalizedValue);
		}
		return normalizedRecord;
	}
	
	/**Normalize a value in [0,1]*/
	public double normalizeValues(double min, double max, double c) {
		double norm_c=(c-min)/(max-min);
		return norm_c;
	}
	
	/**Normalize the values of a row in [0,1] with respect to their own  min and max values*/
	public ArrayList<Double> normalizeRow(ArrayList<Double> array, ArrayList<Double> minArray, ArrayList<Double> maxArray) {
		ArrayList<Double> normalizedArray=new ArrayList<Double>();
		double normalizedValue;
		double min;
		double max;
		Iterator<Double> iterMin=minArray.iterator();
		Iterator<Double> iterMax=maxArray.iterator();
		for(Double d: array) {
			min=iterMin.next();
			max=iterMax.next();
			normalizedValue=normalizeValues(min, max, d);
			normalizedArray.add(normalizedValue);
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
	
	/**Get a matrix normalized in [0,1]. Before normalization, densities are intra-feature normalized*/
	public ArrayList<ArrayList<Double>> buildNormalizedMatrix(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<ArrayList<Double>> intraFeatureMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> normalizedMatrix=new ArrayList<ArrayList<Double>>();
		
		//get all the sums of the features values per column
		ArrayList<Double> sumArray=getSumArray(2, matrix); // it starts from index two because first two are for lat and lng
		
		//get an intra-feature normalized matrix except for the first two columns (lat and lng)
		for(ArrayList<Double> record: matrix) {
			ArrayList<Double> intraFeatureRecord=getIntraFeatureNormalization(record, sumArray);
			intraFeatureMatrix.add(intraFeatureRecord);
		}
		
		//get the arrays of min and max values
		ArrayList<Double> minArray=getMinArray(intraFeatureMatrix);
		ArrayList<Double> maxArray=getMaxArray(intraFeatureMatrix);
		
		//Shift all the values in [0,1] according to each min and max value of the column
		for(ArrayList<Double> record: intraFeatureMatrix) {
			ArrayList<Double> normalizedRecord=normalizeRow(record, minArray, maxArray);
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
	
	/**get the feature labeled either for frequency, density or normalized density*/
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
}
