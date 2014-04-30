package it.unito.geosummly.tools;

import it.unito.geosummly.BoundingBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;

import jp.ndca.similarity.distance.Jaccard;

public class EvaluationTools {

	public EvaluationTools() {}
	
	/**
	 * Fill in the matrix of aggregate (frequency) values from a list of CSV records.
	 * The header won't be considered.
	 * Timestamp, latitude and longitude columns won't be considered.
	*/
	public ArrayList<ArrayList<Double>> buildAggregatesFromList(List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		//remove the header, so i=1
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			//remove timestamp, latitude and longitude columns, so j=3
			for(int j=3;j<list.get(k).size();j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		return matrix;
	}
	
	/**
	 * Fill in the matrix of single venues from a list of CSV records.
	 * The header won't be considered.
	 * Timestamp, been here and venue id columns won't be considered.
	*/
	public ArrayList<ArrayList<Double>> buildSinglesFromList(List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		//remove the header, so i=1
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			//remove timestamp, been_here, id_venue columns, so j=3
			for(int j=3;j<list.get(k).size();j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		return matrix;
	}
	
	/**
	 * Fill in the list of features from a list of CSV records.
	 * Timestamp column won't be considered.
	*/
	public ArrayList<String> getFeaturesFormList(List<CSVRecord> list) {
		ArrayList<String> features=new ArrayList<String>();
		//remove timestamp column, so i=1
		for(int i=1;i<list.get(0).size();i++) {
			features.add(list.get(0).get(i));
		}
		return features;
	}
	
	/**
	 * Fill in the random matrix of aggregate (frequency) values from the original matrix of aggregate (frequency) values.
	*/
	public ArrayList<ArrayList<Double>> buildFrequencyRandomMatrix(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> minArray, ArrayList<Double> maxArray) {
		ArrayList<ArrayList<Double>> frm=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> randomRecord;
		double randomValue;
		int min;
		int max;
		int i=0;
		int j=0;
		
		//matrix.size() records per matrix
		while(i<matrix.size()) {
			randomRecord=new ArrayList<Double>();
			//get randomly the feature values
			while(j<minArray.size()) {
				min=minArray.get(j).intValue();
				max=maxArray.get(j).intValue();
				randomValue=min + (int) (Math.random()*(max-min+1)); //random number from min to max included
				randomRecord.add(randomValue);
				j++;
			}
			frm.add(randomRecord);
			j=0;
			i++;
		}
		return frm;
	}
	
	/**
	 * Fill in the list of features from a list of CSV records.
	 * Timestamp, been here, venue id, venue latitude, venue longitude, 
	 * centroid latitude and centroid longitude columns won't be considered.
	*/
	public ArrayList<String> getFeaturesFromList(List<CSVRecord> list) {
		
		ArrayList<String> features = new ArrayList<String>();
		
		//Don't consider timestamp, been here, venue id, venueLat, venueLng, focalLat, focalLng, so k=7
		for(int i=7;i<list.get(0).size();i++)
			features.add(list.get(0).get(i));
		
		return features;
	}
	
	/**
	 * Fill in the list of timestamp values from a list of CSV records.
	*/
	public ArrayList<Long> getTimestampsFromList(List<CSVRecord> list) {
		ArrayList<Long> timestamps=new ArrayList<Long>();
		//we don't have to consider the header so i=1
		for(int i=1;i<list.size();i++)
			timestamps.add(Long.parseLong(list.get(i).get(0)));
		return timestamps;
	}
	
	/**
	 * Create the folds for cross-validation.
	 * The result will be a list of fnum matrices of singles with N/fnum random venues for each matrix.
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> createFolds(ArrayList<ArrayList<Double>> matrix, int fnum) {
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices=new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> ithMatrix;
		int dimension=matrix.size()/fnum;
		int randomValue;
		Random random = new Random();
		for(int i=0;i<fnum-1;i++) {
			ithMatrix=new ArrayList<ArrayList<Double>>();
			for(int j=0;j<dimension;j++) {
				randomValue=random.nextInt(matrix.size()); //random number between 0 (included) and current matrix.size() (excluded)
				ithMatrix.add(matrix.get(randomValue));
			}
			allMatrices.add(ithMatrix);
			matrix.removeAll(ithMatrix);
		}
		allMatrices.add(matrix);
		return allMatrices;
	}
	
	/**
	 * Remove the columns venue_latitude and venue_longitude from the folds  
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> removeVenueCoordinates(ArrayList<ArrayList<ArrayList<Double>>> folds) {
		
		for(ArrayList<ArrayList<Double>> f: folds) {
			for(ArrayList<Double> rec: f) {
				rec.remove(1); //remove venue latitude
				rec.remove(0); //remove venue longitude
			}
		}
		
		ArrayList<ArrayList<ArrayList<Double>>> newFolds = new ArrayList<ArrayList<ArrayList<Double>>>(folds);
		return newFolds;
	}
	
	/**
	 * Group the folds from single venues to cells.
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> groupFolds(ImportTools tools, ArrayList<BoundingBox> data, ArrayList<ArrayList<ArrayList<Double>>> allMatrices) {
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped=new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> ithGrouped;
		for(ArrayList<ArrayList<Double>> m: allMatrices) {
			ithGrouped=new ArrayList<ArrayList<Double>>();
			for(BoundingBox b: data) {
				ithGrouped.add(tools.groupSinglesToCell(b, m));
			}
			allGrouped.add(ithGrouped);
		}
		return allGrouped;
	}
	
	/**
	 * Compute the Jaccard similarity coefficient between the holdouts 
	*/
	public StringBuilder computeJaccard(List<HashMap<String, Vector<Integer>>> holdoutList) {
		StringBuilder builder=new StringBuilder();
		Jaccard jacc = new Jaccard();
		Double jaccOnLabels = 0.0;
		Double jaccOnSet = 0.0;
		HashMap<String,Vector<Double>> jaccOnSets = new HashMap<>();
		int iterations = 0;
		for (int i=0; i<holdoutList.size()-1; i++) {
			HashMap<String, Vector<Integer>> ho1 = holdoutList.get(i);
			for(int j=i+1; j<holdoutList.size(); j++) {
				builder.append("pair ("+i+","+j+")\n");
				
				HashMap<String, Vector<Integer>> ho2 = holdoutList.get(j);
				builder.append("\tjaccard_labels="+jacc.calc(ho1.keySet().toArray(), ho2.keySet().toArray())+"\n");
							
				//get cluster names from first set
				Set<String> cluster_names = new HashSet<String>();
				cluster_names.addAll(ho1.keySet());
				cluster_names.addAll(ho2.keySet());

				Double jaccOnPair = 0.0;
				for(String name : cluster_names) {
					Vector<Integer> ho1_objects= (ho1.get(name) == null) ? new Vector<Integer>() : ho1.get(name);
					Vector<Integer> ho2_objects= (ho2.get(name) == null) ? new Vector<Integer>() : ho2.get(name);
					builder.append("\tjaccard_on_set("+name+")="+jacc.calc(ho1_objects, ho2_objects)+"\n");
					
					if(!jaccOnSets.containsKey(name)) {
						Vector<Double> v = new Vector<>();
						v.add(jacc.calc(ho1_objects, ho2_objects));
						jaccOnSets.put(name, v);
					}
					else {
						Vector<Double> v = jaccOnSets.get(name);
						jaccOnSets.remove(name);
						v.add(jacc.calc(ho1_objects, ho2_objects));
						jaccOnSets.put(name, v);
					}
					
					jaccOnPair += jacc.calc(ho1_objects, ho2_objects);
				}
				builder.append("\tjaccard_on_set_average="+jaccOnPair/cluster_names.size()+"\n");
				
				jaccOnLabels += jacc.calc(ho1.keySet().toArray(), ho2.keySet().toArray() );
				jaccOnSet += jaccOnPair/cluster_names.size();
				iterations++;
			}
		}
		builder.append("#####\n#  Totals\n####\n");
		builder.append("avg_jaccard_labels=" + jaccOnLabels/iterations+"\n");
		builder.append("avg_jaccard_objects=" + jaccOnSet/iterations+"\n");
				
		for(Entry<String,Vector<Double>> entry: jaccOnSets.entrySet()) {
			Double counter = 0.0; 
			for(Double d : entry.getValue()) {
				counter += d;
			}
			builder.append("avg_jaccard_cluster("+entry.getKey()+")="+counter/entry.getValue().size()+"\n");
		}
		return builder;
	}
	
	/**
	 * Compute the discard in percentage between the SSE of clustering output on the entire dataset (real_SSE)
	 * and the minimum SSE value of correctness experiment (random_SSE).
	 * discard= (real_SSE * 100) / random_SSE 
	*/
	public double getSSEDiscard(ArrayList<Double> SSEs, double cl_sse) {
		Collections.sort(SSEs);
		double min=SSEs.get(0); //get the minimum of SSEs
		double discard= (cl_sse*100)/min;
		
		return discard;
	}
	
	/**Change the feature label by replacing 'old' with 'last'*/
	public ArrayList<String> changeFeaturesLabel(String old, String last, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			//remove character and parenthesis
			label=features.get(i).replaceFirst(old, last).replaceAll("\\(", "").replaceAll("\\)", "");
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
	
	/**Get the feature labeled either for frequency, density or normalized density without timestamp column*/
	public ArrayList<String> getFeaturesLabelNoTimestamp(CoordinatesNormalizationType type, String s, ArrayList<String> features) {
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
}
