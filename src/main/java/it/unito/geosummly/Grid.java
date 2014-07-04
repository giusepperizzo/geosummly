package it.unito.geosummly;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Creation of a bounding box of N*N cells.
 * N is a parameter given by the user.
 */

public class Grid {
	
	private BoundingBox bbox; //Bounding box
	private int cellsNumber; //Number N of cells
	private ArrayList<BoundingBox> structure; //Data structure which contains the bounding box
	
	public static Logger logger = Logger.getLogger(Grid.class.toString());
	
	public Grid(){}
	
	public BoundingBox getBbox() {
		return bbox;
	}
	
	public void setBbox(BoundingBox bbox){
		this.bbox=bbox;
	}

	public int getCellsNumber() {
		return cellsNumber;
	}

	public void setCellsNumber(int cellsNumber) {
		this.cellsNumber = cellsNumber;
	}

	public ArrayList<BoundingBox> getStructure() {
		return structure;
	}

	public void setStructure(ArrayList<BoundingBox> structure) {
		this.structure = structure;
	}
	
	/**Create all the cells of the bounding box and set their coordinates*/
	public void createCells() {
		
		BoundingBox b=this.bbox;

		BoundingBox singleCell;
		int num = this.cellsNumber;
		
		//width of a single cell
		Double cellWidth = (b.getEast() - b.getWest()) / num;
		
		//height of a single cell
		Double cellHeight= (b.getNorth() - b.getSouth() ) / num;
		
		//coordinates of the first cell (top-left side of the bounding box)
		Double northSingleCell=b.getNorth();
		Double southSingleCell;
		Double westSingleCell=b.getWest();
		Double eastSingleCell;
		int row=1;
		int column=1;
		
		while( (northSingleCell.compareTo(b.getSouth())==1) && (row <= num) ) {
			//set the rows
			southSingleCell=northSingleCell - cellHeight;
			column=1;
			
			while( (westSingleCell.compareTo(b.getEast())==-1) && (column <= num) ) {
				//set the columns
				eastSingleCell = westSingleCell + cellWidth;
				
				//set cell coordinates and position
				singleCell=new BoundingBox(northSingleCell, 
										   eastSingleCell, 
										   southSingleCell, 
										   westSingleCell);
				singleCell.setRow(row);
				singleCell.setColumn(column);
				
				//add the cell to data structure
				structure.add(singleCell);
				
				//update the column index
				westSingleCell=eastSingleCell;
				column++;
				
			}
			
			//start again from the first column
			westSingleCell=b.getWest();
			
			//update the row index
			northSingleCell=southSingleCell;
			row++;
		}
	}
	
	/**Create a grid with randomNumber random cells with coordinates 
	 * belonging to the bounding box coordinates interval 
	 * (useful for the discovery step)
	*/
	public void createRandomCells(int randomNumber) {
		
		BoundingBox b=this.bbox;
		BoundingBox singleCell;
		int num=this.cellsNumber;
		
		//width of a single cell
		Double cellWidth = (b.getEast() - b.getWest()) / num;
		
		//height of a single cell
		Double cellHeight = (b.getNorth() - b.getSouth()) / num;
		
		Double northSingleCell;
		Double southSingleCell;
		Double westSingleCell;
		Double eastSingleCell;
		int randomEastFactor;
		int randomSouthFactor;
		int i=0;
		
		Random random = new Random();
		while(i<randomNumber) {
			
			//integer number in [1, cellnum-1] in order to 
			//not exceed the grid coordinate values
			randomSouthFactor = random.nextInt(num-1)+1;
			randomEastFactor = random.nextInt(num-1)+1;
			
			southSingleCell = b.getNorth() - (cellHeight * randomSouthFactor);
			northSingleCell = southSingleCell + cellHeight;
			eastSingleCell = b.getWest() + (cellWidth * randomEastFactor);
			westSingleCell = eastSingleCell - cellWidth;
			
			singleCell = new BoundingBox(northSingleCell, eastSingleCell, 
									     southSingleCell, westSingleCell);
			
			structure.add(singleCell);
			i++;
		}
	}
	
	public String toString() {
		String s= "Bounding box coordinates: "+ bbox +
				"\nCells number:" + structure.size() + "\nAll cells coordinates:";
		
		for(BoundingBox b : structure)
			s+="\n"+b;
		
		return s;
	}
	
}
