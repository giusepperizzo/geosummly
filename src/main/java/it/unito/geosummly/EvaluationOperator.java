package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.LogWriter;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.EvaluationTools;
import it.unito.geosummly.tools.TransformationTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.csv.CSVRecord;

public class EvaluationOperator {
	
	private EvaluationTools eTools;
	
	public EvaluationOperator() {
		eTools=new EvaluationTools();
	}
	
	public void executeCorrectness(String inFreq, String inDeltad, String out, int mnum) throws IOException{
		
		//Read csv file without considering coordinate values
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inFreq);
		
		//Fill in the matrix of aggregate (frequency) values
		ArrayList<ArrayList<Double>> matrix=eTools.buildAggregatesFromList(list);
		
		//Fill in the list of features
		ArrayList<String> features=eTools.getFeaturesFormList(list);
		
		//Get the areas
		TransformationTools tools=new TransformationTools();
		ArrayList<BoundingBox> data=tools.getBoxesFromSingles(matrix);
		ArrayList<Double> bboxArea=eTools.getCellsArea(tools, data, matrix);
		
		//Create the random matrices and print them to file
		ArrayList<ArrayList<Double>> frequencyRandomMatrix;
		ArrayList<ArrayList<Double>> densityRandomMatrix;
		ArrayList<ArrayList<Double>> normalizedRandomMatrix;
		ArrayList<Double> minArray=tools.getMinArray(matrix); //get min and max values of features occurrences
		ArrayList<Double> maxArray=tools.getMaxArray(matrix);
		
		ArrayList<Double> SSEs=new ArrayList<Double>();
		ClusteringOperator co=new ClusteringOperator();
		
		//mnum matrices
		for(int i=0;i<mnum;i++) {
			frequencyRandomMatrix=eTools.buildFrequencyRandomMatrix(matrix, minArray, maxArray);
			densityRandomMatrix=tools.buildDensityMatrix(CoordinatesNormalizationType.MISSING, frequencyRandomMatrix, bboxArea);
			normalizedRandomMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.MISSING, densityRandomMatrix);
			ArrayList<String >feat=tools.changeFeaturesLabel("f", "", features);
			dataIO.printResultHorizontal(null, densityRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "density_rnd", feat), out+"/random-density-transformation-matrix-"+i+".csv");
			dataIO.printResultHorizontal(null, normalizedRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "normalized_density_rnd", feat), out+"/random-normalized-transformation-matrix-"+i+".csv");
			
			SSEs.add(co.executeForCorrectness(normalizedRandomMatrix, inDeltad, 0.1));
		}
		
		//Write down the log file with SSE values
		LogWriter lWriter=new LogWriter();
		lWriter.printSSELog(SSEs, out);
		lWriter.printSSEforR(SSEs, out);
	}
	
	public void executeValidation(String inSingles, String inDeltad, String out, int fnum) throws IOException {
		
		//Read csv file without considering the first three columns: timestamp, beenHere, venueId
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inSingles);
		
		//Fill in the matrix of single venues
		ArrayList<ArrayList<Double>> matrix = eTools.buildSinglesFromList(list);
		
		//Fill in the list of timestamps (useful for venue grouping)
		ArrayList<Long> timestamps=eTools.getTimestampsFromList(list);
		
		//Create fnum matrices of singles with N/fnum random venues for each matrix
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices=eTools.createFolds(matrix, fnum);
		
		//Group the venues and get the value of each cell
		TransformationTools tools=new TransformationTools();
		tools.setSinglesTimestamps(timestamps);
		ArrayList<BoundingBox> data=tools.getBoxesFromSingles(matrix);
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped=eTools.groupFolds(tools, data, allMatrices);
		ArrayList<Double> bboxArea=eTools.getCellsArea(tools, data, matrix);
		
		//Fill in the map of features for transformation
		HashMap<String, Integer> map=eTools.getFeaturesMapFromList(list);
		
		//Transform all the random matrices, prepare map for evaluation and write them to file
		TransformationMatrix ithTm;
		ClusteringOperator co=new ClusteringOperator();
		LogWriter ldw=new LogWriter();
		ArrayList<HashMap<String, Vector<Integer>>> holdoutList=new ArrayList<HashMap<String, Vector<Integer>>>();
		HashMap<String, Vector<Integer>> holdout;
		int index=0; //used for file name
		int length=0;
		for(ArrayList<ArrayList<Double>> grouped: allGrouped) {
			
			//create the transformed fold
			ithTm=eTools.transformFold(grouped, tools, map, bboxArea);
			
			//create map for the holdout evaluation
			holdout=co.executeForValidation(ithTm.getNormalizedMatrix(), length, inDeltad, 0.1); //normalized_matrix, last_cellId, deltad_matrix, eps_value
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
