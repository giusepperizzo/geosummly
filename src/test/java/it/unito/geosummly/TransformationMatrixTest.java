package it.unito.geosummly;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class TransformationMatrixTest extends TestCase {
	
	public void testUpdateMap() {
		//Initialize the transformation matrix and create the hash map 
		TransformationMatrix tm=new TransformationMatrix();
		HashMap<String, Integer> actual=new HashMap<String, Integer>();
		tm.setMap(actual);
		ArrayList<String> a=new ArrayList<String>();
		a.add("Cat 1");
		a.add("Cat 1");
		a.add("Cat 2");
		a.add("Cat 1");
		a.add("Cat 3");
		a.add("Cat 2");
		a.add("Cat 3");
		a.add("Cat 4");
		a.add("Cat 1");
		a.add("Cat 5");
		a.add("Cat 2");
		a.add("Cat 5");
		tm.updateMap(a);
		a.add("Cat 5");
		a.add("Cat 6");
		a.add("Cat 2");
		a.add("Cat 6");
		tm.updateMap(a);
		
		//Construct the test case
		HashMap<String, Integer> expected=new HashMap<String, Integer>();
		expected.put("Cat 1", 2);
		expected.put("Cat 2", 3);
		expected.put("Cat 3", 4);
		expected.put("Cat 4", 5);
		expected.put("Cat 5", 6);
		expected.put("Cat 6", 7);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	public void testFillRow() {
		//Initialize the transformation matrix and create a consistent row 
		TransformationMatrix tm=new TransformationMatrix();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("Cat 1", 2);
		map.put("Cat 2", 3);
		map.put("Cat 3", 4);
		map.put("Cat 4", 5);
		map.put("Cat 5", 6);
		tm.setMap(map);
		ArrayList<Integer> occurrences=new ArrayList<Integer>();
		occurrences.add(3);
		occurrences.add(5);
		occurrences.add(1);
		occurrences.add(4);
		occurrences.add(2);
		ArrayList<String> distincts=new ArrayList<String>();
		distincts.add("Cat 3");
		distincts.add("Cat 5");
		distincts.add("Cat 1");
		distincts.add("Cat 4");
		distincts.add("Cat 2");
		int cat_num=10;
		double lat=10.0;
		double lng=20.0;
		ArrayList<Double> actual;
		actual=tm.fillRow(occurrences, distincts, cat_num, lat, lng);
		
		//Construct the test case
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(10.0);
		expected.add(20.0);
		expected.add(0.1);
		expected.add(0.2);
		expected.add(0.3);
		expected.add(0.4);
		expected.add(0.5);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
}
