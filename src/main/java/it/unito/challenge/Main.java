package it.unito.challenge;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.CoordinatesNormalizationType;
import it.unito.geosummly.DataPrinter;
import it.unito.geosummly.FoursquareDataObject;
import it.unito.geosummly.FoursquareSearchVenues;
import it.unito.geosummly.Grid;
import it.unito.geosummly.InformationType;
import it.unito.geosummly.TransformationMatrix;
import it.unito.geosummly.TransformationTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONException;


public class Main {
	public static Logger logger = Logger.getLogger(Main.class.toString());
	
	public static void main(String[] args) throws IOException, FoursquareApiException, JSONException {
		
		
		/******************************DECODE THE GEOJSON DATASET**********************************/
		GeoJSONDecoder gjd=new GeoJSONDecoder();
		ArrayList<BoundingBox> data=gjd.decode("geojson_input_dataset/milano-grid.geojson");
		
		/******************************CREATE THE BOUNDING BOX**********************************/
		double bigNorth=data.get(data.size()-1).getNorth();
		double bigSouth=data.get(0).getSouth();
		double bigWest=data.get(0).getWest();
		double bigEast=data.get(data.size()-1).getEast();
		BoundingBox global=new BoundingBox(bigNorth, bigSouth, bigWest, bigEast);
		Grid grid=new Grid();
		grid.setCellsNumber((int) Math.sqrt(data.size()));
		grid.setBbox(global);
		grid.setStructure(data);
		
		/*******************************COLLECT ALL THE GEOPOINTS********************************/
		//Get the tools class and its support variables
		TransformationTools tools=new TransformationTools();
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> cellVenue;
		InformationType infoType=InformationType.CELL;
		
		//Download venues informations
		for(BoundingBox b: data){
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			//Generate a matrix with geopoints informations
			supportMatrix=tools.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix, cellVenue);
			bboxArea.add(b.getArea());
		}
		supportMatrix=tools.fixRowsLength(tools.getTotal(), supportMatrix); //update rows length for consistency
		
		/****************CREATE THE TRANSFORMATION MATRIX AND SERIALIZE IT TO FILE******************/
		//Build the transformation matrix
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequencyMatrix=tools.sortMatrix(supportMatrix, tools.getMap());
		tm.setFrequencyMatrix(frequencyMatrix);
		if(infoType.equals(InformationType.CELL)) {
			ArrayList<ArrayList<Double>> densityMatrix=tools.buildDensityMatrix(frequencyMatrix, bboxArea);
			tm.setDensityMatrix(densityMatrix);
			ArrayList<ArrayList<Double>> normalizedMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, densityMatrix);
			tm.setNormalizedMatrix(normalizedMatrix);
		}
		tm.setHeader(tools.sortFeatures(tools.getMap()));
		
		//write down the transformation matrix to file
		DataPrinter dp=new DataPrinter();
		dp.printResult(tm.getFrequencyMatrix(), tools.getFeaturesLabel("f", tm.getHeader()), "datasets/milan/frequency-transformation-matrix.csv");
		if(infoType.equals(InformationType.CELL)) {
			dp.printResult(tm.getDensityMatrix(), tools.getFeaturesLabel("density", tm.getHeader()), "datasets/milan/density-transformation-matrix.csv");
			dp.printResult(tm.getNormalizedMatrix(), tools.getFeaturesLabel("normalized_density", tm.getHeader()), "datasets/milan/normalized-transformation-matrix.csv");
		}
	}
}
