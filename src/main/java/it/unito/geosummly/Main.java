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
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> notNormalizedMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> normalizedMatrix=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		ArrayList<String> header=new ArrayList<String>(); //Sorted list of the distinct category (related to the hash map)
		TransformationMatrix tm=new TransformationMatrix();
		tm.setNotNormalizedMatrix(notNormalizedMatrix);
		tm.setNormalizedMatrix(normalizedMatrix);
		tm.setMap(map);
		tm.setHeader(header);
		ArrayList<Double> row_of_matrix; //row of the transformation matrix (one for each cell);
		
		//Support variables for transformation matrix task
		int tot_num=0; //overall number of categories
		ArrayList<String> distinct_list; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list; //list of the occurrences of the distinct categories for a single cell
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		
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
			supportMatrix.add(row_of_matrix);
			bboxArea.add(b.getArea());
		}
		tm.fixRowsLength(tot_num, supportMatrix); //update rows length for consistency
		
		ArrayList<ArrayList<Double>> sortedMatrix=tm.sortMatrix(supportMatrix, tm.getMap());
		
		tm.buildNotNormalizedMatrix(supportMatrix); //Create a not normalized transformation matrix with frequencies
		tm.buildNormalizedMatrix(tm.getNotNormalizedMatrix(), bboxArea); //Create a normalized transformation matrix in [0,1] with densities
		
		
		// write down the transformation matrix to a file		
		ByteArrayOutputStream bout_notnorm = new ByteArrayOutputStream();
		OutputStreamWriter osw_notnorm = new OutputStreamWriter(bout_notnorm);
		ByteArrayOutputStream bout_norm = new ByteArrayOutputStream();
		OutputStreamWriter osw_norm = new OutputStreamWriter(bout_norm);
        try {
            CSVPrinter csv_notnorm = new CSVPrinter(osw_notnorm, CSVFormat.DEFAULT);
            CSVPrinter csv_norm = new CSVPrinter(osw_norm, CSVFormat.DEFAULT);
		
            // write the header of the matrix
            ArrayList<String> hdr=tm.getHeader();
            for(String s: hdr) {
            	csv_notnorm.print(s);
            	csv_norm.print(s);
            }
            csv_notnorm.println();
            csv_norm.println();
            
            // iterate per each row of the matrix
            ArrayList<ArrayList<Double>> m_original=tm.getNotNormalizedMatrix();
            for(ArrayList<Double> a: m_original) {
            	for(Double d: a) {
            		csv_notnorm.print(d);
            	}
            	csv_notnorm.println();
            }
            csv_notnorm.flush();
            
            ArrayList<ArrayList<Double>> m_norm=tm.getNormalizedMatrix();
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
		
		OutputStream outputStream_notnorm;
		OutputStream outputStream_norm;
        try {
            outputStream_notnorm = new FileOutputStream ("output/not-normalized-transformation-matrix.csv");
            bout_notnorm.writeTo(outputStream_notnorm);
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
