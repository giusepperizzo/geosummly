package it.unito.geosummly;

import java.util.ArrayList;

/**
 * @author James
 *
 * This class create a bounding box of N*N cells.
 */

public class Grid {
	private BoundingBox bbox; //Bounding box
	private BoundingBox cell; //Central cell of the bounding box. Around this cell we construct the bounding box
	private int cellsNumber; //Number N of cells
	private ArrayList<BoundingBox> structure; //Data structure which contains the bounding box 
	
	public Grid(){}
	
	public BoundingBox getBbox() {
		return bbox;
	}
	
	//Create the bounding box and define its coordinates
	public void setBbox(BoundingBox bbox){
		double cellLength=cell.getNorth()-cell.getSouth(); //length of a single cell
		double cellWidth=cell.getEast()-cell.getWest(); //width of a single cell
		double bboxLength=cellLength*cellsNumber; //total length of the bounding box
		double bboxWidth=cellWidth*cellsNumber; //total width of the bounding box
		double extraLength=(bboxLength-cellLength)/2;
		double extraWidth=(bboxWidth-cellWidth)/2;
		double northBBox=cell.getNorth()+extraLength; //North coordinate of the bounding box
		double southBBox=cell.getSouth()-extraLength; //South coordinate of the bounding box
		double westBBox=cell.getWest()-extraWidth; //West coordinate of the bounding box
		double eastBBox=cell.getEast()+extraWidth; //East coordinate of the bounding box
		
		//set the bounding box coordinates
		bbox.setNorth(northBBox);
		bbox.setSouth(southBBox);
		bbox.setWest(westBBox);
		bbox.setEast(eastBBox);
		this.bbox=bbox;
	}

	public BoundingBox getCell() {
		return cell;
	}

	public void setCell(BoundingBox cell) {
		this.cell = cell;
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
	public void createCells(){
		BoundingBox singleCell;
		double singleCellLength=cell.getNorth()-cell.getSouth(); //length of a generic cell
		double singleCellWidth=cell.getEast()-cell.getWest(); //width of a generic cell
		double northSingleCell=bbox.getNorth(); //north coordinate of the first cell (top-left side of bounding box)
		double southSingleCell;
		double westSingleCell=bbox.getWest(); //west coordinate of the first cell (top-left side of bounding box)
		double eastSingleCell;
		int row=1;
		int column=1;
		
		while(northSingleCell>bbox.getSouth()){
			//set the rows
			southSingleCell=northSingleCell-singleCellLength;
			column=1;
			
			while(westSingleCell<bbox.getEast()){
				//set the columns
				eastSingleCell=westSingleCell+singleCellWidth;
				
				//set cell coordinates
				singleCell=new BoundingBox();
				singleCell.setNorth(northSingleCell);
				singleCell.setSouth(southSingleCell);
				singleCell.setWest(westSingleCell);
				singleCell.setEast(eastSingleCell);
				
				//set cell position
				singleCell.setRow(row);
				singleCell.setColumn(column);
				
				//add the cell to data structure
				structure.add(singleCell);
				
				//update the column index
				westSingleCell=eastSingleCell;
				column++;
			}
			
			//start again from the first column
			westSingleCell=bbox.getWest();
			
			//update the row index
			northSingleCell=southSingleCell;
			row++;
		}
	}
	
	public void printAll(){
		System.out.println("Central cell coordinates "+cell);
		System.out.println("Bounding box coordinates: "+bbox);
		System.out.println("Cells number:"+structure.size());
		System.out.println("All cells coordinates:");
		for(BoundingBox b : structure)
			System.out.println(b);
	}
}
