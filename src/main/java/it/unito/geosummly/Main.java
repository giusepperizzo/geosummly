package it.unito.geosummly;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import fi.foyt.foursquare.api.FoursquareApiException;


public class Main {
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		
		/***************************************************************************************/
		/******************************CREATE THE BOUNDING BOX**********************************/
		/***************************************************************************************/
		double north=45.057; //north coordinate of the central cell
		double south=45.0561;
		double west=7.6600;
		double east=7.6613;
		int cells_number=20; //Number N of cells
		
		BoundingBox bbox=new BoundingBox(); //Bounding box
		BoundingBox cell=new BoundingBox(north, south, west, east); //Central cell
		ArrayList<BoundingBox> data=new ArrayList<BoundingBox>(); //Data structure
		
		//Create a N*N bounding box
		Grid box=new Grid();
		box.setCellsNumber(cells_number);
		box.setStructure(data);
		box.setCell(cell);
		box.setBbox(bbox);
		box.createCells();
		box.printAll();
		
		/***************************************************************************************/
		/*****************************COLLECT ALL THE GEOPOINTS*********************************/
		/***************************************************************************************/
		//Initialize a MongoDB instance
    	MongoClient mongoClient=new MongoClient("localhost");
    	DB db=mongoClient.getDB("VenueDB");
    	DBCollection coll=db.getCollection("ResultVenues");
    	
    	//Declare the document which will contain the JSON results for MongoDB 
		BasicDBObject doc;
		
		//Download venues informations
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<String> venueInfo;
		for(BoundingBox b: data){
			
			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			
			for(String s: venueInfo){
				
				//Initialize the document which will contain the JSON result parsed for MongoDB and insert this document into MongoDB collection
				doc= (BasicDBObject) JSON.parse(s);
				coll.insert(doc);
			}
		}
		
		//Print JSON files (just for debug)
    	/*DBCursor cursorDocJSON = coll.find();
    	while (cursorDocJSON.hasNext()) {
    		System.out.println(cursorDocJSON.next());
    	}*/
	}
}
