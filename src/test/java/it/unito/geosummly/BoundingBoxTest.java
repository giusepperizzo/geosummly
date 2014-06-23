package it.unito.geosummly;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class BoundingBoxTest extends TestCase {
	
	public void testGetDistance() {
		BoundingBox b=new BoundingBox();
		double actual= b.getDistance(
					new BigDecimal(45.0390186), 
					new BigDecimal(7.6600), 
					new BigDecimal(45.057), 
					new BigDecimal(7.6600)
							);
		double expected=2;
		assertEquals(expected, actual, 1);
	}
}