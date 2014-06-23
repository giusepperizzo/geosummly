package it.unito.geosummly;

import it.unito.geosummly.tools.SamplingTools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class TransformationToolsTest extends TestCase {
	
	public void testbuildListZero() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
		int size=5;
		ArrayList<BigDecimal> actual=tm.buildListZero(size);
		
		//Construct the test case
		ArrayList<BigDecimal> expected=new ArrayList<BigDecimal>();
		for(int i=0;i<size;i++)
			expected.add(new BigDecimal(0.0));
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	/*public void testUpdateMap() {
		//Initialize the transformation matrix and create the hash map 
		SamplingTools tm=new SamplingTools();
		HashMap<String, Integer> actual=new HashMap<String, Integer>();
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
		actual=tm.updateMap(InformationType.CELL, actual,a);
		a.add("Cat 5");
		a.add("Cat 6");
		a.add("Cat 2");
		a.add("Cat 6");
		actual=tm.updateMap(InformationType.CELL, actual,a);
		
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
	}*/
	
	/*public void testFillRow() {
		//Initialize the transformation matrix and create a consistent row 
		SamplingTools tm=new SamplingTools();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("Cat 1", 2);
		map.put("Cat 2", 3);
		map.put("Cat 3", 4);
		map.put("Cat 4", 5);
		map.put("Cat 5", 6);
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
		ArrayList<Double> actual=tm.fillRowWithCell(map, occurrences, distincts, lat, lng);
		
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
	}*/
	
	public void testFixRowsLength() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
		ArrayList<ArrayList<BigDecimal>> actual= new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> row1=new ArrayList<BigDecimal>();
		row1.add(new BigDecimal(0.1));
		row1.add(new BigDecimal(0.2));
		ArrayList<BigDecimal> row2=new ArrayList<BigDecimal>();
		row2.add(new BigDecimal(0.5));
		row2.add(new BigDecimal(0.3));
		row2.add(new BigDecimal(0.0));
		row2.add(new BigDecimal(0.4));
		ArrayList<BigDecimal> row3=new ArrayList<BigDecimal>();
		row3.add(new BigDecimal(0.2));
		row3.add(new BigDecimal(0.0));
		row3.add(new BigDecimal(0.1));
		actual.add(row1);
		actual.add(row2);
		actual.add(row3);
		actual=tm.fixRowsLength(row2.size(), actual);
		
		//Construct the test case
		ArrayList<ArrayList<BigDecimal>> expected=new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> e1=new ArrayList<BigDecimal>();
		e1.add(new BigDecimal(0.1));
		e1.add(new BigDecimal(0.2));
		e1.add(new BigDecimal(0.0));
		e1.add(new BigDecimal(0.0));
		ArrayList<BigDecimal> e2=new ArrayList<BigDecimal>();
		e2.add(new BigDecimal(0.5));
		e2.add(new BigDecimal(0.3));
		e2.add(new BigDecimal(0.0));
		e2.add(new BigDecimal(0.4));
		ArrayList<BigDecimal> e3=new ArrayList<BigDecimal>();
		e3.add(new BigDecimal(0.2));
		e3.add(new BigDecimal(0.0));
		e3.add(new BigDecimal(0.1));
		e3.add(new BigDecimal(0.0));
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	/*public void testGetSum() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
	}*/
	
	/*public void testGetSumArray() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(5.0);
		a.add(6.0);
		a.add(5.0);
		a.add(6.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(2.0);
		b.add(3.0);
		b.add(2.0);
		b.add(3.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(6.0);
		c.add(7.0);
		c.add(6.0);
		c.add(7.0);
		ArrayList<ArrayList<Double>> abc=new ArrayList<ArrayList<Double>>();
		abc.add(a);
		abc.add(b);
		abc.add(c);
		ArrayList<Double> actual=tm.getSumArray(2, abc);
		
		//Construct the test case
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(13.0);
		expected.add(16.0);
		
		//Start the test
		assertEquals(expected, actual);
	}*/
	
	/*public void testGetMin() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
		double actual=tm.getMin(abc, 1);
		
		//Construct the test case
		double expected=3.0;
		
		//Start the test
		assertEquals(expected, actual, 0);
	}*/
	
	/*public void testGetMinArray() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
		ArrayList<Double> actual=tm.getMinArray(abc);
		
		//Construct the test case
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(2.0);
		expected.add(3.0);
		
		//Start the test
		assertEquals(expected, actual);
	}*/
	
	/*public void testGetMax() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
		double actual=tm.getMax(abc, 1);
		
		//Construct the test case
		double expected=10.0;
		
		//Start the test
		assertEquals(expected, actual, 0);
	}*/
	
	/*public void testGetMaxArray() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
		ArrayList<Double> actual=tm.getMaxArray(abc);
		
		//Construct the test case
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(6.0);
		expected.add(10.0);
		
		//Start the test
		assertEquals(expected, actual);
	}*/
	
	/*public void testGetIntraFeatureNormalization() {
		SamplingTools tm=new SamplingTools();
		ArrayList<Double> record=new ArrayList<Double>();
		record.add(40.0);
		record.add(50.0);
		record.add(2.0);
		record.add(4.0);
		record.add(8.0);
		ArrayList<Double> sumArray=new ArrayList<Double>();
		sumArray.add(20.0);
		sumArray.add(40.0);
		sumArray.add(80.0);
		ArrayList<Double> actual=tm.getIntraFeatureNormalization(record, sumArray);
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(40.0);
		expected.add(50.0);
		expected.add(0.1);
		expected.add(0.1);
		expected.add(0.1);
		
		assertEquals(expected, actual);
	}*/
	
	/*public void testGetIntraFeatureNormalizationNoCoord() {
		SamplingTools tm=new SamplingTools();
		ArrayList<Double> record=new ArrayList<Double>();
		record.add(2.0);
		record.add(4.0);
		record.add(8.0);
		ArrayList<Double> sumArray=new ArrayList<Double>();
		sumArray.add(20.0);
		sumArray.add(40.0);
		sumArray.add(80.0);
		ArrayList<Double> actual=tm.getIntraFeatureNormalizationNoCoord(record, sumArray);
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(0.1);
		expected.add(0.1);
		expected.add(0.1);
		
		assertEquals(expected, actual);
	}*/
	
	/*public void testNormalizeValues() {
		SamplingTools tm=new SamplingTools();
		double actual=tm.normalizeValues(-90, 90, 45);
		double expected=0.75;
		assertEquals(expected, actual, 0);
	}*/
	
	/*public void testNormalizeRow() {
		SamplingTools tm=new SamplingTools();
		ArrayList<Double> record=new ArrayList<Double>();
		record.add(6.0);
		record.add(16.0);
		ArrayList<Double> minArray=new ArrayList<Double>();
		minArray.add(4.0);
		minArray.add(16.0);
		ArrayList<Double> maxArray=new ArrayList<Double>();
		maxArray.add(8.0);
		maxArray.add(32.0);
		ArrayList<Double> actual=tm.normalizeRow(CoordinatesNormalizationType.NORM, record, minArray, maxArray);
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(0.5);
		expected.add(0.0);
		
		assertEquals(expected, actual);
	}*/
	
	/*public void testSortMatrix() {
		SamplingTools tm=new SamplingTools();
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec1=new ArrayList<Double>();
		rec1.add(40.0);
		rec1.add(45.0);
		rec1.add(1.0);
		rec1.add(2.0);
		rec1.add(3.0);
		rec1.add(4.0);
		ArrayList<Double> rec2=new ArrayList<Double>();
		rec2.add(50.0);
		rec2.add(55.0);
		rec2.add(11.0);
		rec2.add(12.0);
		rec2.add(13.0);
		rec2.add(14.0);
		ArrayList<Double> rec3=new ArrayList<Double>();
		rec3.add(60.0);
		rec3.add(65.0);
		rec3.add(21.0);
		rec3.add(22.0);
		rec3.add(23.0);
		rec3.add(24.0);
		matrix.add(rec1);
		matrix.add(rec2);
		matrix.add(rec3);
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 2);
		map.put("C", 3);
		map.put("A", 4);
		map.put("D", 5);
		ArrayList<ArrayList<Double>> actual=tm.sortMatrix(CoordinatesNormalizationType.NORM, matrix, map);
		
		ArrayList<ArrayList<Double>> expected=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		exp1.add(40.0);
		exp1.add(45.0);
		exp1.add(3.0);
		exp1.add(1.0);
		exp1.add(2.0);
		exp1.add(4.0);
		ArrayList<Double> exp2=new ArrayList<Double>();
		exp2.add(50.0);
		exp2.add(55.0);
		exp2.add(13.0);
		exp2.add(11.0);
		exp2.add(12.0);
		exp2.add(14.0);
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp3.add(60.0);
		exp3.add(65.0);
		exp3.add(23.0);
		exp3.add(21.0);
		exp3.add(22.0);
		exp3.add(24.0);
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		
		//Start the test
		assertEquals(expected, actual);
	}*/
	
	/*public void testBuildDensityMatrix() {
		SamplingTools tm=new SamplingTools();
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
		ArrayList<ArrayList<Double>> actual=tm.buildDensityMatrix(CoordinatesNormalizationType.NORM, matrix, area);
		
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
		
		SamplingTools tm1=new SamplingTools();
		ArrayList<ArrayList<Double>> matrix1=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a1=new ArrayList<Double>();
		ArrayList<Double> b1=new ArrayList<Double>();
		ArrayList<Double> c1=new ArrayList<Double>();
		a1.add(2.0);
		a1.add(4.0);
		b1.add(8.0);
		b1.add(16.0);
		c1.add(32.0);
		c1.add(64.0);
		matrix1.add(a1);
		matrix1.add(b1);
		matrix1.add(c1);
		ArrayList<Double> area1=new ArrayList<Double>();
		area1.add(10.0);
		area1.add(10.0);
		area1.add(10.0);
		ArrayList<ArrayList<Double>> actual1=tm1.buildDensityMatrix(CoordinatesNormalizationType.MISSING, matrix1, area1);
		
		ArrayList<ArrayList<Double>> expected1=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp11=new ArrayList<Double>();
		ArrayList<Double> exp21=new ArrayList<Double>();
		ArrayList<Double> exp31=new ArrayList<Double>();
		exp11.add(0.2);
		exp11.add(0.4);
		exp21.add(0.8);
		exp21.add(1.6);
		exp31.add(3.2);
		exp31.add(6.4);
		expected1.add(exp11);
		expected1.add(exp21);
		expected1.add(exp31);
		
		//Start the test
		assertEquals(expected1, actual1);
	}*/
	
	/*public void testBuildNormalizedMatrix() {
		//Initialize the transformation matrix and execute the method
		SamplingTools tm=new SamplingTools();
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
		ArrayList<ArrayList<Double>> actual=tm.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, matrix);
		
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
	}*/
	
	public void testSortFeatures() {
		SamplingTools tm=new SamplingTools();
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
	
	public void testGetFeaturesForSinglesEvaluation() {
		SamplingTools tm=new SamplingTools();
		ArrayList<String> a=new ArrayList<String>();
		a.add("Latitude");
		a.add("Longitude");
		a.add("A");
		a.add("B");
		a.add("C");
		ArrayList<String> actual=tm.getFeaturesForSingles(a);
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Timestamp (ms)");
		expected.add("Been Here");
		expected.add("Venue Id");
		expected.add("Venue Latitude");
		expected.add("Venue Longitude");
		expected.add("Focal Latitude");
		expected.add("Focal Longitude");
		expected.add("A");
		expected.add("B");
		expected.add("C");
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	/*public void testGetFeaturesLabel() {
		SamplingTools tm=new SamplingTools();
		ArrayList<String> a=new ArrayList<String>();
		a.add("Latitude");
		a.add("Longitude");
		a.add("A");
		a.add("B");
		a.add("C");
		ArrayList<String> actual=tm.getFeaturesLabel(CoordinatesNormalizationType.NORM, "d", a);
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Timestamps(ms)");
		expected.add("Latitude");
		expected.add("Longitude");
		expected.add("d(A)");
		expected.add("d(B)");
		expected.add("d(C)");
		
		//Start the test
		assertEquals(expected, actual);
		
		SamplingTools tm1=new SamplingTools();
		ArrayList<String> a1=new ArrayList<String>();
		a1.add("Latitude");
		a1.add("Longitude");
		a1.add("A");
		a1.add("B");
		a1.add("C");
		ArrayList<String> actual1=tm1.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "d", a1);
		
		ArrayList<String> expected1=new ArrayList<String>();
		expected1.add("d(A)");
		expected1.add("d(B)");
		expected1.add("d(C)");
		
		//Start the test
		assertEquals(expected1, actual1);
	}*/
	
	/*public void testGroupSinglesToCell() {
		SamplingTools tm=new SamplingTools();
		ArrayList<Long> singlesTimestamp=new ArrayList<>();
		singlesTimestamp.add((long) 0); singlesTimestamp.add((long) 0); singlesTimestamp.add((long) 0);
		tm.setSinglesTimestamps(singlesTimestamp);
		BoundingBox b=new BoundingBox();
		b.setCenterLat(45.0);
		b.setCenterLng(7.0);
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec1=new ArrayList<Double>();
		rec1.add(40.0);
		rec1.add(30.0);
		rec1.add(45.0);
		rec1.add(7.0);
		rec1.add(1.0);
		rec1.add(0.0);
		rec1.add(0.0);
		matrix.add(rec1);
		ArrayList<Double> rec2=new ArrayList<Double>();
		rec2.add(40.5);
		rec2.add(30.5);
		rec2.add(25.0);
		rec2.add(27.0);
		rec2.add(1.0);
		rec2.add(1.0);
		rec2.add(0.0);
		matrix.add(rec2);
		ArrayList<Double> rec3=new ArrayList<Double>();
		rec3.add(40.8);
		rec3.add(30.8);
		rec3.add(45.0);
		rec3.add(7.0);
		rec3.add(1.0);
		rec3.add(0.0);
		rec3.add(1.0);
		matrix.add(rec3);
		ArrayList<Double> actual=tm.groupSinglesToCell(b, matrix);
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(45.0);
		expected.add(7.0);
		expected.add(2.0);
		expected.add(0.0);
		expected.add(1.0);
		
		//Start the test
		assertEquals(expected, actual);
	}*/
}
