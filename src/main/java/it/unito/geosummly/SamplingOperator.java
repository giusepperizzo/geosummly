package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.io.templates.FoursquareObjectTemplate;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.SamplingTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import fi.foyt.foursquare.api.FoursquareApiException;


public class SamplingOperator {
    public static Logger logger = Logger.getLogger(SamplingOperator.class.toString());
    
    public SamplingOperator() {}
       
    public void executeWithInput(
    		String in, String out, CoordinatesNormalizationType ltype, long sleep) 
    			throws IOException, JSONException, FoursquareApiException, InterruptedException {
    	
    	//Get the grid
    	GeoJSONReader reader=new GeoJSONReader();
		ArrayList<BoundingBox> data=reader.decodeForSampling(in);
		double bigNorth=data.get(0).getNorth(); //top left cell give ne coordinates
		double bigEast=data.get(data.size()-1).getEast(); //bottom right cell give sw coordinates
		double bigSouth=data.get(data.size()-1).getSouth();
		double bigWest=data.get(0).getWest();
		BoundingBox global=new BoundingBox(bigNorth, bigEast, bigSouth, bigWest);
		
		collectAndTransform(global, data, out, sleep);
    }
    
    public void executeWithCoord(
    			ArrayList<Double> coord, String out, int gnum, int rnum, 
    			CoordinatesNormalizationType ltype, long sleep) 
    				throws IOException, FoursquareApiException, InterruptedException {
    	
    	//Create the grid
    	BoundingBox bbox=new BoundingBox(coord.get(0), coord.get(1), coord.get(2), coord.get(3));
    	ArrayList<BoundingBox> data=new ArrayList<BoundingBox>();
    	Grid grid=new Grid();
    	grid.setCellsNumber(gnum);
    	grid.setBbox(bbox);
    	grid.setStructure(data);
    	if(rnum>0)
    		grid.createRandomCells(rnum);
    	else
    		grid.createCells();
    	
    	collectAndTransform(bbox, data, out, sleep);
    }
    
    public void collectAndTransform(
    		BoundingBox bbox, ArrayList<BoundingBox> data, String out, long sleep) 
    			throws FoursquareApiException, InterruptedException, IOException {
    	
    	//Cache system
		/*MongoClient mongoClient=new MongoClient("localhost"); //MongoDB instance
		DB db=mongoClient.getDB("VenueDB");
		DBCollection coll=db.getCollection("ResultVenues");
		Gson gson=new Gson();
		BasicDBObject doc; //document which will contain the JSON results for MongoDB*/
    	
    	//Get the tools class and its support variables
		SamplingTools tools=new SamplingTools();
		ArrayList<ArrayList<Double>> venuesMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> venuesMatrixSecondLevel=new ArrayList<ArrayList<Double>>();
		ArrayList<FoursquareObjectTemplate> cellVenue;
		CSVDataIO dataIO=new CSVDataIO();
		
		//Get infos form foursquare
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		HashMap<String, String> tree = fsv.getCategoryTree(); //category tree
		
		
		//Collect the geopoints
		for(BoundingBox b: data){
		    logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getEast(), b.getSouth(), b.getWest());
			
			//Copy to cache
			/*for(FoursquareObjectTemplate fdo: cellVenue){
				String obj=gson.toJson(fdo); //Serialize with Gson
				doc=(BasicDBObject) JSON.parse(obj); //initialize the document with the JSON result parsed for MongoDB
				coll.insert(doc); //insert the document into MongoDB collection
			}*/
			
			venuesMatrix=tools.getInformations(b.getCenterLat(), 
					b.getCenterLng(), venuesMatrix, cellVenue, tree);
			Thread.sleep(sleep);
		}
		
		//Sort the dataset alphabetically for column names
		venuesMatrix=tools.fixRowsLength(tools.getTotal()+2, venuesMatrix); //update rows length for consistency
		venuesMatrixSecondLevel=tools.fixRowsLength(tools.getTotalSecondLevel()+2, tools.getMatrixSecondLevel());
		venuesMatrix=tools.sortMatrixSingles(venuesMatrix, tools.getMap());
		venuesMatrixSecondLevel=tools.sortMatrixSingles(venuesMatrixSecondLevel, tools.getMapSecondLevel());
		dataIO.printResultSingles(tools.getSinglesTimestamps(), tools.getBeenHere(), tools.getSinglesId(), venuesMatrix, tools.getFeaturesForSingles(tools.sortFeatures(tools.getMap())), out, "/singles-matrix.csv");
		dataIO.printResultSingles(tools.getSinglesTimestamps(), tools.getBeenHere(), tools.getSinglesId(), venuesMatrixSecondLevel, tools.getFeaturesForSingles(tools.sortFeatures(tools.getMapSecondLevel())), out, "/singles-matrix-2nd.csv");

		
		//Write down the log file
		LogDataIO logIO=new LogDataIO();
		logIO.writeSamplingLog(bbox, data, tools.getMap().keySet().size(), tools.getMapSecondLevel().keySet().size(), out);
		//dataIO.printCells(data, out, "/info-coord-celle.csv");
    
    }
}