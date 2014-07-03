package it.unito.geosummly;

import static org.junit.Assert.assertEquals;
import it.unito.geosummly.tools.EvaluationTools;
import it.unito.geosummly.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class JaccardTest {

	@Test
	public void read() 
	{
		List<Pair<?,?>> pairs = new ArrayList<>();
		
		try {
			String jaccard_log = FileUtils.readFileToString(new File("tests/jaccard.log"));
			
			String[] folds = jaccard_log.split("_END_F0.*");
			assertEquals(3, folds.length);
			
			for (String fold : folds) {
				String[] holds = fold.trim().split("_END_H.*");
				assertEquals(2,  holds.length);
						
				Map<String, Vector<Integer>> first = createHoldOutSet(holds[0]);
				Map<String, Vector<Integer>> second = createHoldOutSet(holds[1]);
				
				pairs.add(new Pair(first, second));
			}
			
			EvaluationTools et = new EvaluationTools();
			StringBuilder out = et.computeJaccard2(pairs);
			
			FileUtils.writeStringToFile(new File("tests/jaccard.out"), out.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	public HashMap<String, Vector<Integer>> createHoldOutSet (String hold)
	{
		HashMap<String, Vector<Integer>> result = new HashMap<>();
		
		String[] lines = hold.trim().split("\n");

		for (String line : lines) 
		{
			String[] items = line.trim().split(";");
			String cluster_name = items[0];
			String[] os = items[1].trim().split(" ");
			Vector<Integer> objects = new Vector<>();
			for (String o : os) {
				objects.add(Integer.parseInt(o));
			}
			result.put(cluster_name, objects);
		}
		
		return result;
	}
	
	
}
