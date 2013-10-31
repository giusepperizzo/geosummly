package it.unito.geosummly;

import java.util.ArrayList;

import junit.framework.TestCase;

public class GridTest extends TestCase {

	public void testCreateCells() {
		
		//Central cell
		BoundingBox bbox=new BoundingBox(45.057, 45.0390186, 7.6600, 7.6854548);
		
		//Create the bounding box
		Grid grid=new Grid();
		grid.setCellsNumber(2);
		grid.setBbox(bbox);
		grid.setStructure(new ArrayList<BoundingBox>());
		grid.createCells();
		ArrayList<BoundingBox> lb=grid.getStructure();
		
		//Construct the test case
		BoundingBox b1=new BoundingBox(45.057, 45.048009300000004, 7.66, 7.672727399999999);
		b1.setRow(1);
		b1.setColumn(1);
		BoundingBox b2=new BoundingBox(45.057, 45.048009300000004, 7.672727399999999, 7.685454799999999);
		b2.setRow(1);
		b2.setColumn(2);
		BoundingBox b3=new BoundingBox(45.048009300000004, 45.039018600000006, 7.66, 7.672727399999999);
		b3.setRow(2);
		b3.setColumn(1);
		BoundingBox b4=new BoundingBox(45.048009300000004, 45.039018600000006, 7.672727399999999, 7.685454799999999);
		b4.setRow(2);
		b4.setColumn(2);
		ArrayList<BoundingBox> lb1=new ArrayList<BoundingBox>();
		lb1.add(b1);
		lb1.add(b2);
		lb1.add(b3);
		lb1.add(b4);
		
		//Start the tests
		assertNotNull(lb);
		assertEquals(lb.size(),lb1.size());
		for(int i=0;i<lb.size();i++)
			assertTrue(lb.get(i).toString().equals(lb1.get(i).toString()));
	}
}
