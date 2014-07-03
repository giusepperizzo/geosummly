package it.unito.geosummly.tools;

import java.math.BigDecimal;
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
	
	/*public void testFillRowWithSingle() {
		
		ArrayList<BigDecimal> expected = new ArrayList<BigDecimal>();
		expected.add(new BigDecimal(10.0));
		expected.add(new BigDecimal(20.0));
		expected.add(new BigDecimal(0.0));
		expected.add(new BigDecimal(1.0));
		expected.add(new BigDecimal(0.0));
		expected.add(new BigDecimal(0.0));
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Cat 1", 2);
		map.put("Cat 2", 3);
		map.put("Cat 3", 4);
		map.put("Cat 4", 5);
		BigDecimal lat = new BigDecimal(10.0);
		BigDecimal lng = new BigDecimal(20.0);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<BigDecimal> actual = tools.fillRowWithSingle(map, "Cat 2", lat, lng);
		
		//Start the tests
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	public void testFixRowsLength() {
		
		ArrayList<ArrayList<BigDecimal>> expected = 
							new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> e1=new ArrayList<BigDecimal>();
		e1.add(new BigDecimal(0.1)); e1.add(new BigDecimal(0.2));
		e1.add(new BigDecimal(0.0)); e1.add(new BigDecimal(0.0));
		
		ArrayList<BigDecimal> e2=new ArrayList<BigDecimal>();
		e2.add(new BigDecimal(0.5)); e2.add(new BigDecimal(0.3));
		e2.add(new BigDecimal(0.0)); e2.add(new BigDecimal(0.4));
		
		ArrayList<BigDecimal> e3=new ArrayList<BigDecimal>();
		e3.add(new BigDecimal(0.2)); e3.add(new BigDecimal(0.0));
		e3.add(new BigDecimal(0.1)); e3.add(new BigDecimal(0.0));
		
		expected.add(e1); expected.add(e2); expected.add(e3);
		
		ArrayList<ArrayList<BigDecimal>> actual = 
						new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> row1=new ArrayList<BigDecimal>();
		row1.add(new BigDecimal(0.1)); row1.add(new BigDecimal(0.2));
		
		ArrayList<BigDecimal> row2=new ArrayList<BigDecimal>();
		row2.add(new BigDecimal(0.5)); row2.add(new BigDecimal(0.3));
		row2.add(new BigDecimal(0.0)); row2.add(new BigDecimal(0.4));
		
		ArrayList<BigDecimal> row3=new ArrayList<BigDecimal>();
		row3.add(new BigDecimal(0.2)); row3.add(new BigDecimal(0.0));
		row3.add(new BigDecimal(0.1));
		
		actual.add(row1); actual.add(row2); actual.add(row3);
		
		SamplingTools tools = new SamplingTools();
		actual = tools.fixRowsLength(row2.size(), actual);
		
		assertEquals(expected, actual);
	}
	
	public void testSortMatrix() {
		
		ArrayList<ArrayList<BigDecimal>> expected = 
							new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> exp1 = new ArrayList<BigDecimal>();
		exp1.add(new BigDecimal(0.1)); exp1.add(new BigDecimal(0.11));
		exp1.add(new BigDecimal(40.0)); exp1.add(new BigDecimal(45.0));
		exp1.add(new BigDecimal(0.0)); exp1.add(new BigDecimal(1.0));
		exp1.add(new BigDecimal(0.0)); exp1.add(new BigDecimal(0.0));
		
		ArrayList<BigDecimal> exp2 = new ArrayList<BigDecimal>();
		exp2.add(new BigDecimal(0.2)); exp2.add(new BigDecimal(0.12));
		exp2.add(new BigDecimal(50.0)); exp2.add(new BigDecimal(55.0));
		exp2.add(new BigDecimal(0.0)); exp2.add(new BigDecimal(0.0));
		exp2.add(new BigDecimal(0.0)); exp2.add(new BigDecimal(1.0));
		
		ArrayList<BigDecimal> exp3 = new ArrayList<BigDecimal>();
		exp3.add(new BigDecimal(0.3)); exp3.add(new BigDecimal(0.13));
		exp3.add(new BigDecimal(60.0)); exp3.add(new BigDecimal(65.0));
		exp3.add(new BigDecimal(0.0)); exp3.add(new BigDecimal(0.0));
		exp3.add(new BigDecimal(1.0)); exp3.add(new BigDecimal(0.0));
		
		expected.add(exp1); expected.add(exp2); expected.add(exp3);
		
		ArrayList<ArrayList<BigDecimal>> matrix = 
						new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<BigDecimal> rec1=new ArrayList<BigDecimal>();
		rec1.add(new BigDecimal(0.1)); rec1.add(new BigDecimal(0.11));
		rec1.add(new BigDecimal(40.0)); rec1.add(new BigDecimal(45.0));
		rec1.add(new BigDecimal(1.0)); rec1.add(new BigDecimal(0.0));
		rec1.add(new BigDecimal(0.0)); rec1.add(new BigDecimal(0.0));
		
		ArrayList<BigDecimal> rec2=new ArrayList<BigDecimal>();
		rec2.add(new BigDecimal(0.2)); rec2.add(new BigDecimal(0.12));
		rec2.add(new BigDecimal(50.0)); rec2.add(new BigDecimal(55.0));
		rec2.add(new BigDecimal(0.0)); rec2.add(new BigDecimal(0.0));
		rec2.add(new BigDecimal(0.0)); rec2.add(new BigDecimal(1.0));
		
		ArrayList<BigDecimal> rec3=new ArrayList<BigDecimal>();
		rec3.add(new BigDecimal(0.3)); rec3.add(new BigDecimal(0.13));
		rec3.add(new BigDecimal(60.0)); rec3.add(new BigDecimal(65.0));
		rec3.add(new BigDecimal(0.0)); rec3.add(new BigDecimal(1.0));
		rec3.add(new BigDecimal(0.0)); rec3.add(new BigDecimal(0.0));
		
		matrix.add(rec1); matrix.add(rec2); matrix.add(rec3);
		
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 2); map.put("C", 3); map.put("A", 4); map.put("D", 5);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<ArrayList<BigDecimal>> actual = 
						tools.sortMatrixSingles(matrix, map);
		
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
		expected.add("Latitude"); expected.add("Longitude");
		expected.add("A"); expected.add("B"); expected.add("C");
		
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("B", 2); map.put("C", 3); map.put("A", 4);
		
		SamplingTools tools = new SamplingTools();
		ArrayList<String> actual = tools.sortFeatures(map);
		
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
		a.add("Latitude"); a.add("Longitude"); a.add("A");
		a.add("B"); a.add("C");
		
		SamplingTools tools = new SamplingTools();
		ArrayList<String> actual = tools.getFeaturesForSingles(a);
		
		assertEquals(expected, actual);
	}*/
}
