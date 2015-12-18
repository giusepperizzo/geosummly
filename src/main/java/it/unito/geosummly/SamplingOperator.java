package it.unito.geosummly;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.CixtyJSONReader;
import it.unito.geosummly.io.GeoJSONReader;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.io.templates.FoursquareObjectTemplate;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.SamplingTools;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;


public class SamplingOperator {

    public static Logger logger = Logger.getLogger(SamplingOperator.class.toString());

    public SamplingOperator() {}
       
    public void executeWithInput( String in, 
    							  String out, 
    							  CoordinatesNormalizationType ltype, 
    							  long sleep,
    							  boolean secondLevel) 
    									  	throws IOException, 
    									  	JSONException, 
    									  	FoursquareApiException, 
    									  	InterruptedException {
    	
    	//Get the grid
		ArrayList<BoundingBox> data = null;
		ArrayList<Venue> venues = null;

		if (in.endsWith("geojson")) {  //Process GEOJSON Data
			GeoJSONReader reader = new GeoJSONReader();
			data = reader.decodeForSampling(in);
			//top left cell gives ne coordinates
			Double bigNorth=data.get(0).getNorth();
			Double bigEast=data.get(data.size()-1).getEast();

			//bottom right cell gives sw coordinates
			Double bigSouth=data.get(data.size()-1).getSouth();
			Double bigWest=data.get(0).getWest();

			BoundingBox global=new BoundingBox(bigNorth, bigEast, bigSouth, bigWest);
			collectAndTransform(global, data, out, sleep, secondLevel);
		}
		else if(in.endsWith("cixtyjson")) { //Process 3cixtyJSON Data
			CixtyJSONReader reader = new CixtyJSONReader();
			venues = reader.decodeForSampling(in);

			Double bigNorth=45.567794914783256;
			Double bigEast=9.312688264185276;
			Double bigSouth=45.35668565341486;
			Double bigWest=9.011490619692509;

			BoundingBox global=new BoundingBox(bigNorth, bigEast, bigSouth, bigWest);

			//grid the BoundingBox for 3cixty JSON file
	 		ArrayList<BoundingBox> cells = new ArrayList<BoundingBox>();
			Grid grid = new Grid();
			grid.setBbox(global);
			grid.setCellsNumber(40);
			grid.setStructure(cells);
			grid.createCells();

			collectAndTransform_3Cixty(global, cells, venues, out, sleep, secondLevel);

		}
		else{
			System.out.println("The file sampled isn't supported");
			System.exit(-1);
		}

    }
    
    public void executeWithCoord( ArrayList<Double> coord, 
    							  String out, 
    							  int gnum, 
    							  int rnum, 
    							  CoordinatesNormalizationType ltype, 
    							  long sleep,
    							  boolean secondLevel) 
    									  throws IOException, 
    									  FoursquareApiException, 
    									  InterruptedException {
    	
    	//Create the grid
    	BoundingBox bbox = new BoundingBox(coord.get(0), 
    					   				   coord.get(1), 
    					   				   coord.get(2), 
    					   				   coord.get(3));
    	
    	ArrayList<BoundingBox> data=new ArrayList<BoundingBox>();
    	Grid grid=new Grid();
    	grid.setCellsNumber(gnum);
    	grid.setBbox(bbox);
    	grid.setStructure(data);
    	if(rnum>0)
    		grid.createRandomCells(rnum);
    	else
    		grid.createCells();
    	
    	collectAndTransform(bbox, data, out, sleep, secondLevel);
    }
    
    public void collectAndTransform(
    								BoundingBox bbox, 
    								ArrayList<BoundingBox> data,
    								String out, 
    								long sleep,
    								boolean secondLevel) 
    									throws FoursquareApiException, 
    									InterruptedException, 
    									IOException {
    	
    	
    	//Cache system
		/*MongoClient mongoClient=new MongoClient("localhost"); //MongoDB instance
		DB db=mongoClient.getDB("VenueDB");
		DBCollection coll=db.getCollection("ResultVenues");
		Gson gson=new Gson();
		BasicDBObject doc; //document which will contain the JSON results for MongoDB*/
    	
    	//Get the tools class and its support variables
		SamplingTools tools=new SamplingTools();
		/*ArrayList<ArrayList<Double>> venuesMatrix = 
									new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> venuesMatrixSecondLevel = 
									new ArrayList<ArrayList<Double>>();*/
		ArrayList<ArrayList<Byte>> venuesMatrix = 
									new ArrayList<ArrayList<Byte>>();
		ArrayList<ArrayList<Byte>> venuesMatrixSecondLevel = 
									new ArrayList<ArrayList<Byte>>();
		//ArrayList<FoursquareObjectTemplate> cellVenue;
		CSVDataIO dataIO=new CSVDataIO();

			//Get infos form foursquare
		ArrayList<FoursquareObjectTemplate> cellVenue;
		FoursquareSearchVenues fsv = new FoursquareSearchVenues();
		HashMap<String, String> tree = fsv.getCategoryTree(); //category tree

			//Collect the geopoints
		for (BoundingBox b : data) {

			logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());
			cellVenue = fsv.searchVenues(b.getRow(), b.getColumn(),
					b.getNorth(), b.getEast(),
					b.getSouth(), b.getWest());

			//Copy to cache
			/*for(FoursquareObjectTemplate fdo: cellVenue){
				String obj=gson.toJson(fdo); //Serialize with Gson
				doc=(BasicDBObject) JSON.parse(obj); //initialize the document with the JSON result parsed for MongoDB
				coll.insert(doc); //insert the document into MongoDB collection
			}*/

			venuesMatrix = tools.getInformations2(b.getCenterLat(),
					b.getCenterLng(),
					venuesMatrix,
					cellVenue,
					tree);

			Thread.sleep(sleep);
		}

		//Sort the dataset alphabetically for column names
		venuesMatrix = tools.fixRowsLength2(tools.getTotal(), 
										   venuesMatrix); //update rows length for consistency
		venuesMatrixSecondLevel = tools.fixRowsLength2(tools.getTotalSecond(), 
													  tools.getMatrixSecond());
		venuesMatrix = tools.sortMatrixSingles2(venuesMatrix, 
											   tools.getMap());
		venuesMatrixSecondLevel = tools.sortMatrixSingles2(venuesMatrixSecondLevel, 
														  tools.getMapSecond());
		
		//Write down the log file
		LogDataIO logIO=new LogDataIO();
		logIO.writeSamplingLog( bbox, 
								data, 
								tools.getMap().keySet().size(), 
								tools.getMapSecond().keySet().size(), 
								out, secondLevel);
		
		//Serialize the matrices to file
		dataIO.printResultSingles2(tools.getTimestamp(), 
								  tools.getBeenHere(), 
								  tools.getIds(),
								  tools.getCooridnates(),
								  venuesMatrix, 
								  tools.getFeaturesForSingles2(
										  			tools.sortFeatures2(tools.getMap())), 
								  out, 
								  "/singles-matrix.csv");
		
		if(secondLevel) { //print only if the CLi option is true
			dataIO.printResultSingles2(tools.getTimestampSecond(), 
									  tools.getBeenHereSecond(), 
									  tools.getIdsSecond(),
									  tools.getCooridnatesSecond(),
									  venuesMatrixSecondLevel, 
									  tools.getFeaturesForSingles2(
											  			tools.sortFeatures2(tools.getMapSecond())), 
									  out, 
									  "/singles-matrix-2nd.csv");
		}
		
		//dataIO.printCells(data, out, "/info-coord-celle.csv");
    
    }

	/*
	Transform the 3cixty data to single-matrix.csv
	*/
	public void collectAndTransform_3Cixty(
			BoundingBox bbox,
			ArrayList<BoundingBox> cells,
			ArrayList<Venue> Venuelist,
			String out,
			long sleep,
			boolean secondLevel)
			throws FoursquareApiException,
			InterruptedException,
			IOException {

		SamplingTools tools=new SamplingTools();
		/*ArrayList<ArrayList<Double>> venuesMatrix =
									new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> venuesMatrixSecondLevel =
									new ArrayList<ArrayList<Double>>();*/
		ArrayList<ArrayList<Byte>> venuesMatrix =
				new ArrayList<ArrayList<Byte>>();
		ArrayList<ArrayList<Byte>> venuesMatrixSecondLevel =
				new ArrayList<ArrayList<Byte>>();
		//ArrayList<FoursquareObjectTemplate> cellVenue;
		CSVDataIO dataIO=new CSVDataIO();

		HashMap<String, String> tree = new HashMap<String, String>(); //category tree
		ThreeCixtySearchVenues tcs = new ThreeCixtySearchVenues();
		ArrayList<Venue> cellVenue = new ArrayList<Venue>();

		//build Category tree from Venuelist, Venuelist made from local json file
		for (Venue venue : Venuelist) {
			tree.put(venue.getLabel(), venue.getCategory());
		}

		//Collect the geopoints
		for (BoundingBox box : cells) {

			logger.log(Level.INFO, "Using 3cixty metadata of the cell: " + box.toString());

			cellVenue = tcs.SearchVenues(box.getNorth(), box.getWest(), box.getSouth(), box.getEast(), Venuelist);

			venuesMatrix = tools.getInformations3(box.getCenterLat(),
					box.getCenterLng(),
					venuesMatrix,
					cellVenue,
					tree);

			Thread.sleep(sleep);
		}
		//Sort the dataset alphabetically for column names
		venuesMatrix = tools.fixRowsLength2(tools.getTotal(),
				venuesMatrix); //update rows length for consistency
		venuesMatrixSecondLevel = tools.fixRowsLength2(tools.getTotalSecond(),
				tools.getMatrixSecond());
		venuesMatrix = tools.sortMatrixSingles2(venuesMatrix,
				tools.getMap());
		venuesMatrixSecondLevel = tools.sortMatrixSingles2(venuesMatrixSecondLevel,
				tools.getMapSecond());

		//Write down the log file
		LogDataIO logIO=new LogDataIO();
		logIO.writeSamplingLog(bbox,
				cells,
				tools.getMap().keySet().size(),
				tools.getMapSecond().keySet().size(),
				out, secondLevel);

		//Serialize the matrices to file
		dataIO.printResultSingles2(tools.getTimestamp(),
				tools.getBeenHere(),
				tools.getIds(),
				tools.getCooridnates(),
				venuesMatrix,
				tools.getFeaturesForSingles2(
						tools.sortFeatures2(tools.getMap())),
				out,
				"/singles-matrix.csv");

	/*	if(secondLevel) { //print only if the CLi option is true
			dataIO.printResultSingles2(tools.getTimestampSecond(),
					tools.getBeenHereSecond(),
					tools.getIdsSecond(),
					tools.getCooridnatesSecond(),
					venuesMatrixSecondLevel,
					tools.getFeaturesForSingles2(
							tools.sortFeatures2(tools.getMapSecond())),
					out,
					"/singles-matrix-2nd.csv");
		}*/
		//dataIO.printCells(data, out, "/info-coord-celle.csv");
	}
}