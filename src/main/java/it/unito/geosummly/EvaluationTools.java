package it.unito.geosummly;

import java.util.ArrayList;
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
	 * Fill in the matrix of single venues from a list of CSV records.
	 * The header won't be considered.
	 * Timestamp, been here and venue id columns won't be considered.
	*/
	public ArrayList<ArrayList<Double>> buildSinglesFromList(List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		//remove the header, so i=1
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			//remove timestamo, been_here, id_venue columns, so j=3
			for(int j=3;j<list.get(k).size();j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		return matrix;
	}
	
	/**
	 * Fill in the map of features from a list of CSV records.
	 * Timestamp, been here, venue id, venue latitude, venue longitude, centroid latitude and centroid longitude columns
	 * won't be considered.
	 * Each record of the map will be: feature name, key.
	 * The keys start from 2.
	*/
	public HashMap<String, Integer> getFeaturesMapFromList(List<CSVRecord> list) {
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		int key=2;
		//Don't consider timestamp, been here, venue id, venueLat, venueLng, focalLat, focalLng, so k=7
		for(int i=7;i<list.get(0).size();i++) {
			map.put(list.get(0).get(i), key);
			key++;
		}
		return map;
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
	 * Group the folds from single venues to cells.
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> groupFolds(TransformationTools tools, ArrayList<BoundingBox> data, ArrayList<ArrayList<ArrayList<Double>>> allMatrices) {
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
	 * Get the area of each cell of the grid.
	*/
	public ArrayList<Double> getCellsArea(TransformationTools tools, ArrayList<BoundingBox> data, ArrayList<ArrayList<Double>> matrix) {
		ArrayList<Double> areas=new ArrayList<Double>();
		double edgeValue=tools.getDistance(data.get(0).getCenterLat(), data.get(0).getCenterLng(), data.get(1).getCenterLat(), data.get(1).getCenterLng());
		double areaValue=Math.pow(edgeValue, 2);
		for(int i=0; i<matrix.size();i++)
			areas.add(areaValue);
		return areas;
	}
	
	/**
	 * Get the transformation matrix of a fold.
	*/
	public TransformationMatrix transformFold(ArrayList<ArrayList<Double>> grouped, TransformationTools tools, HashMap<String, Integer> map, ArrayList<Double> bboxArea) {
		TransformationMatrix tm=new TransformationMatrix();
		tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequency=tools.sortMatrix(CoordinatesNormalizationType.NORM, grouped, map);
		tm.setFrequencyMatrix(frequency);
		ArrayList<ArrayList<Double>> density=tools.buildDensityMatrix(CoordinatesNormalizationType.NORM, tm.getFrequencyMatrix(), bboxArea);
		tm.setDensityMatrix(density);
		ArrayList<ArrayList<Double>> normalized=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, tm.getDensityMatrix());
		tm.setNormalizedMatrix(normalized);
		tm.setHeader(tools.sortFeatures(map));
		return tm;
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
}
