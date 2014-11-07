package it.unito.geosummly.tools;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.utils.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.csv.CSVRecord;

import junit.framework.TestCase;

public class ClusteringToolsTest extends TestCase {
	
	public void testBoundariesFromCSV () 
	{
		CSVDataIO dataIO=new CSVDataIO();
		try {
			List<CSVRecord> listDens=dataIO.readCSVFile("tests/density-transformation-matrix.csv");
			ClusteringTools tools=new ClusteringTools();
			ArrayList<Pair<Double, Double>> boundaries = tools.getFeatureBoundariesFromCSV(listDens);
			
			assertEquals(12, boundaries.size());
			
			assertEquals(0.0, boundaries.get(2).getFirst());
			assertEquals(55.79, boundaries.get(2).getSecond(), 0.01);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void testBuildNormalizedFromList() {
		
		ArrayList<ArrayList<Double>> input = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> row_1 = new ArrayList<Double>();
		row_1.add(1.0); row_1.add(0.1); row_1.add(0.5);
		input.add(row_1);
		
		ClusteringTools tools = new ClusteringTools();
		ArrayList<ArrayList<Double>> actual = tools.buildNormalizedFromList(input);
		
		ArrayList<ArrayList<Double>> expected = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> row = new ArrayList<Double>();
		row.add(1.0); row.add(0.1); row.add(0.5);
		expected.add(row);
		
		assertEquals(expected, actual);
	}
	
	public void testGetFeaturesMap() {
		
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("ONE"); labels.add("TWO"); labels.add("ONE AND TWO");
		
		ClusteringTools tools = new ClusteringTools();
		HashMap<Integer, String> actual = tools.getFeaturesMap(labels);
		
		HashMap<Integer, String> expected = new HashMap<Integer, String>();
		expected.put(2, "ONE");
		expected.put(3, "TWO");
		
		assertEquals(expected, actual);
	}
	
	public void testGetDeltadMap() {
		
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("ONE"); labels.add("TWO"); labels.add("ONE AND TWO");
		
		ArrayList<String> minpts = new ArrayList<String>();
		minpts.add("2.5"); minpts.add("3.5"); minpts.add("0.5");
		
		ClusteringTools tools = new ClusteringTools();
		HashMap<String, Double> actual = tools.getDeltadMap(labels, minpts);
		
		HashMap<String, Double> expected = new HashMap<String, Double>();
		expected.put("ONE", 2.5);
		expected.put("TWO", 3.5);
		expected.put("ONE AND TWO", 0.5);
		
		assertEquals(expected, actual);
	}
	
	public void testGetEps() {
		
		ArrayList<ArrayList<Double>> dataset = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> r1 = new ArrayList<Double>();
		r1.add(1.0);
		ArrayList<Double> r2 = new ArrayList<Double>();
		r1.add(2.0);
		ArrayList<Double> r3 = new ArrayList<Double>();
		r1.add(3.0);
		ArrayList<Double> r4 = new ArrayList<Double>();
		r1.add(4.0);
		dataset.add(r1); dataset.add(r2);
		dataset.add(r3); dataset.add(r4);
		
		ClusteringTools tools = new ClusteringTools();
		double actual = tools.getEps(dataset);
		
		double expected = Math.sqrt(2)*0.5;
		
		assertEquals(expected, actual);
	}
	
	public void testGetClusterLabels() {
		
		TreeSet<String> expected = new TreeSet<String>();
		expected.add("A"); expected.add("B"); expected.add("C");
		expected.add("D");
		
		HashMap<Integer, String> clusters = new HashMap<Integer, String>();
		clusters.put(10, "A"); clusters.put(32, "B"); clusters.put(15, "C");
		clusters.put(50, "B"); clusters.put(1, "D");
		
		ClusteringTools tools = new ClusteringTools();
		TreeSet<String> actual = tools.getClusterLabels(clusters);
		
		assertEquals(expected, actual);
	}
	
	public void testGetCellsOfClusters() {
		
		ArrayList<TreeSet<Integer>> expected = new ArrayList<TreeSet<Integer>>();
		TreeSet<Integer> tree_10= new TreeSet<Integer>();
		TreeSet<Integer> tree_32_50= new TreeSet<Integer>();
		TreeSet<Integer> tree_15= new TreeSet<Integer>();
		TreeSet<Integer> tree_1= new TreeSet<Integer>();
		tree_10.add(100); tree_10.add(1000);
		tree_32_50.add(300); tree_32_50.add(400);
		tree_32_50.add(700); tree_32_50.add(800);
		tree_15.add(500); tree_15.add(600);
		tree_1.add(900); tree_1.add(1000);
		expected.add(tree_10); expected.add(tree_32_50);
		expected.add(tree_15); expected.add(tree_1);
		
		ClusteringTools tools = new ClusteringTools();
		HashMap<Integer, String> clusters = new HashMap<Integer, String>();
		clusters.put(10, "A"); clusters.put(32, "B"); clusters.put(15, "C");
		clusters.put(50, "B"); clusters.put(1, "D");
		
		TreeSet<String> tree = new TreeSet<String>();
		tree.add("A"); tree.add("B"); tree.add("C");
		tree.add("D");
		
		HashMap<Integer, ArrayList<Integer>> cells = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> cells_10 = new ArrayList<Integer>();
		ArrayList<Integer> cells_32 = new ArrayList<Integer>();
		ArrayList<Integer> cells_15 = new ArrayList<Integer>();
		ArrayList<Integer> cells_50 = new ArrayList<Integer>();
		ArrayList<Integer> cells_1 = new ArrayList<Integer>();
		cells_10.add(100); cells_10.add(1000);
		cells_32.add(300); cells_32.add(400);
		cells_15.add(500); cells_15.add(600);
		cells_50.add(700); cells_50.add(800);
		cells_1.add(900); cells_1.add(1000); cells_1.add(1000);
		cells.put(10, cells_10);
		cells.put(32, cells_32);
		cells.put(15, cells_15);
		cells.put(50, cells_50);
		cells.put(1, cells_1);
		
		ArrayList<TreeSet<Integer>> actual = tools.getCellsOfClusters(clusters, cells, tree);
		
		assertEquals(expected, actual);
	}
	
	public void testBuildHoldhoutMap() {
		
		Vector<Integer> vector_A = new Vector<Integer>();
		Vector<Integer> vector_B = new Vector<Integer>();
		Vector<Integer> vector_C = new Vector<Integer>();
		Vector<Integer> vector_D = new Vector<Integer>();
		vector_A.add(100); vector_A.add(1000);
		vector_B.add(300); vector_B.add(400);
		vector_B.add(700); vector_B.add(800);
		vector_C.add(500); vector_C.add(600);
		vector_D.add(900); vector_D.add(1000);
		
		HashMap<String, Vector<Integer>> expected = new HashMap<String, Vector<Integer>>();
		expected.put("A", vector_A);
		expected.put("B", vector_B);
		expected.put("C", vector_C);
		expected.put("D", vector_D);
		
		ClusteringTools tools = new ClusteringTools();
		TreeSet<String> tree = new TreeSet<String>();
		tree.add("A"); tree.add("B"); tree.add("C");
		tree.add("D");
		
		ArrayList<TreeSet<Integer>> cells = new ArrayList<TreeSet<Integer>>();
		TreeSet<Integer> tree_10= new TreeSet<Integer>();
		TreeSet<Integer> tree_32_50= new TreeSet<Integer>();
		TreeSet<Integer> tree_15= new TreeSet<Integer>();
		TreeSet<Integer> tree_1= new TreeSet<Integer>();
		tree_10.add(100); tree_10.add(1000);
		tree_32_50.add(300); tree_32_50.add(400);
		tree_32_50.add(700); tree_32_50.add(800);
		tree_15.add(500); tree_15.add(600);
		tree_1.add(900); tree_1.add(1000);
		cells.add(tree_10); cells.add(tree_32_50);
		cells.add(tree_15); cells.add(tree_1);
		
		HashMap<String, Vector<Integer>> actual = tools.buildHoldoutMap(tree, cells, 0);
		
		assertEquals(expected, actual);
	}
}
