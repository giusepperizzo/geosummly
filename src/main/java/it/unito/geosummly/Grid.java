package it.unito.geosummly;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author James
 *
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
		int num=this.cellsNumber;
		BoundingBox singleCell;
		double cellWidth=(b.getEast()-b.getWest())/num; //Width of a single cell
		double cellHeight=(b.getNorth()-b.getSouth())/num;
		double northSingleCell=b.getNorth();//north coordinate of the first cell (top-left side of bounding box)
		double southSingleCell;
		double westSingleCell=b.getWest(); //west coordinate of the first cell (top-left side of bounding box)
		double eastSingleCell;
		int row=1;
		int column=1;
		
		while((northSingleCell > b.getSouth()) && (row <= num)) {
			//set the rows
			southSingleCell=northSingleCell-cellHeight;
			column=1;
			
			while((westSingleCell < b.getEast()) && (column <= num)) {
				//set the columns
				eastSingleCell=westSingleCell+cellWidth;
				
				//set cell coordinates and position
				singleCell=new BoundingBox(northSingleCell, southSingleCell, westSingleCell, eastSingleCell);
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
	
	/**Create a grid with randomNumber random cells with coordinates belonging to the bounding box coordinates interval (useful for the discovery step)*/
	public void createRandomCells(int randomNumber) {
		BoundingBox b=this.bbox;
		BoundingBox singleCell;
		int num=this.cellsNumber;
		double cellWidth=(b.getEast()-b.getWest())/num; //Width of a single cell
		double cellHeight=(b.getNorth()-b.getSouth())/num;
		double northSingleCell;
		double southSingleCell;
		double westSingleCell;
		double eastSingleCell;
		int randomEastFactor;
		int randomSouthFactor;
		int i=0;
		while(i<randomNumber) {
			randomSouthFactor=((int) (Math.random()*(num-1)))+1;
			randomEastFactor=((int) (Math.random()*(num-1)))+1; //integer number in [1, cellnum-1] in order to not exceed the grid coordinate values
			southSingleCell=b.getNorth()-(cellHeight*randomSouthFactor);
			northSingleCell=southSingleCell+cellHeight;
			eastSingleCell=b.getWest()+(cellWidth*randomEastFactor);
			westSingleCell=eastSingleCell-cellWidth;
			singleCell=new BoundingBox(northSingleCell, southSingleCell, westSingleCell, eastSingleCell);
			structure.add(singleCell);
			i++;
		}
	}
	
	public String toString() {
		String s= "Bounding box coordinates: "+bbox+"\nCells number:"+structure.size()+"\nAll cells coordinates:";
		for(BoundingBox b : structure)
			s+="\n"+b;
		return s;
	}
	
}
