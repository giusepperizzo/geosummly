package it.unito.geosummly;

import junit.framework.TestCase;

public class BoundingBoxTest extends TestCase {
	
	public void testGetDistance() {
		BoundingBox b=new BoundingBox();
		double actual= b.getDistance(
					new Double(45.0390186), 
					new Double(7.6600), 
					new Double(45.057), 
					new Double(7.6600) );
		double expected=2;
		assertEquals(expected, actual, 1);
	}
}