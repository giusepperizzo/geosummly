package it.unito.geosummly;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import fi.foyt.foursquare.api.FoursquareApiException;


public class Main {
    public static Logger logger = Logger.getLogger(Main.class.toString());
    
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		
		/***************************************************************************************/
		/******************************CREATE THE BOUNDING BOX**********************************/
		/***************************************************************************************/
		double north=45.08200587145192; //north coordinate of the bounding box
		double south=45.05218065994234;
		double west=7.661247253417969;
		double east=7.70416259765625;
		int cells_number=20; //Number N of cells
		
		BoundingBox bbox=new BoundingBox(north, south, west, east); //Initialize the bounding box
		ArrayList<BoundingBox> data=new ArrayList<BoundingBox>(); //Data structure
		
		//Create a N*N grid based on the bounding box
		Grid grid=new Grid();
		grid.setCellsNumber(cells_number);
		grid.setBbox(bbox);
		grid.setStructure(data);
		grid.createCells();
		
		/***************************************************************************************/
		/****************************COLLECT ALL THE GEOPOINTS AND******************************/
		/****************************CREATE THE TRANSFORMATION MATRIX***************************/
		/***************************************************************************************/
		//Initialize a MongoDB instance
    	MongoClient mongoClient=new MongoClient("localhost");
    	DB db=mongoClient.getDB("VenueDB");
    	DBCollection coll=db.getCollection("ResultVenues");
    	
    	//Initialize a Gson instance and declare the document which will contain the JSON results for MongoDB 
    	Gson gson=new Gson();
		BasicDBObject doc;
		
		//Initialize the transformation matrix and its parameters
		ArrayList<ArrayList<Double>> originalMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		ArrayList<String> header=new ArrayList<String>(); //Sorted list of the distinct category (related to the hash map)
		TransformationMatrix tm=new TransformationMatrix();
		tm.setOriginalMatrix(originalMatrix);
		tm.setMatrix(matrix);
		tm.setMap(map);
		tm.setHeader(header);
		ArrayList<Double> row_of_matrix; //row of the transformation matrix (one for each cell);
		
		//Support variables for transformation matrix task
		int tot_num=0; //overall number of categories
		ArrayList<String> distinct_list; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list; //list of the occurrences of the distinct categories for a single cell
		ArrayList<Double> box_area =new ArrayList<Double>(); //list of the bounding boxes area values
		
		//Download venues informations
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo;
		for(BoundingBox b: data){
		    logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());

			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			
			for(FoursquareDataObject fdo: venueInfo){
				//Serialize with Gson
				String obj=gson.toJson(fdo);
				//Initialize the document which will contain the JSON result parsed for MongoDB and insert this document into MongoDB collection
				doc= (BasicDBObject) JSON.parse(obj);
				coll.insert(doc);
			}
			
			//Create the original transformation matrix
			distinct_list=fsv.createCategoryList(venueInfo);
			occurrences_list=fsv.getCategoryOccurences(venueInfo, distinct_list);
			tm.updateMap(distinct_list);//update the hash map
			row_of_matrix=tm.fillRow(occurrences_list, distinct_list, b.getCenterLat(), b.getCenterLng()); //create a consistent row (related to the categories)
			if(tot_num < row_of_matrix.size())
				tot_num=row_of_matrix.size(); //update the overall number of categories
			tm.addRow(row_of_matrix);
			box_area.add(b.getArea());
		}
		tm.fixRowsLength(tot_num); //update rows length for consistency
		
		//Create the normalized transformation matrix
		tm.buildMatrix(tm.getOriginalMatrix(), box_area);
		
		
		// write down the transformation matrix to a file		
		ByteArrayOutputStream bout_original = new ByteArrayOutputStream();
		OutputStreamWriter osw_original = new OutputStreamWriter(bout_original);
		ByteArrayOutputStream bout_norm = new ByteArrayOutputStream();
		OutputStreamWriter osw_norm = new OutputStreamWriter(bout_norm);
        try {
            CSVPrinter csv_original = new CSVPrinter(osw_original, CSVFormat.DEFAULT);
            CSVPrinter csv_norm = new CSVPrinter(osw_norm, CSVFormat.DEFAULT);
		
            // write the header of the matrix
            ArrayList<String> hdr_original=tm.getHeader();
            ArrayList<String> hdr_norm=new ArrayList<>();
            for(int i=0; i<hdr_original.size(); i++)
            	if(i!=2)
            		hdr_norm.add(hdr_original.get(i));
            for(String s: hdr_original) {
            	csv_original.print(s);
            }
            csv_original.println();
            
            for(String s: hdr_norm) {
            	csv_norm.print(s);
            }
            csv_norm.println();
            
            // iterate per each row of the matrix
            ArrayList<ArrayList<Double>> m_original=tm.getOriginalMatrix();
            for(ArrayList<Double> a: m_original) {
            	for(Double d: a) {
            		csv_original.print(d);
            	}
            	csv_original.println();
            }
            csv_original.flush();
            
            ArrayList<ArrayList<Double>> m_norm=tm.getMatrix();
            for(ArrayList<Double> a: m_norm) {
            	for(Double d: a) {
            		csv_norm.print(d);
            	}
            	csv_norm.println();
            }
            csv_norm.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		OutputStream outputStream_original;
		OutputStream outputStream_norm;
        try {
            outputStream_original = new FileOutputStream ("output/original-transformation-matrix.csv");
            bout_original.writeTo(outputStream_original);
            outputStream_norm = new FileOutputStream ("output/normalized-transformation-matrix.csv");
            bout_norm.writeTo(outputStream_norm);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

		
		//Print JSON files (just for debug)
    	/*DBCursor cursorDocJSON = coll.find();
    	while (cursorDocJSON.hasNext()) {
    		System.out.println(cursorDocJSON.next());
    	}*/
	}
}
