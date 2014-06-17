package it.unito.geosummly.tools;

import java.util.ArrayList;

import junit.framework.TestCase;

public class DiscoveryToolsTest extends TestCase {
	
	public void testGetmeanArray() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(2.0); expected.add(5.0); expected.add(8.0);
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record_1 = new ArrayList<Double>();
		ArrayList<Double> record_2 = new ArrayList<Double>();
		ArrayList<Double> record_3 = new ArrayList<Double>();
		record_1.add(1.0); record_1.add(4.0); record_1.add(7.0);
		record_2.add(2.0); record_2.add(5.0); record_2.add(8.0);
		record_3.add(3.0); record_3.add(6.0); record_3.add(9.0);
		matrix.add(record_1); matrix.add(record_2); matrix.add(record_3);
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<Double> actual = tools.getMeanArray(matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testGetStdMatrix() {
		
		ArrayList<ArrayList<Double>> expected = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> std = new ArrayList<Double>();
		std.add(0.816496580927726); std.add(0.816496580927726); std.add(0.816496580927726);
		expected.add(std); expected.add(std); expected.add(std);
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record_1 = new ArrayList<Double>();
		ArrayList<Double> record_2 = new ArrayList<Double>();
		ArrayList<Double> record_3 = new ArrayList<Double>();
		record_1.add(1.0); record_1.add(4.0); record_1.add(7.0);
		record_2.add(2.0); record_2.add(5.0); record_2.add(8.0);
		record_3.add(3.0); record_3.add(6.0); record_3.add(9.0);
		matrix.add(record_1); matrix.add(record_2); matrix.add(record_3);
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<ArrayList<Double>> actual = tools.getStdMatrix(matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testGetMean() {
		
		double expected = 5.0;
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record_1 = new ArrayList<Double>();
		ArrayList<Double> record_2 = new ArrayList<Double>();
		ArrayList<Double> record_3 = new ArrayList<Double>();
		record_1.add(1.0); record_1.add(4.0); record_1.add(7.0);
		record_2.add(2.0); record_2.add(5.0); record_2.add(8.0);
		record_3.add(3.0); record_3.add(6.0); record_3.add(9.0);
		matrix.add(record_1); matrix.add(record_2); matrix.add(record_3);
		
		DiscoveryTools tools = new DiscoveryTools();
		double actual = tools.getMean(matrix, 1);
		
		assertEquals(expected, actual);
	}
	
	public void testGetMeanSecond() {
		
		double expected = 5.0;
		
		ArrayList<Double> record = new ArrayList<Double>();
		record.add(2.0); record.add(5.0); record.add(8.0);
		
		DiscoveryTools tools = new DiscoveryTools();
		double actual = tools.getMean(record);
		
		assertEquals(expected, actual);
	}
	
	public void testGetVariance() {
		
		double expected = 0.666666666666666;
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record_1 = new ArrayList<Double>();
		ArrayList<Double> record_2 = new ArrayList<Double>();
		ArrayList<Double> record_3 = new ArrayList<Double>();
		record_1.add(1.0); record_1.add(4.0); record_1.add(7.0);
		record_2.add(2.0); record_2.add(5.0); record_2.add(8.0);
		record_3.add(3.0); record_3.add(6.0); record_3.add(9.0);
		matrix.add(record_1); matrix.add(record_2); matrix.add(record_3);
		
		
		DiscoveryTools tools = new DiscoveryTools();
		double actual = tools.getVariance(matrix, 1, 5.0);
		
		assertEquals(expected, actual, 0.0000000001);
	}
	
	public void testGetVarianceSecond() {
		
		double expected = 0.666666666666666;
		
		ArrayList<Double> record = new ArrayList<Double>();
		record.add(4.0); record.add(5.0); record.add(6.0);
		
		DiscoveryTools tools = new DiscoveryTools();
		double actual = tools.getVariance(record, 5.0);
		
		assertEquals(expected, actual, 0.0000000001);
	}
	
	public void testgetStdDev() {
		
		double expected = 2.0;
		
		DiscoveryTools tools = new DiscoveryTools();
		double actual = tools.getStdDev(4.0);
		
		assertEquals(expected, actual);
	}
	
	public void testGetSingleDensities() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(31.02392654); expected.add(46.3398898); expected.add(61.65585307);
		
		ArrayList<Double> mean = new ArrayList<Double>();
		mean.add(20.0); mean.add(30.0); mean.add(40.0);
		
		ArrayList<Double> std = new ArrayList<Double>();
		std.add(2.0); std.add(4.0); std.add(6.0);
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<Double> actual = tools.getSingleDensities(mean, std, 100);
		
		for(int i=0; i<expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i), 0.0000001);
		}
	}
	
	public void testGetDeltadCombinations() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(15.51196327); expected.add(24.85417289);
		expected.add(62.47044693);
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> record_1 = new ArrayList<Double>();
		ArrayList<Double> record_2 = new ArrayList<Double>();
		ArrayList<Double> record_3 = new ArrayList<Double>();
		record_1.add(1.0); record_1.add(4.0); record_1.add(7.0);
		record_2.add(2.0); record_2.add(5.0); record_2.add(8.0);
		record_3.add(3.0); record_3.add(6.0); record_3.add(9.0);
		matrix.add(record_1); matrix.add(record_2); matrix.add(record_3);
		
		ArrayList<Double> mean = new ArrayList<Double>();
		mean.add(2.0); mean.add(5.0); mean.add(8.0);
		
		int[] comb = {-1, -1};
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<Double> actual = tools.getDeltadCombinations(matrix, 
								new ArrayList<Double>(), mean, comb, 0, 0, 100);
		
		for(int i=0; i<expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i), 0.000001);
		}
	}
	
	public void testChangeFeaturesLabel() {
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("label_A"); expected.add("label_B"); expected.add("label_C");
		
		ArrayList<String> features = new ArrayList<String>();
		features.add("feature(A)"); features.add("feature(B)"); features.add("feature(C)");
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<String> actual = tools.changeFeaturesLabel("feature", "label_", features);
		
		assertEquals(expected, actual);
	}
	
	public void testGetFeaturesLabel() {
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("density(A)"); expected.add("density(B)"); expected.add("density(C)");
		
		ArrayList<String> features = new ArrayList<String>();
		features.add("A"); features.add("B"); features.add("C");
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<String> actual = tools.getFeaturesLabel("density", features);
		
		assertEquals(expected, actual);
	}
	
	public void getFeaturesForCombinations() {
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("deltad(A AND B)"); expected.add("deltad(A AND C");
		expected.add("deltad(B AND C)");
		
		ArrayList<String> features = new ArrayList<String>();
		features.add("A"); features.add("B"); features.add("C");
		
		int[] comb = {-1, -1};
		
		DiscoveryTools tools = new DiscoveryTools();
		ArrayList<String> actual = tools.getFeaturesForCombinations
								(new ArrayList<String>(), features, comb, 0, 0);
		
		assertEquals(expected, actual);
	}
}
