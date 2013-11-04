package it.unito.geosummly;

import junit.framework.TestCase;

public class BoundingBoxTest extends TestCase {
	
	public void testGetDistance() {
		BoundingBox b=new BoundingBox();
		double actual= b.getDistance(45.0390186, 7.6600, 45.057, 7.6600);
		double expected=2000;
		assertEquals(expected, actual, 1);
	}
}