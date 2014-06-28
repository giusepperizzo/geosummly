package it.unito.geosummly.tools;

import it.unito.geosummly.BoundingBox;

import java.math.BigDecimal;
import java.util.ArrayList;

import junit.framework.TestCase;

public class EvaluationToolsTest extends TestCase {
	
	public void testBuildFrequencyRandomMatrix() {
		
		ArrayList<ArrayList<Double>> expected = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(1.0); rec_1.add(2.0); rec_1.add(3.0);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(4.0); rec_2.add(5.0); rec_2.add(6.0);
		ArrayList<Double> rec_3 = new ArrayList<Double>();
		rec_3.add(7.0); rec_3.add(8.0); rec_3.add(9.0);
		expected.add(rec_1); expected.add(rec_2); expected.add(rec_3);
		
		int size = 3;
		ArrayList<Double> minArray = new ArrayList<Double>();
		minArray.add(1.0); minArray.add(2.0); minArray.add(3.0);
		ArrayList<Double> maxArray = new ArrayList<Double>();
		maxArray.add(10.0); maxArray.add(11.0); maxArray.add(12.0);
		EvaluationTools tools = new EvaluationTools();
		ArrayList<ArrayList<Double>> actual = tools.buildFrequencyRandomMatrix
														(size, minArray, maxArray);
		
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for(int i=0; i<expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			
			for(int j=0; j<expected.get(i).size(); j++) {
				assertTrue(expected.get(i).get(j) <= maxArray.get(j));
				assertTrue(expected.get(i).get(j) >= minArray.get(j));
				assertTrue(actual.get(i).get(j) <= maxArray.get(j));
				assertTrue(actual.get(i).get(j) >= minArray.get(j));
			}
		}
	}
	
	public void testCreateFolds() {
		
		ArrayList<ArrayList<Double>> matrix =
							new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> r1 = new ArrayList<Double>();
		r1.add(0.11); r1.add(0.12); r1.add(0.13);
		ArrayList<Double> r2 = new ArrayList<Double>();
		r2.add(0.21); r2.add(0.22); r2.add(0.23);
		ArrayList<Double> r3 = new ArrayList<Double>();
		r3.add(0.31); r3.add(0.31); r3.add(0.33);
		ArrayList<Double> r4 = new ArrayList<Double>();
		r4.add(0.41); r4.add(0.42); r4.add(0.43);
		ArrayList<Double> r5 = new ArrayList<Double>();
		r5.add(0.51); r5.add(0.52); r5.add(0.53);
		ArrayList<Double> r6 = new ArrayList<Double>();
		r6.add(0.61); r6.add(0.62); r6.add(0.63);
		ArrayList<Double> r7 = new ArrayList<Double>();
		r7.add(0.71); r7.add(0.72); r7.add(0.73);
		matrix.add(r1); matrix.add(r2); matrix.add(r3);
		matrix.add(r4); matrix.add(r5); matrix.add(r6);
		matrix.add(r7);
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<ArrayList<ArrayList<Double>>> actual =
								tools.createFolds(matrix, 3);
		
		assertTrue(actual.size() == 3);
		for(ArrayList<ArrayList<Double>> array: actual) {
			assertTrue(array.size() == 2);
		}
		
		assertNotSame(actual.get(0), actual.get(1));
		assertNotSame(actual.get(0), actual.get(2));
		assertNotSame(actual.get(1), actual.get(2));
	}
	
	public void testRemoveVenueCoordinates() {
		
		ArrayList<ArrayList<ArrayList<Double>>> expected = 
						new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> genericMatrix = 
						new ArrayList<ArrayList<Double>>();
		ArrayList<Double> genericRecord = new ArrayList<Double>();
		genericRecord.add(3.0); genericRecord.add(4.0); 
		genericRecord.add(5.0); genericRecord.add(6.0);
		genericMatrix.add(genericRecord);
		genericMatrix.add(genericRecord);
		genericMatrix.add(genericRecord);
		for(int i=0; i<3; i++)
			expected.add(genericMatrix);
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<ArrayList<ArrayList<Double>>> folds = 
						new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> foldsMatrix = 
						new ArrayList<ArrayList<Double>>();
		ArrayList<Double> foldsRecord = new ArrayList<Double>();
		foldsRecord.add(1.0); foldsRecord.add(2.0); foldsRecord.add(3.0);
		foldsRecord.add(4.0); foldsRecord.add(5.0); foldsRecord.add(6.0);
		foldsMatrix.add(foldsRecord);
		foldsMatrix.add(foldsRecord);
		foldsMatrix.add(foldsRecord);
		for(int i=0; i<3; i++)
			folds.add(foldsMatrix);
		ArrayList<ArrayList<ArrayList<Double>>> actual =
						tools.removeVenueCoordinates(folds);
		
		assertEquals(expected, actual);
		
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
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<BoundingBox> actual = tools.getFocalPoints(matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testGetDistance() {
		
		double expected = 1.112;
		
		EvaluationTools tools = new EvaluationTools();
		double actual = tools.getDistance(45.51, 7.5, 45.52, 7.5);
		
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
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<Double> actual = tools.getAreasFromFocalPoints(bbox, 3);
		
		for(int i=0; i<expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i), 0.001);
	}
	
	public void testBuildListZero() {
		
		int size = 5;
		ArrayList<Double> expected = new ArrayList<Double>();
		for(int i=0; i<size; i++)
			expected.add(0.0);
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<Double> actual = tools.buildListZero(size);
		
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
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<Double> actual = tools.groupSinglesToCell(bbox, matrix);
		
		assertEquals(expected, actual);
	}
	
	public void testGroupFolds() {
		
		ArrayList<ArrayList<ArrayList<Double>>> expected = 
								new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> m = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> m_1 = new ArrayList<Double>();
		m_1.add(0.1); m_1.add(0.2); m_1.add(0.0); 
		m_1.add(2.0); m_1.add(1.0);
		ArrayList<Double> m_2 = new ArrayList<Double>();
		m_2.add(0.3); m_2.add(0.4); m_2.add(1.0); 
		m_2.add(0.0); m_2.add(2.0);
		m.add(m_1); m.add(m_2);
		expected.add(m); expected.add(m);
		
		ArrayList<BoundingBox> bbox = new ArrayList<BoundingBox>();
		BoundingBox b_1 = new BoundingBox();
		b_1.setCenterLat(new BigDecimal(0.1)); b_1.setCenterLng(new BigDecimal(0.2));
		BoundingBox b_2 = new BoundingBox();
		b_2.setCenterLat(new BigDecimal(0.3)); b_2.setCenterLng(new BigDecimal(0.4));
		bbox.add(b_1); bbox.add(b_2);
		
		ArrayList<ArrayList<ArrayList<Double>>> folds =
						new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> matrix = 
				new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(0.3); rec_1.add(0.4); rec_1.add(1.0);
		rec_1.add(0.0); rec_1.add(0.0);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(0.1); rec_2.add(0.2); rec_2.add(0.0);
		rec_2.add(1.0); rec_2.add(0.0);
		ArrayList<Double> rec_3 = new ArrayList<Double>();
		rec_3.add(0.1); rec_3.add(0.2); rec_3.add(0.0);
		rec_3.add(0.0); rec_3.add(1.0);
		ArrayList<Double> rec_4 = new ArrayList<Double>();
		rec_4.add(0.3); rec_4.add(0.4); rec_4.add(0.0);
		rec_4.add(0.0); rec_4.add(1.0);
		ArrayList<Double> rec_5 = new ArrayList<Double>();
		rec_5.add(0.3); rec_5.add(0.4); rec_5.add(0.0);
		rec_5.add(0.0); rec_5.add(1.0);
		ArrayList<Double> rec_6 = new ArrayList<Double>();
		rec_6.add(0.1); rec_6.add(0.2); rec_6.add(0.0);
		rec_6.add(1.0); rec_6.add(0.0);
		matrix.add(rec_1); matrix.add(rec_2); matrix.add(rec_3);
		matrix.add(rec_4); matrix.add(rec_5); matrix.add(rec_6);
		folds.add(matrix); folds.add(matrix);
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<ArrayList<ArrayList<Double>>> actual =
						tools.groupFolds(bbox, folds);
		
		assertEquals(expected, actual);		
	}
	
	public void testComputeJaccard() {
		
		
	}
	
	public void testGetSSEDiscard() {
		
		double expected = 50.0;
		
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(0.6); values.add(0.9); values.add(0.4);
		values.add(0.9);
		
		EvaluationTools tools = new EvaluationTools();
		double actual = tools.getSSEDiscard(values, 0.2);
		
		assertEquals(expected, actual);
	}
	
	public void testChangeFeaturesLabel() {
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("cat_1"); expected.add("cat_2");
		expected.add("cat_3"); expected.add("cat_4");
		
		ArrayList<String> features = new ArrayList<String>();
		features.add("f(cat_1)"); features.add("f(cat_2)");
		features.add("f(cat_3)"); features.add("f(cat_4)");
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<String> actual = tools.changeFeaturesLabel("f", "", features);
		
		assertEquals(expected, actual);
	}
	
	public void getFeaturesLabelNoTimestamp() {
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("Latitude"); expected.add("Longitude");
		expected.add("f(cat_1)"); expected.add("f(cat_2)");
		expected.add("f(cat_3)");
		
		ArrayList<String> expectedNoCoord = new ArrayList<String>();
		expectedNoCoord.add("f(cat_1)"); expectedNoCoord.add("f(cat_2)");
		expectedNoCoord.add("f(cat_3)");
		
		ArrayList<String> features = new ArrayList<String>();
		features.add("Latitude"); features.add("Longitude"); 
		features.add("cat_1"); features.add("cat_2"); 
		features.add("cat_3");
		
		ArrayList<String> featuresNoCoord = new ArrayList<String>();
		featuresNoCoord.add("cat_1"); featuresNoCoord.add("cat_2"); 
		featuresNoCoord.add("cat_3");
		
		CoordinatesNormalizationType typeYes =
						CoordinatesNormalizationType.NORM;
		CoordinatesNormalizationType typeNo =
				CoordinatesNormalizationType.MISSING;
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<String> actual = 
				tools.getFeaturesLabelNoTimestamp(typeYes, "f", features);
		ArrayList<String> actualNoCoord =
				tools.getFeaturesLabelNoTimestamp(typeNo, "f", featuresNoCoord);
		
		assertEquals(expected, actual);
		assertEquals(expectedNoCoord, actualNoCoord);
	}
	
	
	public void testBuildNorm() {
		
		ArrayList<ArrayList<Double>> norm = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_1 = new ArrayList<Double>();
		rec_1.add(0.1); rec_1.add(0.2); rec_1.add(0.3); rec_1.add(0.4);
		rec_1.add(0.5);
		ArrayList<Double> rec_2 = new ArrayList<Double>();
		rec_2.add(0.6); rec_2.add(0.7); rec_2.add(0.8); rec_2.add(0.9);
		rec_2.add(0.09);
		norm.add(rec_1); norm.add(rec_2);
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<ArrayList<Double>> actual = tools.buildNorm(norm);
		
		assertEquals(2, actual.size());
		for(ArrayList<Double> r: actual) {
			assertEquals(5, r.size());
			for(int i=0; i<r.size(); i++) {
				assertTrue(r.get(i)<=1 && r.get(i)>=0);
			}
		}
		
		assertEquals(0.1, norm.get(0).get(0));
		assertEquals(0.2, norm.get(0).get(1));
		assertEquals(0.6, norm.get(1).get(0));
		assertEquals(0.7, norm.get(1).get(1));
	}
	
public void testGetFeaturesLabel() {
		
		ArrayList<String> expected=new ArrayList<String>();
		expected.add("Latitude"); expected.add("Longitude");
		expected.add("d(A)"); expected.add("d(B)");
		expected.add("d(C)");
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("Latitude"); a.add("Longitude");
		a.add("A"); a.add("B"); a.add("C");
		
		EvaluationTools tools = new EvaluationTools();
		ArrayList<String> actual = tools.getFeaturesLabel("d", a);

		assertEquals(expected, actual);
	}
}
