package it.unito.geosummly.tools;

import it.unito.geosummly.BoundingBox;

import java.math.BigDecimal;
import java.util.ArrayList;

import junit.framework.TestCase;

public class ImportToolsTest extends TestCase {
	
	public void testBuildListZero() {
		
		int size=5;
		
		ArrayList<Double> expected=new ArrayList<Double>();
		for(int i=0; i<size; i++)
			expected.add(0.0);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.buildListZero(size);
		
		assertEquals(expected, actual);
	}
	
	public void testGetAreas() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(10.0); expected.add(20.0); expected.add(30.0);
		
		BoundingBox b1 = new BoundingBox();
		b1.setArea(new BigDecimal(10));
		BoundingBox b2 = new BoundingBox();
		b2.setArea(new BigDecimal(20));
		BoundingBox b3 = new BoundingBox();
		b3.setArea(new BigDecimal(30));
		ArrayList<BoundingBox> bbox = new ArrayList<BoundingBox>();
		bbox.add(b1); bbox.add(b2); bbox.add(b3);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.getAreas(bbox);
		
		assertEquals(expected, actual);
	}
	
	public void testGetAreasFromFocalPoints() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(1.236544); expected.add(1.236544); expected.add(1.236544);
		
		ArrayList<BoundingBox> bbox = new ArrayList<BoundingBox>();
		BoundingBox b1 = new BoundingBox();
		b1.setCenterLat(new BigDecimal(45.51)); b1.setCenterLng(new BigDecimal(7.5));
		BoundingBox b2 = new BoundingBox();
		b2.setCenterLat(new BigDecimal(45.52)); b2.setCenterLng(new BigDecimal(7.5));
		BoundingBox b3 = new BoundingBox();
		b3.setCenterLat(new BigDecimal(45.53)); b3.setCenterLng(new BigDecimal(7.5));
		bbox.add(b1); bbox.add(b2); bbox.add(b3);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.getAreasFromFocalPoints(bbox, 3);
		
		for(int i=0; i<expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i), 0.01);
	}
	
	public void testGetFocalPoints() {
		
		ArrayList<BoundingBox> expected = new ArrayList<BoundingBox>();
		BoundingBox b1 = new BoundingBox();
		b1.setCenterLat(new BigDecimal(3.0)); b1.setCenterLng(new BigDecimal(4.0));
		BoundingBox b2 = new BoundingBox();
		b2.setCenterLat(new BigDecimal(9.0)); b2.setCenterLng(new BigDecimal(10.0));
		BoundingBox b3 = new BoundingBox();
		b3.setCenterLat(new BigDecimal(15.0)); b3.setCenterLng(new BigDecimal(16.0));
		expected.add(b1); expected.add(b2); expected.add(b3);
		
		ArrayList<ArrayList<Double>> matrix =
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(1.0); rec_1.add(2.0); rec_1.add(3.0);
		rec_1.add(4.0); rec_1.add(5.0); rec_1.add(6.0);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(1.0); rec_2.add(2.0); rec_2.add(3.0);
		rec_2.add(4.0); rec_2.add(50.0); rec_2.add(60.0);
		ArrayList<Double> rec_3 = new ArrayList<Double>();
		rec_3.add(7.0); rec_3.add(8.0); rec_3.add(9.0);
		rec_3.add(10.0); rec_3.add(11.0); rec_3.add(12.0);
		ArrayList<Double> rec_4 = new ArrayList<Double>();
		rec_4.add(13.0); rec_4.add(14.0); rec_4.add(15.0);
		rec_4.add(16.0); rec_4.add(17.0); rec_4.add(18.0);
		matrix.add(rec_1); matrix.add(rec_2); matrix.add(rec_3);
		matrix.add(rec_4);
		
		ImportTools tools = new ImportTools();
		ArrayList<BoundingBox> actual = tools.getFocalPoints(matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testGroupSinglesToCell() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(0.1); expected.add(0.2);
		expected.add(0.0); expected.add(2.0);
		expected.add(1.0);
		
		BoundingBox bbox = new BoundingBox();
		bbox.setCenterLat(new BigDecimal(0.1));
		bbox.setCenterLng(new BigDecimal(0.2));
		ArrayList<ArrayList<Double>> matrix = 
								new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(0.3); rec_1.add(0.4); rec_1.add(0.0);
		rec_1.add(1.0); rec_1.add(0.0);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(0.1); rec_2.add(0.2); rec_2.add(0.0);
		rec_2.add(1.0); rec_2.add(0.0);
		ArrayList<Double> rec_3 = new ArrayList<Double>();
		rec_3.add(0.1); rec_3.add(0.2); rec_3.add(0.0);
		rec_3.add(0.0); rec_3.add(1.0);
		ArrayList<Double> rec_4 = new ArrayList<Double>();
		rec_4.add(0.3); rec_4.add(0.4); rec_4.add(1.0);
		rec_4.add(0.0); rec_4.add(0.0);
		ArrayList<Double> rec_5 = new ArrayList<Double>();
		rec_5.add(0.5); rec_5.add(0.6); rec_5.add(0.0);
		rec_5.add(0.0); rec_5.add(1.0);
		ArrayList<Double> rec_6 = new ArrayList<Double>();
		rec_6.add(0.1); rec_6.add(0.2); rec_6.add(0.0);
		rec_6.add(1.0); rec_6.add(0.0);
		matrix.add(rec_1); matrix.add(rec_2); matrix.add(rec_3);
		matrix.add(rec_4); matrix.add(rec_5); matrix.add(rec_6);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.groupSinglesToCell(bbox, matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testBuildFrequencyMatrix() {
		
		ArrayList<Double> r_1 = new ArrayList<Double>();
		r_1.add(0.1); r_1.add(0.2);
		r_1.add(0.0); r_1.add(2.0);
		r_1.add(1.0);
		ArrayList<Double> r_2 = new ArrayList<Double>();
		r_2.add(0.1); r_2.add(0.2);
		r_2.add(0.0); r_2.add(2.0);
		r_2.add(1.0);
		
		ArrayList<ArrayList<Double>> expected = 
						new ArrayList<ArrayList<Double>>();
		expected.add(r_1); expected.add(r_2);
		
		BoundingBox b1 = new BoundingBox();
		b1.setCenterLat(new BigDecimal(0.1));
		b1.setCenterLng(new BigDecimal(0.2));
		BoundingBox b2 = new BoundingBox();
		b2.setCenterLat(new BigDecimal(0.1));
		b2.setCenterLng(new BigDecimal(0.2));
		ArrayList<BoundingBox> bbox = new ArrayList<BoundingBox>();
		bbox.add(b1); bbox.add(b2);
		
		ArrayList<ArrayList<Double>> matrix = 
								new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(0.3); rec_1.add(0.4); rec_1.add(0.0);
		rec_1.add(1.0); rec_1.add(0.0);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(0.1); rec_2.add(0.2); rec_2.add(0.0);
		rec_2.add(1.0); rec_2.add(0.0);
		ArrayList<Double> rec_3 = new ArrayList<Double>();
		rec_3.add(0.1); rec_3.add(0.2); rec_3.add(0.0);
		rec_3.add(0.0); rec_3.add(1.0);
		ArrayList<Double> rec_4 = new ArrayList<Double>();
		rec_4.add(0.3); rec_4.add(0.4); rec_4.add(1.0);
		rec_4.add(0.0); rec_4.add(0.0);
		ArrayList<Double> rec_5 = new ArrayList<Double>();
		rec_5.add(0.5); rec_5.add(0.6); rec_5.add(0.0);
		rec_5.add(0.0); rec_5.add(1.0);
		ArrayList<Double> rec_6 = new ArrayList<Double>();
		rec_6.add(0.1); rec_6.add(0.2); rec_6.add(0.0);
		rec_6.add(1.0); rec_6.add(0.0);
		matrix.add(rec_1); matrix.add(rec_2); matrix.add(rec_3);
		matrix.add(rec_4); matrix.add(rec_5); matrix.add(rec_6);
		
		ImportTools tools = new ImportTools();
		ArrayList<ArrayList<Double>> actual = tools.buildFrequencyMatrix(bbox, matrix);
		
		assertEquals(expected, actual);
	}
		
	public void testBuildDensityMatrix() {
		
		ArrayList<ArrayList<Double>> expected = 
						new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		ArrayList<Double> exp2=new ArrayList<Double>();
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp1.add(10.0); exp1.add(20.0); exp1.add(0.2);
		exp1.add(0.4);
		exp2.add(30.0); exp2.add(40.0); exp2.add(0.8);
		exp2.add(1.6);
		exp3.add(50.0); exp3.add(60.0); exp3.add(3.2);
		exp3.add(6.4);
		expected.add(exp1); expected.add(exp2); expected.add(exp3);
		
		ImportTools tools = new ImportTools();
		ArrayList<ArrayList<Double>> matrix = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a=new ArrayList<Double>();
		ArrayList<Double> b=new ArrayList<Double>();
		ArrayList<Double> c=new ArrayList<Double>();
		a.add(10.0); a.add(20.0); a.add(2.0); a.add(4.0);
		b.add(30.0); b.add(40.0); b.add(8.0); b.add(16.0);
		c.add(50.0); c.add(60.0); c.add(32.0); c.add(64.0);
		matrix.add(a); matrix.add(b); matrix.add(c);
		
		ArrayList<Double> area=new ArrayList<Double>();
		area.add(10.0); area.add(10.0); area.add(10.0);
		ArrayList<ArrayList<Double>> actual = 
						tools.buildDensityMatrix(CoordinatesNormalizationType.NORM, 
												 matrix, 
												 area);
		
		assertEquals(expected, actual);
		
		ArrayList<ArrayList<Double>> expected1 = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp11=new ArrayList<Double>();
		ArrayList<Double> exp21=new ArrayList<Double>();
		ArrayList<Double> exp31=new ArrayList<Double>();
		exp11.add(0.2); exp11.add(0.4);
		exp21.add(0.8); exp21.add(1.6);
		exp31.add(3.2); exp31.add(6.4);
		expected1.add(exp11); expected1.add(exp21); expected1.add(exp31);
		
		ArrayList<ArrayList<Double>> matrix1 = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a1=new ArrayList<Double>();
		ArrayList<Double> b1=new ArrayList<Double>();
		ArrayList<Double> c1=new ArrayList<Double>();
		a1.add(2.0); a1.add(4.0);
		b1.add(8.0); b1.add(16.0);
		c1.add(32.0); c1.add(64.0);
		matrix1.add(a1); matrix1.add(b1); matrix1.add(c1);
		
		ArrayList<Double> area1=new ArrayList<Double>();
		area1.add(10.0); area1.add(10.0); area1.add(10.0);
		ArrayList<ArrayList<Double>> actual1 = 
						tools.buildDensityMatrix(CoordinatesNormalizationType.MISSING, 
												matrix1, 
												area1);
		
		assertEquals(expected1, actual1);
	}
	
	public void testBuildNormalizedMatrix() {
		
		ArrayList<ArrayList<Double>> expected = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> exp1=new ArrayList<Double>();
		ArrayList<Double> exp2=new ArrayList<Double>();
		ArrayList<Double> exp3=new ArrayList<Double>();
		exp1.add(0.0); exp1.add(0.0); exp1.add(0.0);
		exp1.add(0.0);
		exp2.add(0.5); exp2.add(0.5); exp2.add(0.5);
		exp2.add(0.5);
		exp3.add(1.0); exp3.add(1.0); exp3.add(1.0);
		exp3.add(1.0);
		expected.add(exp1); expected.add(exp2); expected.add(exp3);
		
		ArrayList<ArrayList<Double>> matrix = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a=new ArrayList<Double>();
		ArrayList<Double> b=new ArrayList<Double>();
		ArrayList<Double> c=new ArrayList<Double>();
		a.add(10.0); a.add(20.0); a.add(2.0); a.add(2.0);
		b.add(30.0); b.add(40.0); b.add(3.0); b.add(3.0);
		c.add(50.0); c.add(60.0); c.add(4.0); c.add(4.0);
		matrix.add(a); matrix.add(b); matrix.add(c);
		
		ImportTools tools = new ImportTools();
		ArrayList<ArrayList<Double>> actual = 
						tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, 
													matrix);
		
		for(int i=0;i<actual.size();i++)
			for(int j=0;j<actual.get(i).size();j++)
				assertEquals(expected.get(i).get(j), actual.get(i).get(j), 0.001);
	}
	
	public void testGetSumArray() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(13.0); expected.add(16.0);
		
		ArrayList<Double> a = new ArrayList<Double>();
		a.add(5.0); a.add(6.0); a.add(5.0); a.add(6.0);
		ArrayList<Double> b = new ArrayList<Double>();
		b.add(2.0); b.add(3.0); b.add(2.0); b.add(3.0);
		ArrayList<Double> c = new ArrayList<Double>();
		c.add(6.0); c.add(7.0); c.add(6.0); c.add(7.0);
		
		ArrayList<ArrayList<Double>> abc = 
						new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.getSumArray(2, abc);
		
		assertEquals(expected, actual);
	}
	
	public void testGetSum() {
		
		double expected=13;
		
		ArrayList<Double> a = new ArrayList<Double>();
		a.add(5.0);
		ArrayList<Double> b = new ArrayList<Double>();
		b.add(2.0);
		ArrayList<Double> c = new ArrayList<Double>();
		c.add(6.0);
		ArrayList<ArrayList<Double>> abc = 
						new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tm=new ImportTools();
		double actual=tm.getSum(abc, 0);
		
		assertEquals(expected, actual);
	}
	
	public void testGetIntraFeatureNormalizationNoCoord() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(0.25); expected.add(0.0); expected.add(0.0);
		
		ArrayList<Double> record = new ArrayList<Double>();
		record.add(5.0); record.add(0.0); record.add(0.0);
		ArrayList<Double> sumArray = new ArrayList<Double>();
		sumArray.add(20.0); sumArray.add(30.0); sumArray.add(0.0);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = 
					tools.getIntraFeatureNormalizationNoCoord(record, 
															  sumArray);
		
		assertEquals(expected, actual);
	}
	
	public void testGetIntraFeatureNormalization() {
		
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(45.0); expected.add(7.0);
		expected.add(0.25); expected.add(0.0); expected.add(0.0);
		
		ArrayList<Double> record = new ArrayList<Double>();
		record.add(45.0); record.add(7.0);
		record.add(5.0); record.add(0.0); record.add(0.0);
		ArrayList<Double> sumArray = new ArrayList<Double>();
		sumArray.add(20.0); sumArray.add(30.0); sumArray.add(0.0);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = 
					tools.getIntraFeatureNormalization(record, sumArray);
		
		assertEquals(expected, actual);
	}
	
	public void testGetMin() {
		
		double expected=3.0;
		
		ArrayList<Double> a=new ArrayList<Double>();
		a.add(5.0); a.add(3.0);
		ArrayList<Double> b=new ArrayList<Double>();
		b.add(2.0); b.add(4.0);
		ArrayList<Double> c=new ArrayList<Double>();
		c.add(6.0); c.add(10.0);
		ArrayList<ArrayList<Double>> abc=new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tools = new ImportTools();
		double actual = tools.getMin(abc, 1);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testGetMinArray() {
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(2.0); expected.add(3.0);
		
		ArrayList<Double> a = new ArrayList<Double>();
		a.add(5.0); a.add(3.0);
		ArrayList<Double> b = new ArrayList<Double>();
		b.add(2.0); b.add(4.0);
		ArrayList<Double> c = new ArrayList<Double>();
		c.add(6.0); c.add(10.0);
		ArrayList<ArrayList<Double>> abc = 
						new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.getMinArray(abc);
		
		assertEquals(expected, actual);
	}
	
	public void testGetMax() {

		double expected=10.0;
		
		ArrayList<Double> a = new ArrayList<Double>();
		a.add(5.0); a.add(3.0);
		ArrayList<Double> b = new ArrayList<Double>();
		b.add(2.0); b.add(4.0);
		ArrayList<Double> c = new ArrayList<Double>();
		c.add(6.0); c.add(10.0);
		ArrayList<ArrayList<Double>> abc = 
					new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tools = new ImportTools();
		double actual = tools.getMax(abc, 1);

		assertEquals(expected, actual, 0);
	}
	
	public void testGetMaxArray() {
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(6.0); expected.add(10.0);
		
		ArrayList<Double> a = new ArrayList<Double>();
		a.add(5.0); a.add(3.0);
		ArrayList<Double> b = new ArrayList<Double>();
		b.add(2.0); b.add(4.0);
		ArrayList<Double> c = new ArrayList<Double>();
		c.add(6.0); c.add(10.0);
		ArrayList<ArrayList<Double>> abc = 
						new ArrayList<ArrayList<Double>>();
		abc.add(a); abc.add(b); abc.add(c);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.getMaxArray(abc);
		
		//Start the test
		assertEquals(expected, actual);
	}
	
	public void testNormalizeRow() {
		
		ArrayList<Double> expected=new ArrayList<Double>();
		expected.add(0.5); expected.add(0.0);
		
		ArrayList<Double> record=new ArrayList<Double>();
		record.add(6.0); record.add(16.0);
		ArrayList<Double> minArray=new ArrayList<Double>();
		minArray.add(4.0); minArray.add(16.0);
		ArrayList<Double> maxArray=new ArrayList<Double>();
		maxArray.add(8.0); maxArray.add(32.0);
		
		ImportTools tools = new ImportTools();
		ArrayList<Double> actual = tools.normalizeRow(CoordinatesNormalizationType.NORM, 
												      record, 
												      minArray, 
												      maxArray);
		
		assertEquals(expected, actual);
	}
	
	public void testNormalizeValues() {
		
		double expected=0.75;
		
		ImportTools tools = new ImportTools();
		double actual = tools.normalizeValues(-90, 90, 45);
		
		assertEquals(expected, actual);
	}
	
	public void testGetFeaturesLabel() {
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Timestamps(ms)");
		expected.add("Latitude"); expected.add("Longitude");
		expected.add("d(A)"); expected.add("d(B)");
		expected.add("d(C)");
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("Latitude"); a.add("Longitude");
		a.add("A"); a.add("B"); a.add("C");
		
		ImportTools tools = new ImportTools();
		ArrayList<String> actual = 
					tools.getFeaturesLabel(CoordinatesNormalizationType.NORM, 
										   "d", 
										   a);

		assertEquals(expected, actual);
		
		ArrayList<String> expected1 = new ArrayList<String>();
		expected1.add("d(A)"); expected1.add("d(B)");
		expected1.add("d(C)");
		
		ArrayList<String> a1=new ArrayList<String>();
		a1.add("Latitude"); a1.add("Longitude");
		a1.add("A"); a1.add("B"); a1.add("C");
		
		ArrayList<String> actual1 = 
					tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, 
										   "d", 
										   a1);
		
		assertEquals(expected1, actual1);
	}
	
	public void testGetDistance() {
		
		double expected = 1.112;
		
		ImportTools tools = new ImportTools();
		double actual = tools.getDistance(45.51, 7.5, 45.52, 7.5);
		
		assertEquals(expected, actual, 0.01);
	}
}