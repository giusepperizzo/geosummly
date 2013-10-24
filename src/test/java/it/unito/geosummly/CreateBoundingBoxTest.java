package it.unito.geosummly;

import static org.junit.Assert.*;
import java.util.LinkedList;
import org.junit.Test;

public class CreateBoundingBoxTest {

	@Test
	public void testSetBbox(){
		
		//Central cell
		BoundingBox cell=new BoundingBox(45.057, 45.0561, 7.6600, 7.6613);
		
		//Create the bounding box (just set its coordinates)
		CreateBoundingBox createBox=new CreateBoundingBox();
		createBox.setCellsNumber(20);
		createBox.setStructure(new LinkedList<BoundingBox>());
		createBox.setCell(cell);
		createBox.setBbox(new BoundingBox());
		
		//Construct the test cases
		double north=createBox.getBbox().getNorth();
		double south=createBox.getBbox().getSouth();
		double west=createBox.getBbox().getWest();
		double east=createBox.getBbox().getEast();
		
		//Start the tests
		assertNotNull(createBox.getBbox());
		assertEquals(45.065550000000016, north, 0); //0 is the delta value
		assertEquals(45.04754999999999, south, 0);
		assertEquals(7.647650000000004, west, 0);
		assertEquals(7.673649999999997, east, 0);
	}
	
	@Test
	public void testCreateCells() {
		
		//Central cell
		BoundingBox cell=new BoundingBox(45.057, 45.0561, 7.6600, 7.6613);
		
		//Create the bounding box
		CreateBoundingBox createBox=new CreateBoundingBox();
		createBox.setCellsNumber(2);
		createBox.setStructure(new LinkedList<BoundingBox>());
		createBox.setCell(cell);
		createBox.setBbox(new BoundingBox());
		createBox.createCells();
		LinkedList<BoundingBox> lb=createBox.getStructure();
		
		//Construct the test case
		BoundingBox b1=new BoundingBox(45.05745, 45.05655, 7.65935, 7.6606499999999995);
		BoundingBox b2=new BoundingBox(45.05745, 45.05655, 7.6606499999999995, 7.661949999999999);
		BoundingBox b3=new BoundingBox(45.05655, 45.05565, 7.65935, 7.6606499999999995);
		BoundingBox b4=new BoundingBox(45.05655, 45.05565, 7.6606499999999995, 7.661949999999999);
		LinkedList<BoundingBox> lb1=new LinkedList<BoundingBox>();
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
