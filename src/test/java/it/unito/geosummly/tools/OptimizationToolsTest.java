package it.unito.geosummly.tools;

import java.util.ArrayList;

import it.unito.geosummly.io.templates.FeatureCollectionTemplate;
import it.unito.geosummly.io.templates.FeaturePropertiesTemplate;
import it.unito.geosummly.io.templates.FeatureTemplate;
import it.unito.geosummly.io.templates.GeometryTemplate;
import it.unito.geosummly.io.templates.VenueTemplate;
import junit.framework.TestCase;

public class OptimizationToolsTest extends TestCase {
	
	public void testGetMultiPoints() {
		
		ArrayList<ArrayList<ArrayList<Double>>> expected =
						new ArrayList<ArrayList<ArrayList<Double>>>();
		
		//1st cluster
		ArrayList<ArrayList<Double>> cluster_1 =
						new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_11 = new ArrayList<Double>();
		rec_11.add(10.0); rec_11.add(11.0);
		ArrayList<Double> rec_12 = new ArrayList<Double>();
		rec_12.add(12.0); rec_12.add(13.0);
		cluster_1.add(rec_11); cluster_1.add(rec_12);
		
		//2nd cluster
		ArrayList<ArrayList<Double>> cluster_2 =
						new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec_21 = new ArrayList<Double>();
		rec_21.add(14.0); rec_21.add(15.0);
		ArrayList<Double> rec_22 = new ArrayList<Double>();
		rec_22.add(16.0); rec_22.add(17.0);
		ArrayList<Double> rec_23 = new ArrayList<Double>();
		rec_23.add(18.0); rec_23.add(19.0);
		cluster_2.add(rec_21); cluster_2.add(rec_22); 
		cluster_2.add(rec_23);
		
		expected.add(cluster_1); expected.add(cluster_2);
		
		//1st feature template
		ArrayList<ArrayList<Double>> coordinates_1 =
					new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> c_11 = new ArrayList<Double>();
		c_11.add(10.0); c_11.add(11.0);
		
		ArrayList<Double> c_12 = new ArrayList<Double>();
		c_12.add(12.0); c_12.add(13.0);
		
		coordinates_1.add(c_11); coordinates_1.add(c_12);
		
		GeometryTemplate g_1 = new GeometryTemplate();
		g_1.setCoordinates(coordinates_1);
		
		FeatureTemplate ft_1 = new FeatureTemplate();
		ft_1.setGeometry(g_1);
		
		//2nd feature template
		ArrayList<ArrayList<Double>> coordinates_2 =
				new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> c_21 = new ArrayList<Double>();
		c_21.add(14.0); c_21.add(15.0);
		
		ArrayList<Double> c_22 = new ArrayList<Double>();
		c_22.add(16.0); c_22.add(17.0);
		
		ArrayList<Double> c_23 = new ArrayList<Double>();
		c_23.add(18.0); c_23.add(19.0);
		
		coordinates_2.add(c_21); coordinates_2.add(c_22);
		coordinates_2.add(c_23);
		
		GeometryTemplate g_2 = new GeometryTemplate();
		g_2.setCoordinates(coordinates_2);
		
		FeatureTemplate ft_2 = new FeatureTemplate();
		ft_2.setGeometry(g_2);
		
		//List of FeatureTemplate
		ArrayList<FeatureTemplate> features = 
							new ArrayList<FeatureTemplate>();
		features.add(ft_1); features.add(ft_2);
		
		FeatureCollectionTemplate fct =
					new FeatureCollectionTemplate();
		
		fct.setFeatures(features);
		
		OptimizationTools tools = new OptimizationTools();
		ArrayList<ArrayList<ArrayList<Double>>> actual = 
										tools.getMultiPoints(fct);
		
		assertEquals(expected, actual);
	}
	
	public void testGetObjectsOfClusters() {
		
		ArrayList<ArrayList<ArrayList<Double>>> expected =
						new ArrayList<ArrayList<ArrayList<Double>>>();
		
		ArrayList<ArrayList<Double>> cl_1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> cell_11 = new ArrayList<Double>();
		cell_11.add(45.0); cell_11.add(7.0);
		ArrayList<Double> cell_12 = new ArrayList<Double>();
		cell_12.add(55.0); cell_12.add(17.0);
		cl_1.add(cell_11); cl_1.add(cell_12);
		
		ArrayList<ArrayList<Double>> cl_2 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> cell_21 = new ArrayList<Double>();
		cell_21.add(65.0); cell_21.add(27.0);
		ArrayList<Double> cell_22 = new ArrayList<Double>();
		cell_22.add(55.0); cell_22.add(17.0);
		cl_2.add(cell_21); cl_2.add(cell_22);
		
		expected.add(cl_1); expected.add(cl_2);
		
		ArrayList<VenueTemplate> cluster_1 = new ArrayList<VenueTemplate>();
		VenueTemplate vt_11 = new VenueTemplate(123456, 0, "venue_11", 45.11, 
												7.11, 45.0, 7.0, "cat_1");
		VenueTemplate vt_12 = new VenueTemplate(123456, 5, "venue_12", 45.12, 
												7.12, 45.0, 7.0, "cat_2");
		VenueTemplate vt_13 = new VenueTemplate(123456, 10, "venue_13", 55.13, 
												17.13, 55.0, 17.0, "cat_1");
		cluster_1.add(vt_11); cluster_1.add(vt_12); cluster_1.add(vt_13);
		
		ArrayList<VenueTemplate> cluster_2 = new ArrayList<VenueTemplate>();
		VenueTemplate vt_21 = new VenueTemplate(123456, 0, "venue_21", 65.21, 
												27.21, 65.0, 27.0, "cat_2");
		VenueTemplate vt_22 = new VenueTemplate(123456, 5, "venue_22", 65.22, 
												27.22, 65.0, 27.0, "cat_2");
		VenueTemplate vt_23 = new VenueTemplate(123456, 15, "venue_23", 55.13, 
												17.13, 55.0, 17.0, "cat_2");
		cluster_2.add(vt_21); cluster_2.add(vt_22); cluster_2.add(vt_23);
		
		ArrayList<ArrayList<VenueTemplate>> venues =
						new ArrayList<ArrayList<VenueTemplate>>();
		venues.add(cluster_1); venues.add(cluster_2);
		
		OptimizationTools tools = new OptimizationTools();
		ArrayList<ArrayList<ArrayList<Double>>> actual = 
								tools.getObjectsOfClusters(venues);
		
		assertEquals(expected, actual);
	}
	
	public void testIsPresent() {
		
		ArrayList<ArrayList<Double>> cells =
						new ArrayList<ArrayList<Double>>();
		
		//It will be left empty for the 3rd test
		ArrayList<ArrayList<Double>> cells_1 =
				new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> c1 = new ArrayList<Double>();
		c1.add(1.0); c1.add(2.0);
		ArrayList<Double> c2 = new ArrayList<Double>();
		c2.add(3.0); c2.add(4.0);
		ArrayList<Double> c3 = new ArrayList<Double>();
		c3.add(4.0); c3.add(5.0);
		
		cells.add(c1); cells.add(c2); cells.add(c3);
		
		ArrayList<Double> c4 = new ArrayList<Double>();
		c4.add(4.0); c4.add(5.0);
		ArrayList<Double> c5 = new ArrayList<Double>();
		c5.add(5.0); c5.add(6.0);
		
		OptimizationTools tools = new OptimizationTools();
		boolean actual_1 = tools.isPresent(cells, c4);
		boolean actual_2 = tools.isPresent(cells, c5);
		boolean actual_3 = tools.isPresent(cells_1, c4);
		
		assertTrue(actual_1);
		assertFalse(actual_2);
		assertFalse(actual_3);
	}
	
	public void testGetVenuesOfClusters() {
		
		ArrayList<ArrayList<VenueTemplate>> expected =
						new ArrayList<ArrayList<VenueTemplate>>();
		ArrayList<VenueTemplate> cluster_1 = 
						new ArrayList<VenueTemplate>();
		VenueTemplate cl_11 = new VenueTemplate(123456, 0, "venue_11", 45.11, 
												7.11, 45.0, 7.0, "cat_1");
		VenueTemplate cl_12 = new VenueTemplate(123456, 5, "venue_12", 45.12, 
												7.12, 45.0, 7.0, "cat_2");
		VenueTemplate cl_13 = new VenueTemplate(123456, 10, "venue_13", 55.13, 
												17.13, 55.0, 17.0, "cat_1");
		cluster_1.add(cl_11); cluster_1.add(cl_12); cluster_1.add(cl_13);
		
		ArrayList<VenueTemplate> cluster_2 = 
				new ArrayList<VenueTemplate>();
		VenueTemplate cl_21 = new VenueTemplate(123456, 0, "venue_21", 65.21, 
												27.21, 65.0, 27.0, "cat_2");
		VenueTemplate cl_22 = new VenueTemplate(123456, 5, "venue_22", 65.22, 
												27.22, 65.0, 27.0, "cat_2");
		VenueTemplate cl_23 = new VenueTemplate(123456, 15, "venue_23", 55.13, 
												17.13, 55.0, 17.0, "cat_2");
		cluster_2.add(cl_21); cluster_2.add(cl_22); cluster_2.add(cl_23);
		
		expected.add(cluster_1); expected.add(cluster_2);
		
		//1st feature
		FeatureTemplate ft_1 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_1 =
						new FeaturePropertiesTemplate();
		ArrayList<VenueTemplate> venues_1 = 
						new ArrayList<VenueTemplate>();
		VenueTemplate vt_11 = new VenueTemplate(123456, 0, "venue_11", 45.11, 
												7.11, 45.0, 7.0, "cat_1");
		VenueTemplate vt_12 = new VenueTemplate(123456, 5, "venue_12", 45.12, 
												7.12, 45.0, 7.0, "cat_2");
		VenueTemplate vt_13 = new VenueTemplate(123456, 10, "venue_13", 55.13, 
												17.13, 55.0, 17.0, "cat_1");
		venues_1.add(vt_11); venues_1.add(vt_12); venues_1.add(vt_13);
		fpt_1.setVenues(venues_1);
		ft_1.setProperties(fpt_1);
		
		//2nd feature
		FeatureTemplate ft_2 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_2 =
						new FeaturePropertiesTemplate();
		ArrayList<VenueTemplate> venues_2 = 
						new ArrayList<VenueTemplate>();
		VenueTemplate vt_21 = new VenueTemplate(123456, 0, "venue_21", 65.21, 
												27.21, 65.0, 27.0, "cat_2");
		VenueTemplate vt_22 = new VenueTemplate(123456, 5, "venue_22", 65.22, 
												27.22, 65.0, 27.0, "cat_2");
		VenueTemplate vt_23 = new VenueTemplate(123456, 15, "venue_23", 55.13, 
												17.13, 55.0, 17.0, "cat_2");
		venues_2.add(vt_21); venues_2.add(vt_22); venues_2.add(vt_23);
		fpt_2.setVenues(venues_2);
		ft_2.setProperties(fpt_2);
		
		//Get the feature collection
		ArrayList<FeatureTemplate> features =
						new ArrayList<FeatureTemplate>();
		features.add(ft_1); features.add(ft_2);
		FeatureCollectionTemplate fct =
				new FeatureCollectionTemplate();
		fct.setFeatures(features);
		
		OptimizationTools tools = new OptimizationTools();
		ArrayList<ArrayList<VenueTemplate>> actual =
								tools.getVenuesOfClusters(fct);
		
		assertEquals(expected.size(), actual.size());
		
		for(int i=0; i<expected.size(); i++) {
			
			for(int j=0; j<expected.get(i).size(); j++) {
				
				assertEquals(expected.get(i).get(j).getBeenHere(), 
							 actual.get(i).get(j).getBeenHere());
				assertEquals(expected.get(i).get(j).getCategory(), 
							 actual.get(i).get(j).getCategory());
				assertEquals(expected.get(i).get(j).getCentroidLatitude(), 
						 actual.get(i).get(j).getCentroidLatitude());
				assertEquals(expected.get(i).get(j).getCentroidLongitude(), 
						 actual.get(i).get(j).getCentroidLongitude());
				assertEquals(expected.get(i).get(j).getId(), 
						 actual.get(i).get(j).getId());
				assertEquals(expected.get(i).get(j).getTimestamp(), 
						 actual.get(i).get(j).getTimestamp());
				assertEquals(expected.get(i).get(j).getVenueLatitude(), 
						 actual.get(i).get(j).getVenueLatitude());
				assertEquals(expected.get(i).get(j).getVenueLongitude(), 
						 actual.get(i).get(j).getVenueLongitude());
				
			}
		}
	}
	
	public void testGetLabelsOfClusters() {
		
		ArrayList<String[]> expected = 
						new ArrayList<String[]>();
		String[] c1 = {"cat_1", "cat_2"};
		String[] c2 = {"cat_3"};
		expected.add(c1); expected.add(c2);
		
		FeatureCollectionTemplate fct = 
						new FeatureCollectionTemplate();
		FeatureTemplate ft_1 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_1 =
						new FeaturePropertiesTemplate();
		fpt_1.setName(" cat_1,cat_2 ");
		ft_1.setProperties(fpt_1);
		
		FeatureTemplate ft_2 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_2 =
						new FeaturePropertiesTemplate();
		fpt_2.setName("cat_3");
		ft_2.setProperties(fpt_2);
		
		ArrayList<FeatureTemplate> ft_array =
						new ArrayList<FeatureTemplate>();
		ft_array.add(ft_1); ft_array.add(ft_2);
		fct.setFeatures(ft_array);
		
		OptimizationTools tools = new OptimizationTools();
		ArrayList<String[]> actual = tools.getLabelsOfClusters(fct);
		
		assertEquals(expected.size(), actual.size());
		for(int i=0; i<expected.size(); i++)
			for(int j=0; j<expected.get(i).length; j++)
				assertEquals(expected.get(i)[j], 
							 actual.get(i)[j]);
	}
	
	public void testGetIdsOfClusters() {
		
		ArrayList<Integer> expected = 
						new ArrayList<Integer>();
		expected.add(1); expected.add(5);
		
		FeatureCollectionTemplate fct = 
						new FeatureCollectionTemplate();
		FeatureTemplate ft_1 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_1 =
						new FeaturePropertiesTemplate();
		fpt_1.setClusterId(1);
		ft_1.setProperties(fpt_1);
		
		FeatureTemplate ft_2 = 
						new FeatureTemplate();
		FeaturePropertiesTemplate fpt_2 =
						new FeaturePropertiesTemplate();
		fpt_2.setClusterId(5);
		ft_2.setProperties(fpt_2);
		
		ArrayList<FeatureTemplate> ft_array =
						new ArrayList<FeatureTemplate>();
		ft_array.add(ft_1); ft_array.add(ft_2);
		fct.setFeatures(ft_array);
		
		OptimizationTools tools = new OptimizationTools();
		ArrayList<Integer> actual = tools.getIdsOfClusters(fct);
		
		assertEquals(expected, actual);
	}
	
	public void testGetSpatialCoverage() {
		
	}
}
