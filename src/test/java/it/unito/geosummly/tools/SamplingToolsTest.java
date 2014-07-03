package it.unito.geosummly.tools;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class SamplingToolsTest extends TestCase{
	
	public void testbuildListZero() {
		
		int size=5;
		
		ArrayList<Byte> expected=new ArrayList<Byte>();
		for(int i=0; i<size; i++)
			expected.add((byte)(0));
		
		SamplingTools tools = new SamplingTools();
		ArrayList<Byte> actual = tools.buildListZero2(size);
		
		assertEquals(expected, actual);
	}
	
	public void testUpdateMap() {
		
		HashMap<String, Integer> expected=new HashMap<String, Integer>();
		expected.put("Cat 1", 0); expected.put("Cat 5", 1);;
		
		SamplingTools tools = new SamplingTools();
		HashMap<String, Integer> actual = 
						new HashMap<String, Integer>();
		ArrayList<String> a = new ArrayList<String>();
		a.add("Cat 1"); a.add("Cat 1"); a.add("Cat 2");
		
		actual = tools.updateMap2(actual,a);
		
		a.set(0, "Cat 5");
		
		actual = tools.updateMap2(actual,a);
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	public void testFillRowWithSingle() {
		
		ArrayList<Byte> expected = new ArrayList<Byte>();
		expected.add((byte) 0);
		expected.add((byte) 1);
		expected.add((byte) 0);
		expected.add((byte) 0);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Cat 1", 0);
		map.put("Cat 2", 1);
		map.put("Cat 3", 2);
		map.put("Cat 4", 3);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<Byte> actual = tools.fillRowWithSingle2(map, "Cat 2");
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	public void testFixRowsLength() {
		
		ArrayList<ArrayList<Byte>> expected = 
							new ArrayList<ArrayList<Byte>>();
		ArrayList<Byte> e1=new ArrayList<Byte>();
		e1.add((byte)(0)); e1.add((byte)(0)); e1.add((byte)(0));
		
		ArrayList<Byte> e2=new ArrayList<Byte>();
		e2.add((byte)(0)); e2.add((byte)(0.4)); e2.add((byte)(0));
		
		ArrayList<Byte> e3=new ArrayList<Byte>();
		e3.add((byte)(0.1)); e3.add((byte)(0)); e3.add((byte)(0));
		
		expected.add(e1); expected.add(e2); expected.add(e3);
		
		ArrayList<ArrayList<Byte>> actual = 
						new ArrayList<ArrayList<Byte>>();
		ArrayList<Byte> row1=new ArrayList<Byte>();
		row1.add((byte)(0)); row1.add((byte)(0));
		
		ArrayList<Byte> row2=new ArrayList<Byte>();
		row2.add((byte) 0); row2.add((byte) 0.4); row2.add((byte) 0);
		
		ArrayList<Byte> row3=new ArrayList<Byte>();
		row3.add((byte) 0.1); row3.add((byte) 0);
		
		actual.add(row1); actual.add(row2); actual.add(row3);
		
		SamplingTools tools = new SamplingTools();
		actual = tools.fixRowsLength2(row2.size(), actual);
		
		assertEquals(expected, actual);
	}
	
	public void testSortMatrix() {
		
		ArrayList<ArrayList<Byte>> expected = 
							new ArrayList<ArrayList<Byte>>();
		ArrayList<Byte> exp1 = new ArrayList<Byte>();
		exp1.add((byte) 0.0); exp1.add((byte) 1.0);
		exp1.add((byte) 0.0); exp1.add((byte) 0.0);
		
		ArrayList<Byte> exp2 = new ArrayList<Byte>();
		exp2.add((byte) 0.0); exp2.add((byte) 0.0);
		exp2.add((byte) 0.0); exp2.add((byte) 1.0);
		
		ArrayList<Byte> exp3 = new ArrayList<Byte>();
		exp3.add((byte) 0.0); exp3.add((byte) 0.0);
		exp3.add((byte) 1.0); exp3.add((byte) 0.0);
		
		expected.add(exp1); expected.add(exp2); expected.add(exp3);
		
		ArrayList<ArrayList<Byte>> matrix = 
						new ArrayList<ArrayList<Byte>>();
		ArrayList<Byte> rec1=new ArrayList<Byte>();
		rec1.add((byte) 1.0); rec1.add((byte) 0.0);
		rec1.add((byte) 0.0); rec1.add((byte) 0.0);
		
		ArrayList<Byte> rec2=new ArrayList<Byte>();
		rec2.add((byte) 0.0); rec2.add((byte) 0.0);
		rec2.add((byte) 0.0); rec2.add((byte) 1.0);
		
		ArrayList<Byte> rec3=new ArrayList<Byte>();
		rec3.add((byte) 0.0); rec3.add((byte) 1.0);
		rec3.add((byte) 0.0); rec3.add((byte) 0.0);
		
		matrix.add(rec1); matrix.add(rec2); matrix.add(rec3);
		
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 0); map.put("C", 1); map.put("A", 2); map.put("D", 3);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<ArrayList<Byte>> actual = 
						tools.sortMatrixSingles2(matrix, map);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testGetTopCategory() {
		
		String expected = "cat_1";
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("subsubcat_111", "subcat_11");
		map.put("subsubcat_112", "subcat_11");
		map.put("subcat_11", "cat_1");
		map.put("subcat_12", "cat_1");
		map.put("cat_1", null);
		map.put("subcat_21", "cat_2");
		map.put("cat_2", null);
		
		SamplingTools tools = new SamplingTools();
		String actual = tools.getTopCategory("subsubcat_111", map);
		String actual_1 = tools.getTopCategory("subcat_11", map);
		String actual_2 = tools.getTopCategory("cat_1", map);
		
		assertEquals(expected, actual);
		assertEquals(expected, actual_1);
		assertEquals(expected, actual_2);
	}
	
	public void testGetSubCategory() {
		
		String expected = "subcat_11";
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("subsubcat_111", "subcat_11");
		map.put("subsubcat_112", "subcat_11");
		map.put("subcat_11", "cat_1");
		map.put("cat_1", null);
		map.put("subcat_21", "cat_2");
		map.put("cat_2", null);
		
		SamplingTools tools = new SamplingTools();
		String actual = tools.getSubCategory("subsubcat_111", map);
		String actual_1 = tools.getSubCategory("subcat_11", map);
		
		assertEquals(expected, actual);
		assertEquals(expected, actual_1);
	}
	
	public void testSortFeatures() {
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("A"); expected.add("B"); expected.add("C");
		
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 0); map.put("C", 1); map.put("A", 2);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<String> actual = tools.sortFeatures2(map);
		
		assertEquals(expected, actual);
	}
	
	public void testGetFeaturesForSingles() {
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Timestamp (ms)"); expected.add("Been Here");
		expected.add("Venue Id"); expected.add("Venue Latitude");
		expected.add("Venue Longitude"); expected.add("Focal Latitude");
		expected.add("Focal Longitude"); expected.add("A");
		expected.add("B"); expected.add("C");
		
		ArrayList<String> a=new ArrayList<String>();
		a.add("A"); a.add("B"); a.add("C");
		
		SamplingTools tools = new SamplingTools();
		ArrayList<String> actual = tools.getFeaturesForSingles2(a);
		
		assertEquals(expected, actual);
	}
}
