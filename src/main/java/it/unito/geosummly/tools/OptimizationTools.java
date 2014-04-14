package it.unito.geosummly.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OptimizationTools {

	/**Compute the spatial coverage function for each cluster
	 * For each cluster: sp_cov = #obj_of_cluster / #obj_of_bbox 
	*/
	public ArrayList<Double> getSpatialCoverage(ArrayList<Integer> clObj, int bboxObj) {
		
		ArrayList<Double> list=new ArrayList<Double>();
		for(Integer i: clObj)
			list.add(((double)i)/bboxObj);
		
		return list;
	}
	
	/**Compute the density function for each cluster
	 * For each cluster: dens = #venues_of_cluster / #obj_of_cluster
	 * #obj_of_cluster represents the dimension of the cluster 
	*/
	public ArrayList<Double> getDensity(ArrayList<Integer> clVen, ArrayList<Integer> clObj) {
		
		ArrayList<Double> list= new ArrayList<Double>();
		for(int i=0;i<clVen.size();i++)
			list.add(((double) clVen.get(i))/clObj.get(i));
		
		return list;
	}
	
	/**Compute the heterogeneity function for each cluster
	 * For each cluster: het = #categories_of_cluster / #total_categories
	 * #categories_of_cluster is the number of cluster labels
	 * #total_categories is the number of categories found in the sampling state
	*/
	public ArrayList<Double> getHeterogeneity(ArrayList<Integer> clCat, int totCat) {
		
		ArrayList<Double> list= new ArrayList<Double>();
		for(Integer i: clCat)
			list.add(((double) clCat.get(i))/totCat);
		
		return list;
	}
	
	/**
	 * Get the linear combination of spatial coverage (f1), 
	 * density (f2) and heterogeneity (f3) functions
	 * For each cluster: f0 = w1*f1 + w2*f2 + w3*f3
	*/
	public Map<Integer, Double> getLinearCombination(ArrayList<Double> f1, 
													ArrayList<Double> f2,
													ArrayList<Double> f3, 
													ArrayList<Double> weights) {
		
		Map<Integer, Double> clusterMap=new HashMap<Integer, Double>();
		for(int i=0;i<f1.size();i++) {
			double f0 = (weights.get(0)*f1.get(i)) + 
						(weights.get(1)*f2.get(i)) + 
						(weights.get(2)*f3.get(i));
			clusterMap.put(i+1, f0);
		}
		
		return clusterMap;
	}
	
	/**
	 * Sort the f0 values of the map in a decreasing order
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Double> sortByValue(Map clusterMap) {
		
		List<Double> fZeroes=new LinkedList(clusterMap.entrySet());
		
		//sort list based on comparator
		Collections.sort(fZeroes, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		
		//put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = fZeroes.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
}
