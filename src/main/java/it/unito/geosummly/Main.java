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
    	/*MongoClient mongoClient=new MongoClient("localhost");
    	DB db=mongoClient.getDB("VenueDB");
    	DBCollection coll=db.getCollection("ResultVenues");
    	
    	//Initialize a Gson instance and declare the document which will contain the JSON results for MongoDB 
    	Gson gson=new Gson();
		BasicDBObject doc;*/
		
		//Initialize the transformation matrix and its parameters
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		TransformationMatrix tm=new TransformationMatrix();
		tm.setMap(map);
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
			
			/*for(FoursquareDataObject fdo: venueInfo){
				//Serialize with Gson
				String obj=gson.toJson(fdo);
				//Initialize the document which will contain the JSON result parsed for MongoDB and insert this document into MongoDB collection
				doc= (BasicDBObject) JSON.parse(obj);
				coll.insert(doc);
			}*/
			
			//put venues values in a matrix
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
		
		//Build the transformation matrix
		ArrayList<ArrayList<Double>> frequencyMatrix=tm.sortMatrix(supportMatrix, tm.getMap());
		ArrayList<ArrayList<Double>> densityMatrix=tm.buildDensityMatrix(frequencyMatrix, bboxArea);
		ArrayList<ArrayList<Double>> normalizedMatrix=tm.buildNormalizedMatrix(densityMatrix);
		tm.setFrequencyMatrix(frequencyMatrix);
		tm.setDensityMatrix(densityMatrix);
		tm.setNormalizedMatrix(normalizedMatrix);
		tm.setHeader(tm.sortFeatures(map));
		
		// write down the transformation matrix to a file		
		ByteArrayOutputStream bout_freq = new ByteArrayOutputStream();
		OutputStreamWriter osw_freq = new OutputStreamWriter(bout_freq);
		ByteArrayOutputStream bout_dens = new ByteArrayOutputStream();
		OutputStreamWriter osw_dens = new OutputStreamWriter(bout_dens);
		ByteArrayOutputStream bout_norm = new ByteArrayOutputStream();
		OutputStreamWriter osw_norm = new OutputStreamWriter(bout_norm);
        try {
            CSVPrinter csv_freq = new CSVPrinter(osw_freq, CSVFormat.DEFAULT);
            CSVPrinter csv_dens = new CSVPrinter(osw_dens, CSVFormat.DEFAULT);
            CSVPrinter csv_norm = new CSVPrinter(osw_norm, CSVFormat.DEFAULT);
		
            // write the header of the matrix
            ArrayList<String> featureFreq=tm.getFeaturesLabel("f", tm.getHeader());
            ArrayList<String> featureDens=tm.getFeaturesLabel("density", tm.getHeader());
            ArrayList<String> featureNorm=tm.getFeaturesLabel("normalized_density", tm.getHeader());
            for(int i=0;i<featureFreq.size();i++) {
            	csv_freq.print(featureFreq.get(i));
            	csv_dens.print(featureDens.get(i));
            	csv_norm.print(featureNorm.get(i));
            }
            csv_freq.println();
            csv_dens.println();
            csv_norm.println();
            
            // iterate per each row of the matrix
            ArrayList<ArrayList<Double>> mFreq=tm.getFrequencyMatrix();
            for(ArrayList<Double> a: mFreq) {
            	for(Double d: a) {
            		csv_freq.print(d);
            	}
            	csv_freq.println();
            }
            csv_freq.flush();
            
            ArrayList<ArrayList<Double>> mDens=tm.getDensityMatrix();
            for(ArrayList<Double> a: mDens) {
            	for(Double d: a) {
            		csv_dens.print(d);
            	}
            	csv_dens.println();
            }
            csv_dens.flush();
            
            ArrayList<ArrayList<Double>> mNorm=tm.getNormalizedMatrix();
            for(ArrayList<Double> a: mNorm) {
            	for(Double d: a) {
            		csv_norm.print(d);
            	}
            	csv_norm.println();
            }
            csv_norm.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		OutputStream outputStream_freq;
		OutputStream outputStream_dens;
		OutputStream outputStream_norm;
        try {
            outputStream_freq = new FileOutputStream ("output/frequency-transformation-matrix.csv");
            bout_freq.writeTo(outputStream_freq);
            outputStream_dens = new FileOutputStream ("output/density-transformation-matrix.csv");
            bout_dens.writeTo(outputStream_dens);
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
