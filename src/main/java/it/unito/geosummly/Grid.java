package it.unito.geosummly;

import java.math.BigDecimal;
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
		BigDecimal num = new BigDecimal(this.cellsNumber);
		BoundingBox singleCell;
		
		//width of a single cell
		BigDecimal cellWidth = (b.getEast().subtract(b.getWest())).divide(num);
		
		//height of a single cell
		BigDecimal cellHeight=(b.getNorth().subtract(b.getSouth())).divide(num);
		
		//coordinates of the first cell (top-left side of the bounding box)
		BigDecimal northSingleCell=b.getNorth();
		BigDecimal southSingleCell;
		BigDecimal westSingleCell=b.getWest();
		BigDecimal eastSingleCell;
		int row=1;
		int column=1;
		
		while( (northSingleCell.compareTo(b.getSouth())==1) && (row <= num.intValue()) ) {
			//set the rows
			southSingleCell=northSingleCell.subtract(cellHeight);
			column=1;
			
			while( (westSingleCell.compareTo(b.getEast())==-1) && (column <= num.intValue()) ) {
				//set the columns
				eastSingleCell = westSingleCell.add(cellWidth);
				
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
		BigDecimal cellWidth = (b.getEast().subtract(b.getWest())).divide(new BigDecimal(num));
		
		//height of a single cell
		BigDecimal cellHeight = (b.getNorth().subtract(b.getSouth())).divide(new BigDecimal(num));
		
		BigDecimal northSingleCell;
		BigDecimal southSingleCell;
		BigDecimal westSingleCell;
		BigDecimal eastSingleCell;
		int randomEastFactor;
		int randomSouthFactor;
		int i=0;
		
		Random random = new Random();
		while(i<randomNumber) {
			
			//integer number in [1, cellnum-1] in order to 
			//not exceed the grid coordinate values
			randomSouthFactor = random.nextInt(num-1)+1;
			randomEastFactor = random.nextInt(num-1)+1;
			
			southSingleCell = b.getNorth().subtract(
									cellHeight.multiply(new BigDecimal(randomSouthFactor))
							  );
			northSingleCell = southSingleCell.add(cellHeight);
			eastSingleCell = b.getWest().add(
									cellWidth.multiply(
									new BigDecimal(randomEastFactor))
							 );
			westSingleCell = eastSingleCell.subtract(cellWidth);
			
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
