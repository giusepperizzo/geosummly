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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import fi.foyt.foursquare.api.FoursquareApiException;


public class ClusteringCorrectness {
    public static Logger logger = Logger.getLogger(ClusteringCorrectness.class.toString());
    
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
		InformationType infoType=InformationType.CELL;
		
		//Download venues informations
		for(BoundingBox b: data){
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			supportMatrix=tools.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix, cellVenue);
			bboxArea.add(b.getArea());
		}
		supportMatrix=tools.fixRowsLength(tools.getTotal(), supportMatrix); //update rows length for consistency
		logger.log(Level.INFO, "GEOPOINTS COLLECTED");
		
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
		logger.log(Level.INFO, "TRANSFORMATION MATRIX CREATED");
		
		//write down the transformation matrix to file
		printResult(tm.getFrequencyMatrix(), tools.getFeaturesLabel("f", tm.getHeader()), "output/evaluation/clustering correctness/frequency-transformation-matrix.csv");
		if(infoType.equals(InformationType.CELL)) {
			printResult(tm.getDensityMatrix(), tools.getFeaturesLabel("density", tm.getHeader()), "output/evaluation/clustering correctness/density-transformation-matrix.csv");
			printResult(tm.getNormalizedMatrix(), tools.getFeaturesLabel("normalized_density", tm.getHeader()), "output/evaluation/clustering correctness/normalized-transformation-matrix.csv");
		}
		logger.log(Level.INFO, "TRANSFORMATION MATRIX PRINTED");
		
		/******CREATE 500 RANDOM MATRICES RELATED TO THE TRANSFORMATION MATRIX AND SERIALIZE THEM TO FILE******/
		int recordNum=400;
		int matrixNum=500;
		ArrayList<ArrayList<Double>> frequencyRandomMatrix;
		ArrayList<ArrayList<Double>> densityRandomMatrix;
		ArrayList<ArrayList<Double>> normalizedRandomMatrix;
		ArrayList<Double> randomRecord;
		double randomValue;
		ArrayList<Double> minArray=tools.getMinArray(tm.getFrequencyMatrix()); //get min and max values of features occurrences
		ArrayList<Double> maxArray=tools.getMaxArray(tm.getFrequencyMatrix());
		int min;
		int max;
		ArrayList<String> hdr=new ArrayList<String>();
		
		//header without lat and lng
		for(int i=2;i<tm.getHeader().size();i++)
			hdr.add(tm.getHeader().get(i));
		
		//500 matrices
		for(int i=0;i<matrixNum;i++) {
			frequencyRandomMatrix=new ArrayList<ArrayList<Double>>();
			//400 records per matrix
			for(int j=0;j<recordNum;j++) {
				randomRecord=new ArrayList<Double>();
				//get randomly the features values
				for(int k=2;k<minArray.size();k++) {
					min=minArray.get(k).intValue();
					max=maxArray.get(k).intValue();
					randomValue=min + (int) (Math.random()*(max-min+1)); //random number from min to max included
					randomRecord.add(randomValue);
				}
				frequencyRandomMatrix.add(randomRecord);
			}
			logger.log(Level.INFO, "RANDOM MATRIX "+i+" CREATED");
			if(infoType.equals(InformationType.CELL)) {
				densityRandomMatrix=tools.buildDensityMatrixNoCoord(frequencyRandomMatrix, bboxArea);
				normalizedRandomMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.MISSING, densityRandomMatrix);
				printResult(normalizedRandomMatrix, tools.getFeaturesLabelNoCoord("normalized_density_rnd", hdr), "output/evaluation/clustering correctness/random-normalized-transformation-matrix-"+i+".csv");
				logger.log(Level.INFO, "RANDOM MATRIX "+i+" PRINTED");
			}
		}
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
