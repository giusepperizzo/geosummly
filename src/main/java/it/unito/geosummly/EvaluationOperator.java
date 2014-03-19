package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.LogDataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.csv.CSVRecord;

public class EvaluationOperator {
	
	public EvaluationOperator() {}
	
	public void executeCorrectness(String in, String out, int mnum) throws IOException{
		
		//Read csv file without considering coordinate values
		CSVDataIO dataIO=new CSVDataIO();
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		List<CSVRecord> list=dataIO.readCSVFile(in);
		
		//Remove timestamp and coordinates
		for(int k=3;k<list.get(0).size();k++) {
			features.add(list.get(0).get(k));
		}
		
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			for(int j=3;j<list.get(k).size();j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		
		//Get the areas
		TransformationTools tools=new TransformationTools();
		double edgeValue=tools.getDistance(Double.parseDouble(list.get(1).get(0)), Double.parseDouble(list.get(1).get(1)), Double.parseDouble(list.get(2).get(0)), Double.parseDouble(list.get(2).get(1)));
		double areaValue=Math.pow(edgeValue, 2);
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		for(int i=0; i<matrix.size();i++)
			bboxArea.add(areaValue);
		
		//Create the random matrices and print them to file
		ArrayList<ArrayList<Double>> frequencyRandomMatrix;
		ArrayList<ArrayList<Double>> densityRandomMatrix;
		ArrayList<ArrayList<Double>> normalizedRandomMatrix;
		ArrayList<Double> randomRecord;
		double randomValue;
		ArrayList<Double> minArray=tools.getMinArray(matrix); //get min and max values of features occurrences
		ArrayList<Double> maxArray=tools.getMaxArray(matrix);
		int min;
		int max;
		
		//mnum matrices
		for(int i=0;i<mnum;i++) {
			frequencyRandomMatrix=new ArrayList<ArrayList<Double>>();
			//matrix.size() records per matrix
			for(int j=0;j<matrix.size();j++) {
				randomRecord=new ArrayList<Double>();
				//get randomly the features values
				for(int k=0;k<minArray.size();k++) {
					min=minArray.get(k).intValue();
					max=maxArray.get(k).intValue();
					randomValue=min + (int) (Math.random()*(max-min+1)); //random number from min to max included
					randomRecord.add(randomValue);
				}
				frequencyRandomMatrix.add(randomRecord);
			}
			
			densityRandomMatrix=tools.buildDensityMatrix(CoordinatesNormalizationType.MISSING, frequencyRandomMatrix, bboxArea);
			normalizedRandomMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.MISSING, densityRandomMatrix);
			ArrayList<String >feat=tools.changeFeaturesLabel("f", "", features);
			dataIO.printResultHorizontal(null, densityRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "density_rnd", feat), out+"/random-density-transformation-matrix-"+i+".csv");
			dataIO.printResultHorizontal(null, normalizedRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "normalized_density_rnd", feat), out+"/random-normalized-transformation-matrix-"+i+".csv");
		}
	}
	
	public void executeValidation(String in, String inDeltad, String out, int fnum) throws IOException {
		
		//Read csv file without considering the first three columns: timestamp, beenHere, venueId
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(in);
		EvaluationTools eTools=new EvaluationTools();
		
		//Fill in the matrix of single venues
		ArrayList<ArrayList<Double>> matrix = eTools.buildSinglesFromList(list);
		
		//Fill in the list of timestamps (useful for venue grouping)
		ArrayList<Long> timestamps=eTools.getTimestampsFromList(list);
		
		//Create fnum matrices of singles with N/fnum random venues for each matrix
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices=eTools.createFolds(matrix, fnum);
		
		//Group the venues and get the value of each cell
		TransformationTools tools=new TransformationTools();
		tools.setSinglesTimestamps(timestamps);
		ArrayList<BoundingBox> data=tools.getBoxes(matrix);
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped=eTools.groupFolds(tools, data, allMatrices);
		ArrayList<Double> bboxArea=eTools.getCellsArea(tools, data, matrix);
		
		//Fill in the map of features for transformation
		HashMap<String, Integer> map=eTools.getFeaturesMapFromList(list);
		
		//Transform all the random matrices, prepare map for evaluation and write them to file
		TransformationMatrix ithTm;
		ClusteringOperator co=new ClusteringOperator();
		LogDataWriter ldw=new LogDataWriter();
		ArrayList<HashMap<String, Vector<Integer>>> holdoutList=new ArrayList<HashMap<String, Vector<Integer>>>();
		HashMap<String, Vector<Integer>> holdout;
		int index=0; //used for file name
		int length=0;
		for(ArrayList<ArrayList<Double>> grouped: allGrouped) {
			
			//create the transformed fold
			ithTm=eTools.transformFold(grouped, tools, map, bboxArea);
			
			//create map for the holdout evaluation
			holdout=co.executeForEvaluation(ithTm.getNormalizedMatrix(), length, inDeltad); //normalized_matrix, last_cellId, deltad_matrix
			holdoutList.add(holdout);
			length+=ithTm.getNormalizedMatrix().size(); //update last_cellId value
			
			//write down the transformation matrices to file
			index++; //just for file name
			dataIO.printResultHorizontal(null, ithTm.getFrequencyMatrix(), tools.getFeaturesLabelNoTimestamp(CoordinatesNormalizationType.NORM, "f", ithTm.getHeader()), out+"/frequency-transformation-matrix-fold"+index+".csv");
			dataIO.printResultHorizontal(null, ithTm.getDensityMatrix(), tools.getFeaturesLabelNoTimestamp(CoordinatesNormalizationType.NORM, "density", ithTm.getHeader()), out+"/density-transformation-matrix-fold"+index+".csv");
			dataIO.printResultHorizontal(null, ithTm.getNormalizedMatrix(), tools.getFeaturesLabelNoTimestamp(CoordinatesNormalizationType.NORM, "normalized_density", ithTm.getHeader()), out+"/normalized-transformation-matrix-fold"+index+".csv");
		
			//write down the holdout to file
		    ldw.printHoldoutLog(holdout, out);
		}
		
		//Compute jaccard and write the result to file
		StringBuilder builder=eTools.computeJaccard(holdoutList);
		ldw.printJaccardLog(builder, out);
	}
}
