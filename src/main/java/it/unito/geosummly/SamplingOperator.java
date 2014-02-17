package it.unito.geosummly;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import fi.foyt.foursquare.api.FoursquareApiException;


public class SamplingOperator {
    public static Logger logger = Logger.getLogger(SamplingOperator.class.toString());
    
    public SamplingOperator() {}
       
    public void executeWithInput(String in, String out, InformationType vtype, CoordinatesNormalizationType ltype) throws IOException, JSONException, FoursquareApiException, InterruptedException {
    	
    	//Create the grid
    	GeoJSONDecoder gjd=new GeoJSONDecoder();
		ArrayList<BoundingBox> data=gjd.decode(in);
		double bigNorth=data.get(data.size()-1).getNorth();
		double bigSouth=data.get(0).getSouth();
		double bigWest=data.get(0).getWest();
		double bigEast=data.get(data.size()-1).getEast();
		BoundingBox global=new BoundingBox(bigNorth, bigSouth, bigWest, bigEast);
		Grid grid=new Grid();
		grid.setCellsNumber((int) Math.sqrt(data.size()));
		grid.setBbox(global);
		grid.setStructure(data);
		
		collectAndTransform(data, out, vtype, ltype);
    }
    
    public void executeWithCoord(ArrayList<Double> coord, String out, int gnum, int rnum, InformationType vtype, CoordinatesNormalizationType ltype) throws IOException, FoursquareApiException, InterruptedException {
    	
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
    	
    	collectAndTransform(data, out, vtype, ltype);
    }
    
    public void collectAndTransform(ArrayList<BoundingBox> data, String out, InformationType vtype, CoordinatesNormalizationType ltype) throws UnknownHostException, FoursquareApiException, InterruptedException {
    	//Cache system
		/*MongoClient mongoClient=new MongoClient("localhost"); //MongoDB instance
		DB db=mongoClient.getDB("VenueDB");
		DBCollection coll=db.getCollection("ResultVenues");
		Gson gson=new Gson();
		BasicDBObject doc; //document which will contain the JSON results for MongoDB*/
    	
    	//Get the tools class and its support variables
		TransformationTools tools=new TransformationTools();
		ArrayList<ArrayList<Double>> venuesMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> cellVenue;
		DataPrinter dp=new DataPrinter();
		
		//Collect the geopoints
		for(BoundingBox b: data){
		    logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			
			//Copy to cache
			/*for(FoursquareDataObject fdo: cellVenue){
				String obj=gson.toJson(fdo); //Serialize with Gson
				doc=(BasicDBObject) JSON.parse(obj); //initialize the document with the JSON result parsed for MongoDB
				coll.insert(doc); //insert the document into MongoDB collection
			}*/
			
			venuesMatrix=tools.getInformations(vtype, b.getCenterLat(), b.getCenterLng(), venuesMatrix, cellVenue);
			bboxArea.add(b.getArea());
			Thread.sleep(1000);
		}
		
		//Group single venues to cells if necessary
		switch (vtype) {
		case SINGLE:
			//two more columns if I've to build singles matrix
			venuesMatrix=tools.fixRowsLength(tools.getTotal()+2, venuesMatrix); //update rows length for consistency
			dp.printResultHorizontal(venuesMatrix, tools.getFeaturesForSingles(tools.sortFeatures(tools.getMap())), out+"/singles-matrix.csv");
			ArrayList<ArrayList<Double>> auxMatrix=new ArrayList<ArrayList<Double>>();
			for(BoundingBox b: data)
				auxMatrix.add(tools.groupSinglesToCell(b, venuesMatrix));
			venuesMatrix=new ArrayList<ArrayList<Double>>(auxMatrix);
			break;
		case CELL:
			venuesMatrix=tools.fixRowsLength(tools.getTotal(), venuesMatrix); //update rows length for consistency
			break;
		}
		
		//Build the transformation matrix
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequencyMatrix=tools.sortMatrix(ltype, venuesMatrix, tools.getMap());
		tm.setFrequencyMatrix(frequencyMatrix);
		ArrayList<ArrayList<Double>> densityMatrix=tools.buildDensityMatrix(ltype, frequencyMatrix, bboxArea);
		tm.setDensityMatrix(densityMatrix);
		ArrayList<ArrayList<Double>> normalizedMatrix=tools.buildNormalizedMatrix(ltype, densityMatrix);
		tm.setNormalizedMatrix(normalizedMatrix);
		tm.setHeader(tools.sortFeatures(tools.getMap()));
		
		//Write down the transformation matrix to file
		dp.printResultHorizontal(tm.getFrequencyMatrix(), tools.getFeaturesLabel(ltype, "f", tm.getHeader()), out+"/frequency-transformation-matrix.csv");
		dp.printResultHorizontal(tm.getDensityMatrix(), tools.getFeaturesLabel(ltype, "density", tm.getHeader()), out+"/density-transformation-matrix.csv");
		dp.printResultHorizontal(tm.getNormalizedMatrix(), tools.getFeaturesLabel(ltype, "normalized_density", tm.getHeader()), out+"/normalized-transformation-matrix.csv");
    }
}