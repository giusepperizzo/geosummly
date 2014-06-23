package it.unito.geosummly.tools;

import it.unito.geosummly.io.templates.FeatureCollectionTemplate;
import it.unito.geosummly.io.templates.FeatureTemplate;
import it.unito.geosummly.io.templates.VenueTemplate;
import it.unito.geosummly.pareto.ParetoPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OptimizationTools {
	
	/**
	 * Get the MultiPoints coordinates of all the clusters 
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> 
					getMultiPoints(FeatureCollectionTemplate fct) 
	{
		ArrayList<ArrayList<ArrayList<Double>>> objs = 
							new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<FeatureTemplate> ft_array=fct.getFeatures();

		//iterate for all clusters
		for(FeatureTemplate ft: ft_array) {
			objs.add(ft.getGeometry().getCoordinates()); //multipoints of a cluter
		}

		return objs;
	}
	
	/**
	 * Get the cells of all the clusters 
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> getObjectsOfClusters(ArrayList<ArrayList<VenueTemplate>> venues) {
		
		ArrayList<ArrayList<ArrayList<Double>>> objs = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> cells;
		ArrayList<Double> couple;
		
		//iterate for all clusters
		for(ArrayList<VenueTemplate> vt_array: venues) {
			
			cells = new ArrayList<ArrayList<Double>>();
			
			for(VenueTemplate v: vt_array) {
				
				couple=new ArrayList<Double>();
				couple.add(v.getCentroidLatitude());
				couple.add(v.getCentroidLongitude());
				
				//Create a list of distinct cells for each cluster
				if(!isPresent(cells, couple))
					cells.add(couple);
			}
			objs.add(cells);
		}
		
		return objs;
	}
	
	/**
	 * Check if a coordinates pair has been already selected
	*/
	public boolean isPresent(ArrayList<ArrayList<Double>> source, ArrayList<Double> a) {
		
		boolean present=false;
		ArrayList<Double> tmp;
		
		for(int i=0;i<source.size() && !present;i++) {
			
			tmp = new ArrayList<Double>(source.get(i));
			if(tmp.get(0) == a.get(0) && tmp.get(1) == a.get(1))
				present=true;
		}
		
		return present;
	}
	
	/**
	 * Get the venues of all the clusters 
	*/
	public ArrayList<ArrayList<VenueTemplate>> getVenuesOfClusters(FeatureCollectionTemplate fct) {
		
		ArrayList<ArrayList<VenueTemplate>> venues = new ArrayList<ArrayList<VenueTemplate>>();
		ArrayList<FeatureTemplate> ft_array=fct.getFeatures();
		
		//iterate for all clusters
		for(FeatureTemplate ft: ft_array) {
			venues.add(ft.getProperties().getVenues()); //venues of a cluster
		}
		
		return venues;
	}
	
	/**
	 * Get the labels of all the clusters 
	*/
	public ArrayList<String[]> getLabelsOfClusters(FeatureCollectionTemplate fct) {
		
		ArrayList<String[]> labels=new ArrayList<String[]>();
		ArrayList<FeatureTemplate> ft_array=fct.getFeatures();
		String[] label;
		
		//iterate for all clusters
		for(FeatureTemplate ft: ft_array) {
			label=ft.getProperties().getName().trim().split(",");
			labels.add(label);
		}
		
		return labels;
	}
	
	/**
	 * Get the ids of all the clusters 
	*/
	public ArrayList<Integer> getIdsOfClusters(FeatureCollectionTemplate fct) {
		
		ArrayList<Integer> ids=new ArrayList<Integer>();
		ArrayList<FeatureTemplate> ft_array=fct.getFeatures();
		int id;
		
		//iterate for all clusters
		for(FeatureTemplate ft: ft_array) {
			id=ft.getProperties().getClusterId();
			ids.add(id);
		}
		
		return ids;
	}
	
	
	/**
	 * Compute the spatial coverage function for each cluster
	 * For each cluster: sp_cov = #obj_of_cluster / #obj_of_bbox 
	 */
	public ArrayList<Double> getSpatialCoverage(
									ArrayList<ArrayList<ArrayList<Double>>> cells, 
									int bboxObj
									) 
	{
		
		ArrayList<Double> list=new ArrayList<Double>();
		for(ArrayList<ArrayList<Double>> clObjs: cells) {
			double i = clObjs.size();
			list.add(i/bboxObj);
		}
		
		return list;
	}
	
	/**Compute the density function for each cluster
	 * For each cluster: dens = #venues_of_cluster / #obj_of_cluster
	 * #obj_of_cluster represents the dimension of the cluster 
	*/
	public ArrayList<Double> getDensity(ArrayList<ArrayList<VenueTemplate>> venues, 
										ArrayList<ArrayList<ArrayList<Double>>> cells) {
		
		ArrayList<Double> list= new ArrayList<Double>();
		for(int i=0;i<venues.size();i++) {
			double ven = venues.get(i).size();
			double clObjs = cells.get(i).size();
			list.add(ven/clObjs);
		}
		
		return list;
	}
	
	/**Compute the heterogeneity function for each cluster
	 * For each cluster: het = #categories_of_cluster / #total_categories
	 * #categories_of_cluster is the number of cluster labels
	 * #total_categories is the number of categories found in the sampling state
	*/
	public ArrayList<Double> getHeterogeneity(ArrayList<String[]> labels, int totCat) {
		
		ArrayList<Double> list= new ArrayList<Double>();
		for(String[] str: labels) {
			double i=str.length;
			list.add(i/totCat);
		}
		
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
	
	/**
	 * Select the best #top clusters to keep in the fingerprint 
	*/
	public List<Integer> selectTop (Map<Integer, Double> sortedMap, int top) {
		
		List<Integer> keys = new ArrayList<Integer>(sortedMap.keySet());
		
		keys = keys.subList(0, top); //keep only the best clusters
		
		return keys;
	}
	
	/**
	 * Get the cells corresponding to the best #top clusters 
	*/
	public ArrayList<ArrayList<ArrayList<Double>>> getTopCells(ArrayList<ArrayList<ArrayList<Double>>> allCells,
																List<Integer> selected) {
		
		ArrayList<ArrayList<ArrayList<Double>>> cells = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(Integer i: selected) {
			cells.add(allCells.get(i-1));
		}
		
		return cells;
	}
	
	/**
	 * Get the venues corresponding to the best #top clusters 
	*/
	public ArrayList<ArrayList<VenueTemplate>> getTopVenues(ArrayList<ArrayList<VenueTemplate>> allVenues,
																List<Integer> selected) {
		
		ArrayList<ArrayList<VenueTemplate>> venues = new ArrayList<ArrayList<VenueTemplate>>();
		for(Integer i: selected) {
			venues.add(allVenues.get(i-1));
		}
		
		return venues;
	}
	
	/**
	 * Get the labels corresponding to the best #top clusters 
	*/
	public ArrayList<String[]> getTopLabels(
											ArrayList<String[]> allLabels,
											List<Integer> selected
										   ) 
	{
		ArrayList<String[]> labels = new ArrayList<String[]>();
		for(Integer i: selected) {
			labels.add(allLabels.get(i-1));
		}
		
		return labels;
	}

	public ArrayList<Double> getSSE(FeatureCollectionTemplate fct) 
	{
		ArrayList<Double> result = new ArrayList<>();
		ArrayList<FeatureTemplate> ft_array=fct.getFeatures();
		
		//iterate for all clusters
		for(FeatureTemplate ft: ft_array) {
			result.add(ft.getProperties().getSSE());
		}
		
		return result;
	}

	public Collection<ParetoPoint> getParetoPoints(
			ArrayList<Double> spatialCoverage, 
			ArrayList<Double> density,
			ArrayList<Double> heterogeneity, 
			ArrayList<Double> sse) 
	{
		ArrayList<ParetoPoint> result = new ArrayList<>();
		
		for (int i=0; i < spatialCoverage.size(); i++)
			result.add(new ParetoPoint(i,
									   spatialCoverage.get(i),
									   density.get(i),
									   heterogeneity.get(i),
									   sse.get(i))
					   );
		
		return result;
	}

	public List<Integer> listSelected(Collection<ParetoPoint> frontier) 
	{
		ArrayList<Integer> result = new ArrayList<>();
		
		for (ParetoPoint p : frontier) 
			result.add(p.getIndex());	
		
		
		return result;
	}
}
