package it.unito.geosummly;

import java.util.ArrayList;

import junit.framework.TestCase;

public class GridTest extends TestCase {

	public void testCreateCells() {
		
		//Central cell
		BoundingBox cell=new BoundingBox(45.057, 45.0561, 7.6600, 7.6613);
		
		//Create the bounding box
		Grid grid=new Grid();
		grid.setCellsNumber(2);
		grid.setStructure(new ArrayList<BoundingBox>());
		grid.setBbox(new BoundingBox());
		grid.createCells();
		ArrayList<BoundingBox> lb=grid.getStructure();
		
		//Construct the test case
		BoundingBox b1=new BoundingBox(45.05745, 45.05655, 7.65935, 7.6606499999999995);
		BoundingBox b2=new BoundingBox(45.05745, 45.05655, 7.6606499999999995, 7.661949999999999);
		BoundingBox b3=new BoundingBox(45.05655, 45.05565, 7.65935, 7.6606499999999995);
		BoundingBox b4=new BoundingBox(45.05655, 45.05565, 7.6606499999999995, 7.661949999999999);
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
