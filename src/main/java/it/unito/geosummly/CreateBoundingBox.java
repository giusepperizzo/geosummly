package it.unito.geosummly;

import java.util.LinkedList;

/**
 * @author James
 *
 * This class create a bounding box of N*N cells.
 */

public class CreateBoundingBox {
	private BoundingBox bbox; //Bounding box
	private BoundingBox cell; //Central cell of the bounding box. Around this cell we construct the bounding box
	private int cellsNumber; //Number N of cells
	private LinkedList<BoundingBox> structure; //Data structure which contains the bounding box 
	
	public CreateBoundingBox(){}
	
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

	public LinkedList<BoundingBox> getStructure() {
		return structure;
	}

	public void setStructure(LinkedList<BoundingBox> structure) {
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
		
		while(northSingleCell>bbox.getSouth()){
			//set the row
			southSingleCell=northSingleCell-singleCellLength;
			
			while(westSingleCell<bbox.getEast()){
				//set the column
				eastSingleCell=westSingleCell+singleCellWidth;
				
				//set cell coordinates
				singleCell=new BoundingBox();
				singleCell.setNorth(northSingleCell);
				singleCell.setSouth(southSingleCell);
				singleCell.setWest(westSingleCell);
				singleCell.setEast(eastSingleCell);
				
				//add the cell to data structure
				structure.add(singleCell);
				
				//update the column index
				westSingleCell=eastSingleCell;
			}
			
			//start again from the first column
			westSingleCell=bbox.getWest();
			
			//update the row index
			northSingleCell=southSingleCell;
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
