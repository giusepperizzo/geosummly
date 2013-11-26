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
		ArrayList<String> actualHeader=new ArrayList<String>();
		tm.setHeader(actualHeader);
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
		ArrayList<String> expectedHeader=new ArrayList<String>();
		expectedHeader.add("Latitude");
		expectedHeader.add("Longitude");
		expectedHeader.add("Cat 1");
		expectedHeader.add("Cat 2");
		expectedHeader.add("Cat 3");
		expectedHeader.add("Cat 4");
		expectedHeader.add("Cat 5");
		expectedHeader.add("Cat 6");
		
		//Start the tests
		assertNotNull(actualMap);
		assertNotNull(expectedHeader);
		assertEquals(expectedMap, actualMap);
		assertEquals(expectedHeader, actualHeader);
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
		double area=100;
		ArrayList<Double> actual;
		actual=tm.fillRow(occurrences, distincts, lat, lng, area);
		
		//Construct the test case
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(10.0);
		expected.add(20.0);
		expected.add(10.0);
		expected.add(20.0);
		expected.add(30.0);
		expected.add(40.0);
		expected.add(50.0);
		
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
	
	public void testBuildNotNormalizedMatrix() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> notnorm=new ArrayList<ArrayList<Double>>();
		tm.setNotNormalizedMatrix(notnorm);
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(10.0);
		a.add(10.0);
		a.add(4.0);
		a.add(6.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(10.0);
		b.add(20.0);
		b.add(2.0);
		b.add(4.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(20.0);
		c.add(20.0);
		c.add(6.0);
		c.add(2.0);
		ArrayList<ArrayList<Double>> support=new ArrayList<ArrayList<Double>>();
		support.add(a);
		support.add(b);
		support.add(c);
		tm.buildNotNormalizedMatrix(support);
		ArrayList<ArrayList<Double>> actual=tm.getNotNormalizedMatrix();
		
		//Construct the test case
		ArrayList<Double> exp1=new ArrayList<Double>();
		exp1.add(10.0);
		exp1.add(10.0);
		exp1.add(0.333);
		exp1.add(0.5);
		ArrayList<Double> exp2=new ArrayList<Double>();
		exp2.add(10.0);
		exp2.add(20.0);
		exp2.add(0.166);
		exp2.add(0.333);
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp3.add(20.0);
		exp3.add(20.0);
		exp3.add(0.5);
		exp3.add(0.166);
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());		
		for(int i=0;i<expected.size();i++)
			for(int j=0;j<expected.get(0).size();j++)
				assertEquals(expected.get(i).get(j), actual.get(i).get(j),  0.001);
	}
	
	public void testBuildNormalizedMatrix() {
		//Initialize the transformation matrix and execute the method
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> norm=new ArrayList<ArrayList<Double>>();
		tm.setNormalizedMatrix(norm);
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(10.0);
		a.add(10.0);
		a.add(4.0);
		a.add(8.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(10.0);
		b.add(20.0);
		b.add(2.0);
		b.add(4.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(20.0);
		c.add(20.0);
		c.add(8.0);
		c.add(2.0);
		ArrayList<ArrayList<Double>> support=new ArrayList<ArrayList<Double>>();
		support.add(a);
		support.add(b);
		support.add(c);
		tm.buildNormalizedMatrix(support);
		ArrayList<ArrayList<Double>> actual=tm.getNormalizedMatrix();
		
		//Construct the test case
		ArrayList<Double> exp1=new ArrayList<Double>();
		exp1.add(0.0);
		exp1.add(0.0);
		exp1.add(0.333);
		exp1.add(1.0);
		ArrayList<Double> exp2=new ArrayList<Double>();
		exp2.add(0.0);
		exp2.add(1.0);
		exp2.add(0.0);
		exp2.add(0.333);
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp3.add(1.0);
		exp3.add(1.0);
		exp3.add(1.0);
		exp3.add(0.0);
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());		
		for(int i=0;i<expected.size();i++)
			for(int j=0;j<expected.get(0).size();j++)
				assertEquals(expected.get(i).get(j), actual.get(i).get(j),  0.001);
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
		double[] expected={3.0, 10.0};
		
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
}
