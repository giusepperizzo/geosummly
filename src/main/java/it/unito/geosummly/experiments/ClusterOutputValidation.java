package it.unito.geosummly.experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import jp.ndca.similarity.distance.Jaccard;

import org.apache.commons.io.FileUtils;

public class ClusterOutputValidation {

	public static void main(String[] args) 
	{
		ClusterOutputValidation main = new ClusterOutputValidation();
		List<HashMap<String, Vector<Integer>>> holdout = 
				main.computeCrossFoldValidation("output/evaluation/clustering_output_validation/10-holdout/holdout_results_all_clusters.log");
	
		Jaccard jacc = new Jaccard();
		Double jaccOnLabels = 0.0;
		Double jaccOnSet = 0.0;
		HashMap<String,Vector<Double>> jaccOnSets = new HashMap<>();
		int iterations = 0;
		for (int i=0; i<holdout.size()-1; i++) 
		{
			HashMap<String, Vector<Integer>> ho1 = holdout.get(i);
			for(int j=i+1; j<holdout.size(); j++) 
			{
				System.out.printf("pair (%s,%s)\n", i,j);
				
				HashMap<String, Vector<Integer>> ho2 = holdout.get(j);

				System.out.printf("\tjaccard_labels=%s\n", jacc.calc(ho1.keySet().toArray(), ho2.keySet().toArray()) );
							
				//get cluster names from first set
				Set<String> cluster_names = new HashSet<String>();
				cluster_names.addAll(ho1.keySet());
				cluster_names.addAll(ho2.keySet());

				Double jaccOnPair = 0.0;
				for(String name : cluster_names) {
					Vector<Integer> ho1_objects= (ho1.get(name) == null) ? new Vector<Integer>() : ho1.get(name);
					Vector<Integer> ho2_objects= (ho2.get(name) == null) ? new Vector<Integer>() : ho2.get(name);
					
					System.out.printf("\tjaccard_on_set(%s)=%s\n", name, jacc.calc(ho1_objects, ho2_objects));
					
					if(!jaccOnSets.containsKey(name)){
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
				
				System.out.printf("\tjaccard_on_set_average=%s\n", jaccOnPair/cluster_names.size());
				
				jaccOnLabels += jacc.calc(ho1.keySet().toArray(), ho2.keySet().toArray() );
				jaccOnSet += jaccOnPair/cluster_names.size();
				iterations++;
			}
		}
		
		System.out.println("#####\n#  Totals\n###");
		System.out.println("avg_jaccard_labels=" + jaccOnLabels/iterations );
		System.out.println("avg_jaccard_objects=" + jaccOnSet/iterations );
				
		for(Entry<String,Vector<Double>> entry: jaccOnSets.entrySet()) {
			Double counter = 0.0; 
			for(Double d : entry.getValue()) {
				counter += d;
			}
			
			System.out.printf("avg_jaccard_cluster(%s)=%s\n", entry.getKey(), counter/entry.getValue().size()); 
		}
	}
	
	public List<HashMap<String, Vector<Integer>>> computeCrossFoldValidation(String path) 
	{
		List<HashMap<String, Vector<Integer>>> holdout = new ArrayList<>();
		try {
			String blob  = FileUtils.readFileToString(new File(path));
			String[] chunks = blob.split("_END_HO.*");
									
			for (String chunk : chunks) 
			{
				String[] lines = chunk.trim().split("\n");
				
				// HO level 
				HashMap<String, Vector<Integer>> ho = new HashMap<>();
				for (String line : lines) 
				{
					String[] items = line.trim().split(";");
					String cluster_name = items[0];
					String[] os = items[1].trim().split(" ");
					Vector<Integer> objects = new Vector<>();
					for (String o : os) {
						objects.add(Integer.parseInt(o));
					}
					ho.put(cluster_name, objects);
				}
				holdout.add(ho);
			}
			
			return holdout;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
