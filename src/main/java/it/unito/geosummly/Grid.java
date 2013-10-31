package it.unito.geosummly;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author James
 *
 * This class create a bounding box of N*N cells.
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
	
	//Create the bounding box and define its coordinates
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
	
	//Create all the cells of the bounding box and set their coordinates
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
	
	public String toString() {
		String s= "Bounding box coordinates: "+bbox+"\nCells number:"+structure.size()+"\nAll cells coordinates:";
		for(BoundingBox b : structure)
			s+="\n"+b;
		return s;
	}
	
}
