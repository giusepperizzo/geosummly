package it.unito.geosummly;

import java.net.UnknownHostException;
import java.util.LinkedList;
import fi.foyt.foursquare.api.FoursquareApiException;


public class Main {
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		
		//SearchVenues sv=new SearchVenues();
		//sv.searchVenues("45.05,7.666667", 1, 1);
		
		double north=45.057; //north coordinate of the central cell
		double south=45.0561;
		double west=7.6600;
		double east=7.6613;
		int cell_number=20; //Number N of cells
		
		BoundingBox bbox=new BoundingBox(); //Bounding box
		BoundingBox cell=new BoundingBox(north, south, west, east); //Central cell
		LinkedList<BoundingBox> data=new LinkedList<BoundingBox>(); //Data structure
		
		//Create a N*N bounding box
		CreateBoundingBox box=new CreateBoundingBox();
		box.setCellsNumber(cell_number);
		box.setStructure(data);
		box.setCell(cell);
		box.setBbox(bbox);
		box.createCells();
		box.printAll();
	}
}
