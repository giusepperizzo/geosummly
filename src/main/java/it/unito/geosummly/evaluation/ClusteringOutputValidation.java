package it.unito.geosummly.evaluation;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.CoordinatesNormalizationType;
import it.unito.geosummly.FoursquareDataObject;
import it.unito.geosummly.FoursquareSearchVenues;
import it.unito.geosummly.Grid;
import it.unito.geosummly.InformationType;
import it.unito.geosummly.TransformationMatrix;
import it.unito.geosummly.TransformationTools;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import fi.foyt.foursquare.api.FoursquareApiException;


public class ClusteringOutputValidation  {
    public static Logger logger = Logger.getLogger(ClusteringOutputValidation.class.toString());
    
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		
		/******************************CREATE THE BOUNDING BOX**********************************/
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
		logger.log(Level.INFO, "GRID CREATED");
		
		/*******************************COLLECT ALL THE GEOPOINTS********************************/
		//Get the tools class and its support variables
		TransformationTools tools=new TransformationTools();
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> cellVenue;
		InformationType infoType=InformationType.SINGLE;
		
		//Download venues informations
		for(BoundingBox b: data){
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			supportMatrix=tools.getInformationsWithFocalPts(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix, cellVenue);
			bboxArea.add(b.getArea());
		}
		supportMatrix=tools.fixRowsLength(tools.getTotal()+2, supportMatrix); //update rows length for consistency (+2 because of venue lat and lng)
		logger.log(Level.INFO, "GEOPOINTS COLLECTED");
		
		//write down the matrix to file
		printResult(supportMatrix, tools.getFeaturesForSinglesEvaluation(tools.sortFeatures(tools.getMap())), "output/evaluation/clustering output validation/singles-matrix.csv");
			
		/***********CREATE MATRICES A AND B WITH N/2 RANDOM VENUES FOR EACH MATRIX*************/
		ArrayList<ArrayList<Double>> matrixA=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> matrixB=new ArrayList<ArrayList<Double>>(supportMatrix);
		int dimension=supportMatrix.size()/2;
		int randomValue=0;
		Random random = new Random();
		for(int i=0;i<dimension;i++) {
			randomValue=random.nextInt(supportMatrix.size()); //random number between 0 (included) and matrix.size() (excluded)
			matrixA.add(supportMatrix.get(randomValue));
		}
		
		matrixB.removeAll(matrixA);
		logger.log(Level.INFO, "A AND B MATRICES CREATED");
		
		/***********DIVIDE A AND B IN 400 CELLS AND GROUP THE VENUES*************/
		ArrayList<ArrayList<Double>> groupedA=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> groupedB=new ArrayList<ArrayList<Double>>();
		
		for(BoundingBox b: data) {
			groupedA.add(tools.groupSinglesToCell(b, matrixA));
			groupedB.add(tools.groupSinglesToCell(b, matrixB));
		}
		logger.log(Level.INFO, "A AND B MATRICES GROUPED");
		
		/****************CREATE THE TRANSFORMATION MATRICES AND SERIALIZE THEM TO FILE******************/
		infoType=InformationType.CELL;
		
		//Build the transformation matrix for A
		TransformationMatrix tmA=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequencyA=tools.sortMatrix(groupedA, tools.getMap());
		tmA.setFrequencyMatrix(frequencyA);
		if(infoType.equals(InformationType.CELL)) {
			ArrayList<ArrayList<Double>> densityA=tools.buildDensityMatrix(frequencyA, bboxArea);
			tmA.setDensityMatrix(densityA);
			ArrayList<ArrayList<Double>> normalizedA=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, densityA);
			tmA.setNormalizedMatrix(normalizedA);
		}
		tmA.setHeader(tools.sortFeatures(tools.getMap()));
		
		//Build the transformation matrix for B
		TransformationMatrix tmB=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequencyB=tools.sortMatrix(groupedB, tools.getMap());
		tmB.setFrequencyMatrix(frequencyB);
		if(infoType.equals(InformationType.CELL)) {
			ArrayList<ArrayList<Double>> densityB=tools.buildDensityMatrix(frequencyB, bboxArea);
			tmB.setDensityMatrix(densityB);
			ArrayList<ArrayList<Double>> normalizedB=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, densityB);
			tmB.setNormalizedMatrix(normalizedB);
		}
		tmB.setHeader(tools.sortFeatures(tools.getMap()));
		logger.log(Level.INFO, "TRANSFORMATION MATRICES OF A AND B CREATED");
		
		//write down the transformation matrices to file
		printResult(tmA.getFrequencyMatrix(), tools.getFeaturesLabel("f", tmA.getHeader()), "output/evaluation/clustering output validation/frequency-transformation-matrix-A.csv");
		if(infoType.equals(InformationType.CELL)) {
			printResult(tmA.getDensityMatrix(), tools.getFeaturesLabel("density", tmA.getHeader()), "output/evaluation/clustering output validation/density-transformation-matrix-A.csv");
			printResult(tmA.getNormalizedMatrix(), tools.getFeaturesLabel("normalized_density", tmA.getHeader()), "output/evaluation/clustering output validation/normalized-transformation-matrix-A.csv");
		}
		printResult(tmB.getFrequencyMatrix(), tools.getFeaturesLabel("f", tmB.getHeader()), "output/evaluation/clustering output validation/frequency-transformation-matrix-B.csv");
		if(infoType.equals(InformationType.CELL)) {
			printResult(tmB.getDensityMatrix(), tools.getFeaturesLabel("density", tmB.getHeader()), "output/evaluation/clustering output validation/density-transformation-matrix-B.csv");
			printResult(tmB.getNormalizedMatrix(), tools.getFeaturesLabel("normalized_density", tmB.getHeader()), "output/evaluation/clustering output validation/normalized-transformation-matrix-B.csv");
		}
		logger.log(Level.INFO, "TRANSFORMATION MATRICES FOR A AND B PRINTED");
	}
	
	public static void printResult(ArrayList<ArrayList<Double>> matrix, ArrayList<String> features, String output) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
            
            //print the header of the matrix
            for(String f: features) {
            	csv.print(f);
            }
            csv.println();
            
            //iterate per each row of the matrix
            for(ArrayList<Double> a: matrix) {
            	for(Double d: a) {
            		csv.print(d);
            	}
            	csv.println();
            }
            csv.flush();
            csv.close();
        } catch (IOException e1) {
    		e1.printStackTrace();
        }
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream (output);
            bout.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
