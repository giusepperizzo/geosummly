package it.unito.geosummly;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class TransformationMatrixTest extends TestCase {
	
	public void testUpdateMap() {
		//Initialize the transformation matrix and create the hash map 
		TransformationMatrix tm=new TransformationMatrix();
		HashMap<String, Integer> actualMap=new HashMap<String, Integer>();
		tm.setMap(actualMap);
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
		HashMap<String, Integer> expectedMap=new HashMap<String, Integer>();
		expectedMap.put("Cat 1", 2);
		expectedMap.put("Cat 2", 3);
		expectedMap.put("Cat 3", 4);
		expectedMap.put("Cat 4", 5);
		expectedMap.put("Cat 5", 6);
		expectedMap.put("Cat 6", 7);
		
		//Start the tests
		assertNotNull(actualMap);
		assertEquals(expectedMap, actualMap);
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
		double lat=10.0;
		double lng=20.0;
		ArrayList<Double> actual;
		actual=tm.fillRow(occurrences, distincts, lat, lng);
		
		//Construct the test case
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(10.0);
		expected.add(20.0);
		expected.add(1.0);
		expected.add(2.0);
		expected.add(3.0);
		expected.add(4.0);
		expected.add(5.0);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	public void testFixRowsLength() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> actual= new ArrayList<ArrayList<Double>>();
		ArrayList<Double> row1=new ArrayList<Double>();
		row1.add(0.1);
		row1.add(0.2);
		ArrayList<Double> row2=new ArrayList<Double>();
		row2.add(0.5);
		row2.add(0.3);
		row2.add(0.0);
		row2.add(0.4);
		ArrayList<Double> row3=new ArrayList<Double>();
		row3.add(0.2);
		row3.add(0.0);
		row3.add(0.1);
		actual.add(row1);
		actual.add(row2);
		actual.add(row3);
		tm.fixRowsLength(row2.size(), actual);
		
		//Construct the test case
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> e1=new ArrayList<Double>();
		e1.add(0.1);
		e1.add(0.2);
		e1.add(0.0);
		e1.add(0.0);
		ArrayList<Double> e2=new ArrayList<Double>();
		e2.add(0.5);
		e2.add(0.3);
		e2.add(0.0);
		e2.add(0.4);
		ArrayList<Double> e3=new ArrayList<Double>();
		e3.add(0.2);
		e3.add(0.0);
		e3.add(0.1);
		e3.add(0.0);
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testSortFeatures() {
		TransformationMatrix tm=new TransformationMatrix();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 2);
		map.put("C", 3);
		map.put("A", 4);
		ArrayList<String> actual=tm.sortFeatures(map);
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Latitude");
		expected.add("Longitude");
		expected.add("A");
		expected.add("B");
		expected.add("C");
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testSortMatrix() {
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec1=new ArrayList<Double>();
		rec1.add(40.0);
		rec1.add(45.0);
		rec1.add(1.0);
		rec1.add(2.0);
		rec1.add(3.0);
		ArrayList<Double> rec2=new ArrayList<Double>();
		rec2.add(50.0);
		rec2.add(55.0);
		rec2.add(11.0);
		rec2.add(12.0);
		rec2.add(13.0);
		ArrayList<Double> rec3=new ArrayList<Double>();
		rec3.add(60.0);
		rec3.add(65.0);
		rec3.add(21.0);
		rec3.add(22.0);
		rec3.add(23.0);
		matrix.add(rec1);
		matrix.add(rec2);
		matrix.add(rec3);
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 2);
		map.put("C", 3);
		map.put("A", 4);
		ArrayList<ArrayList<Double>> actual=tm.sortMatrix(matrix, map);
		
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		exp1.add(40.0);
		exp1.add(45.0);
		exp1.add(3.0);
		exp1.add(1.0);
		exp1.add(2.0);
		ArrayList<Double> exp2=new ArrayList<Double>();
		exp2.add(50.0);
		exp2.add(55.0);
		exp2.add(13.0);
		exp2.add(11.0);
		exp2.add(12.0);
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp3.add(60.0);
		exp3.add(65.0);
		exp3.add(23.0);
		exp3.add(21.0);
		exp3.add(22.0);
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testBuildDensityMatrix() {
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a=new ArrayList<Double>();
		ArrayList<Double> b=new ArrayList<Double>();
		ArrayList<Double> c=new ArrayList<Double>();
		a.add(10.0);
		a.add(20.0);
		a.add(2.0);
		a.add(4.0);
		b.add(30.0);
		b.add(40.0);
		b.add(8.0);
		b.add(16.0);
		c.add(50.0);
		c.add(60.0);
		c.add(32.0);
		c.add(64.0);
		matrix.add(a);
		matrix.add(b);
		matrix.add(c);
		ArrayList<Double> area=new ArrayList<Double>();
		area.add(10.0);
		area.add(10.0);
		area.add(10.0);
		ArrayList<ArrayList<Double>> actual=tm.buildDensityMatrix(matrix, area);
		
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		ArrayList<Double> exp2=new ArrayList<Double>();
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp1.add(10.0);
		exp1.add(20.0);
		exp1.add(0.2);
		exp1.add(0.4);
		exp2.add(30.0);
		exp2.add(40.0);
		exp2.add(0.8);
		exp2.add(1.6);
		exp3.add(50.0);
		exp3.add(60.0);
		exp3.add(3.2);
		exp3.add(6.4);
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testBuildNormalizedMatrix() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a=new ArrayList<Double>();
		ArrayList<Double> b=new ArrayList<Double>();
		ArrayList<Double> c=new ArrayList<Double>();
		a.add(10.0);
		a.add(20.0);
		a.add(2.0);
		a.add(2.0);
		b.add(30.0);
		b.add(40.0);
		b.add(3.0);
		b.add(3.0);
		c.add(50.0);
		c.add(60.0);
		c.add(4.0);
		c.add(4.0);
		matrix.add(a);
		matrix.add(b);
		matrix.add(c);
		ArrayList<ArrayList<Double>> actual=tm.buildNormalizedMatrix(matrix);
		
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		ArrayList<Double> exp2=new ArrayList<Double>();
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp1.add(0.0);
		exp1.add(0.0);
		exp1.add(0.0);
		exp1.add(0.0);
		exp2.add(0.5);
		exp2.add(0.5);
		exp2.add(0.5);
		exp2.add(0.5);
		exp3.add(1.0);
		exp3.add(1.0);
		exp3.add(1.0);
		exp3.add(1.0);
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the test
		for(int i=0;i<actual.size();i++)
			for(int j=0;j<actual.get(i).size();j++)
				assertEquals(expected.get(i).get(j), actual.get(i).get(j), 0.001);
	}
	
	public void testGetSum() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(5.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(2.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(6.0);
		ArrayList<ArrayList<Double>> abc=new ArrayList<ArrayList<Double>>();
		abc.add(a);
		abc.add(b);
		abc.add(c);
		double actual=tm.getSum(abc, 0);
		
		//Construct the test case
		double expected=13;
		
		//Start the test
		assertEquals(expected, actual, 0);
	}
	
	public void testGetMinMax() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(5.0);
		a.add(3.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(2.0);
		b.add(4.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(6.0);
		c.add(10.0);
		ArrayList<ArrayList<Double>> abc=new ArrayList<ArrayList<Double>>();
		abc.add(a);
		abc.add(b);
		abc.add(c);
		double[] actual=tm.getMinMax(abc, 1);
		
		//Construct the test case
		double[] expected={3, 10};
		
		//Start the test
		for(int i=0;i<actual.length;i++)
			assertEquals(expected[i], actual[i]);
	}
	
	public void testNormalizeValues() {
		TransformationMatrix tm=new TransformationMatrix();
		double actual=tm.normalizeValues(-90, 90, 45);
		double expected=0.75;
		assertEquals(expected, actual, 0);
	}
	
	public void testGetFeaturesLabel() {
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<String> a=new ArrayList<String>();
		a.add("Latitude");
		a.add("Longitude");
		a.add("A");
		a.add("B");
		a.add("C");
		ArrayList<String> actual=tm.getFeaturesLabel("d", a);
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Latitude");
		expected.add("Longitude");
		expected.add("d(A)");
		expected.add("d(B)");
		expected.add("d(C)");
		
		//Start the test
		assertEquals(expected, actual);
	}
}
