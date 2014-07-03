package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.EvaluationTools;
import it.unito.geosummly.tools.ImportTools;

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

	public void executeCorrectness(String inLog, String inFreq, String out, int mnum) throws IOException{

		//Read input files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inFreq);
		LogDataIO logIO=new LogDataIO();
		ArrayList<ArrayList<String>> infos=logIO.readClusteringLog(inLog);

		//Get features labels, minpts and eps
		ArrayList<String> labels=infos.get(0);
		ArrayList<String> minpts=infos.get(1);
		double eps=Double.parseDouble(infos.get(2).get(0));
		double cl_sse=Double.parseDouble(infos.get(2).get(1)); //sse of clustering on entire dataset

		//Fill in the matrix of aggregate (frequency) values without consider timestamp and coordinates
		//ArrayList<ArrayList<Double>> matrix=eTools.buildAggregatesFromList(list);
		
		//Normalize coordinates

		//Fill in the list of features
		ArrayList<String> features=eTools.getFeaturesFormList(list);
		
		
		//Matrix of normalized values with coordinates
		ArrayList<ArrayList<Double>> matrix=eTools.build(list); /****NEW CORRECTNESS VARIABLE*****/

		//Get the areas
		//ImportTools tools=new ImportTools();
		//ArrayList<BoundingBox> data=tools.getFocalPoints(matrix);
		//ArrayList<Double> bboxArea=tools.getAreasFromFocalPoints(data, matrix.size());

		//Create the random matrices and print them to file
		//ArrayList<ArrayList<Double>> frequencyRandomMatrix;
		//ArrayList<ArrayList<Double>> densityRandomMatrix;
		ArrayList<ArrayList<Double>> normalizedRandomMatrix;
		//ArrayList<Double> minArray=tools.getMinArray(matrix); //get min and max values of features occurrences
		//ArrayList<Double> maxArray=tools.getMaxArray(matrix);

		ArrayList<Double> SSEs=new ArrayList<Double>();
		ClusteringOperator co=new ClusteringOperator();

		//mnum matrices
		for(int i=0;i<mnum;i++) {
			/*frequencyRandomMatrix=eTools.buildFrequencyRandomMatrix(matrix.size(), minArray, maxArray);
			densityRandomMatrix=tools.buildDensityMatrix(CoordinatesNormalizationType.MISSING, frequencyRandomMatrix, bboxArea);
			normalizedRandomMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.MISSING, densityRandomMatrix);*/
			ArrayList<String>feat=eTools.changeFeaturesLabel("f", "", features);
			/*dataIO.printResultHorizontal(null, densityRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "density_rnd", feat), out, "/random-density-transformation-matrix-"+i+".csv");
			dataIO.printResultHorizontal(null, normalizedRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "normalized_density_rnd", feat), out, "/random-normalized-transformation-matrix-"+i+".csv");*/
			
			normalizedRandomMatrix = eTools.buildNormalizedUniformly(matrix); /****NEW CORRECTNESS VARIABLE*****/
			dataIO.printResultHorizontal(null, 
										 normalizedRandomMatrix, 
										 eTools.getFeaturesLabel("normalized_density_rnd", feat), 
										 out, 
							             "/random-normalized-transformation-matrix-"+i+".csv"); /****NEW CORRECTNESS VARIABLE*****/

			SSEs.add(co.executeForCorrectness(normalizedRandomMatrix, labels, minpts, eps));
		}
		
		//Get the sse ratio
		//double ratio = eTools.getSSERatio(SSEs, cl_sse);
		
		double pvalue = eTools.getPvalue(SSEs, cl_sse);
		
		//Write down the log file with SSE values
		//logIO.writeSSELog(SSEs, cl_sse, pvalue, out);
		logIO.writeSSELog(SSEs, cl_sse, pvalue, out);
		logIO.writeSSEforR(SSEs, out);
	}
	
	public void executeValidation2(String logFile,
								  String inSingles,
								  String out,
								  int fnum) throws IOException 
	{
		//Read input files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inSingles);
		LogDataIO logIO=new LogDataIO();
		ArrayList<ArrayList<String>> infos=logIO.readClusteringLog(logFile);
		
		//Get feature labels, minpts and eps
		ArrayList<String> labels=infos.get(0);
		ArrayList<String> minpts=infos.get(1);
		double eps=Double.parseDouble(infos.get(2).get(0));

		//Fill in the matrix of single venues 
		//without considering timestamp, been_here, venue_id
		ArrayList<ArrayList<Double>> matrix = eTools.buildSinglesFromList(list);
		
		//Fill in the list of features for transformation
		//Only the categories will be considered
		ArrayList<String> features = eTools.getFeaturesFromList(list);
		
		//Create fnum matrices of singles with N/fnum random venues for each matrix
		//and remove also the columns venue_latitude and venue_longitude
		ArrayList<ArrayList<ArrayList<Double>>> folds = eTools.createFolds(matrix, fnum);
		folds = eTools.removeVenueCoordinates(folds);
		
		//Group the venues and get the value of each cell
		ArrayList<BoundingBox> data=eTools.getFocalPoints(matrix);
		ArrayList<Double> bboxArea=eTools.getAreasFromFocalPoints(data, matrix.size());
		
		ImportTools tools = new ImportTools();
		ClusteringOperator co=new ClusteringOperator();
		int index = 0;
		int length = 0;
		
		//For each fold
		for(ArrayList<ArrayList<Double>> fold: folds) {
			
			index++; //just for file name
			
			//Do the holdout
			ArrayList<ArrayList<ArrayList<Double>>> holdout = eTools.doHoldOut(fold, 2);
	
			//Group the sets to cell
			ArrayList<ArrayList<ArrayList<Double>>> sets = eTools.groupFolds(data, holdout);
			
			//For each set this variable will contain the cells of the resulting clustering
			//ArrayList<HashMap<String, Vector<Integer>>> holdoutClustering = 
				//	new ArrayList<HashMap<String, Vector<Integer>>>();
			
			char name = 'A';
			
			//For each set
			for(ArrayList<ArrayList<Double>> set: sets) {
				
				//create the density and normalized matrix
				ArrayList<ArrayList<Double>> density = 
								tools.buildDensityMatrix(CoordinatesNormalizationType.NORM, set, bboxArea);
				ArrayList<ArrayList<Double>> normalized = 
								tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, density);
				
				//write down the matrices to file
				dataIO.printResultHorizontal(null, 
											set, 
											eTools.getFeaturesLabelNoTimestamp(
																CoordinatesNormalizationType.NORM, 
																"f", 
																features), 
											out+"/fold_"+index, 
											"/"+name+"-frequency-transformation-matrix.csv");
				dataIO.printResultHorizontal(null, 
											 density, 
											 eTools.getFeaturesLabelNoTimestamp(
													 			CoordinatesNormalizationType.NORM, 
													 			"density", 
													 			features), 
											 out+"/fold_"+index, 
											 "/"+name+"-density-transformation-matrix.csv");
				dataIO.printResultHorizontal(null, 
											 normalized, 
											 eTools.getFeaturesLabelNoTimestamp(
													 			CoordinatesNormalizationType.NORM, 
													 			"normalized_density", 
													 			features), 
								 			 out+"/fold_"+index, 
								 			 "/"+name+"-normalized-transformation-matrix.csv");
				
				//Clustering of the sets
				HashMap<String, Vector<Integer>> setClustering = 
								co.executeForValidation(normalized, length, labels, minpts, eps);
				//holdoutClustering.add(setClustering);
				
				//write down the clustering of the resulting holdout to file
				logIO.writeHoldoutLog2(setClustering, out, name, index);
				
				//switch the set name from 'A' to 'B'
				name++;
				
				length+=normalized.size(); //update last_cellId value
			}
		}
    }

	public void executeValidation(String logFile, 
								  String inSingles, 
								  String out, 
								  int fnum) throws IOException 
	{
		//Read input files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inSingles);
		LogDataIO logIO=new LogDataIO();
		ArrayList<ArrayList<String>> infos=logIO.readClusteringLog(logFile);
		
		//Get feature labels, minpts and eps
		ArrayList<String> labels=infos.get(0);
		ArrayList<String> minpts=infos.get(1);
		double eps=Double.parseDouble(infos.get(2).get(0));

		//Fill in the matrix of single venues 
		//without considering timestamp, been_here, venue_id
		ArrayList<ArrayList<Double>> matrix = eTools.buildSinglesFromList(list);

		//Create fnum matrices of singles with N/fnum random venues for each matrix
		//and remove also the columns venue_latitude and venue_longitude
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices=eTools.createFolds(matrix, fnum);
		allMatrices=eTools.removeVenueCoordinates(allMatrices);
		
		//Group the venues and get the value of each cell
		ArrayList<BoundingBox> data=eTools.getFocalPoints(matrix);
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped=eTools.groupFolds(data, allMatrices);
		ArrayList<Double> bboxArea=eTools.getAreasFromFocalPoints(data, matrix.size());

		//Fill in the list of features for transformation
		//Only the categories will be considered
		ArrayList<String> features = eTools.getFeaturesFromList(list);

		//Transform all the random matrices, 
		//repare map for evaluation 
		//and write them to file
		ImportTools tools=new ImportTools();
		ClusteringOperator co=new ClusteringOperator();
		ArrayList<HashMap<String, Vector<Integer>>> holdoutList = 
									new ArrayList<HashMap<String, Vector<Integer>>>();
		HashMap<String, Vector<Integer>> holdout;
		int index=0; //used for file name
		int length=0;
		
		for(ArrayList<ArrayList<Double>> grouped: allGrouped) {

			//create the transformed fold
			ArrayList<ArrayList<Double>> density = 
							tools.buildDensityMatrix(CoordinatesNormalizationType.NORM, grouped, bboxArea);
			ArrayList<ArrayList<Double>> normalized = 
							tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, density);

			//create map for the holdout evaluation
			//normalized_matrix, last_cellId, deltad_matrix, eps_value
			holdout = co.executeForValidation(normalized, length, labels, minpts, eps);
			holdoutList.add(holdout);
			length+=normalized.size(); //update last_cellId value

			//write down the transformation matrices to file
			index++; //just for file name
			dataIO.printResultHorizontal(null, 
										grouped, 
										eTools.getFeaturesLabelNoTimestamp(
															CoordinatesNormalizationType.NORM, 
															"f", 
															features), 
										out, 
										"/frequency-transformation-matrix-fold"+index+".csv");
			dataIO.printResultHorizontal(null, 
										 density, 
										 eTools.getFeaturesLabelNoTimestamp(
												 			CoordinatesNormalizationType.NORM, 
												 			"density", 
												 			features), 
										 out, 
										 "/density-transformation-matrix-fold"+index+".csv");
			dataIO.printResultHorizontal(null, 
										 normalized, 
										 eTools.getFeaturesLabelNoTimestamp(
												 			CoordinatesNormalizationType.NORM, 
												 			"normalized_density", 
												 			features), 
							 			 out, 
							 			 "/normalized-transformation-matrix-fold"+index+".csv");

			//write down the holdout to file
			logIO.writeHoldoutLog(holdout, out);
		}

		//Compute jaccard and write the result to file
		StringBuilder builder=eTools.computeJaccard(holdoutList);
		logIO.writeJaccardLog(builder, out);
	}
}