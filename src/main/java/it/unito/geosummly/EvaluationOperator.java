package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.io.LogDataIO;
import it.unito.geosummly.tools.CoordinatesNormalizationType;
import it.unito.geosummly.tools.EvaluationTools;
import it.unito.geosummly.tools.ImportTools;
import it.unito.geosummly.utils.Pair;

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

	public void executeCorrectness(	String inLog, 
									String inDens,
									String inNorm, 
									String out, 
									int mnum) throws IOException{

		//Read input files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inNorm);
		LogDataIO logIO=new LogDataIO();
		ArrayList<ArrayList<String>> infos = 
						logIO.readClusteringLog(inLog);

		//Get features labels, minpts and eps
		ArrayList<String> labels=infos.get(0);
		ArrayList<String> minpts=infos.get(1);
		double eps=Double.parseDouble(infos.get(2).get(0));
		//sse of clustering on entire dataset
		double cl_sse=Double.parseDouble(infos.get(2).get(1));

		//Fill in the matrix of aggregate (frequency) values 
		//without consider timestamp and coordinates
		/*ArrayList<ArrayList<Double>> matrix = 
							eTools.buildAggregatesFromList(list);*/
		
		//Normalize coordinates

		//Fill in the list of features
		ArrayList<String> features=eTools.getFeaturesFromListC(list);
		
		
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
		//get min and max values of features occurrences
		//ArrayList<Double> minArray=tools.getMinArray(matrix);
		//ArrayList<Double> maxArray=tools.getMaxArray(matrix);

		ArrayList<Double> SSEs=new ArrayList<Double>();
		ClusteringOperator co=new ClusteringOperator();

		//mnum matrices
		for(int i=0;i<mnum;i++) {
			
			/*frequencyRandomMatrix = 
			 				eTools.buildFrequencyRandomMatrix(matrix.size(), 
			  												  minArray, 
			  												  maxArray);
			densityRandomMatrix = 
							tools.buildDensityMatrix(CoordinatesNormalizationType.MISSING, 
													 frequencyRandomMatrix, 
													 bboxArea);
			normalizedRandomMatrix = 
							tools.buildNormalizedMatrix(CoordinatesNormalizationType.MISSING, 
														densityRandomMatrix);*/
			ArrayList<String>feat=eTools.changeFeaturesLabel("f", "", features);
			
			/*dataIO.printResultHorizontal(null, densityRandomMatrix, 
			  							   tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, 
			  													  "density_rnd", 
			  													   feat), 
			  							   out, 
			  							   "/random-density-transformation-matrix-"+i+".csv");
			dataIO.printResultHorizontal(null, normalizedRandomMatrix, 
										 tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, 
										 						"normalized_density_rnd", 
										 						feat), 
				 						 out, 
				 						 "/random-normalized-transformation-matrix-"+i+".csv");*/
			
			normalizedRandomMatrix = eTools.buildNormalizedUniformly(matrix); /****NEW CORRECTNESS VARIABLE*****/
			dataIO.printResultHorizontal(null, 
										 normalizedRandomMatrix, 
										 eTools.getFeaturesLabel("normalized_density_rnd", feat), 
										 out, 
							             "/random-normalized-transformation-matrix-"+i+".csv"); /****NEW CORRECTNESS VARIABLE*****/

			SSEs.add(co.executeForCorrectness(inDens, normalizedRandomMatrix, labels, minpts, eps));
		}
		
		//Get the sse ratio
		//double ratio = eTools.getSSERatio(SSEs, cl_sse);
		
		double pvalue = eTools.getPvalue(SSEs, cl_sse);
		
		//Write down the log file with SSE values
		//logIO.writeSSELog(SSEs, cl_sse, pvalue, out);
		logIO.writeSSELog(SSEs, cl_sse, pvalue, out);
		logIO.writeSSEforR(SSEs, out);
	}
	
	public void executeValidation(  String logFile,
									String inDens,
									String out,
									int fnum) 
	throws IOException 
	{	
		//Read input files
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(inDens);
		LogDataIO logIO=new LogDataIO();
		ArrayList<ArrayList<String>> infos=logIO.readClusteringLog(logFile);
		
		//Get feature labels, minpts and eps
		ArrayList<String> labels=infos.get(0);
		ArrayList<String> minpts=infos.get(1);
		double eps=Double.parseDouble(infos.get(2).get(0));
		
		//minpts/2
		/*ArrayList<String> minpts = new ArrayList<String>();
		for(String s: minptsS) {
			
			Double d = Double.parseDouble(s)/2;
			minpts.add(d.toString());
		}*/
		
		// Build feature list (timestamp and coordinates are not considered)
		ArrayList<String> features = new ArrayList<String>();
		for(String s: list.get(0))
			features.add(s);
		features.remove(0); //remove timestamp
		features.remove(0); //remove lat
		features.remove(0); //remove lng
		
		//Density matrix (convert values from string to double)
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		//i=1 --> no header
		for(int i=1; i<list.size(); i++) {
			ArrayList<Double> record = new ArrayList<Double>();
			//j=1 --> no timestamp
			for(int j=1; j<list.get(i).size(); j++) {
				record.add(Double.parseDouble(list.get(i).get(j)));
			}
			matrix.add(record);
		}
		
		//Get labels and minpts
		DiscoveryOperator dO = new DiscoveryOperator();
		ArrayList<ArrayList<String>> deltad = dO.executeForValidation(inDens, 3);
		labels = new ArrayList<String>(deltad.get(0));
		minpts = new ArrayList<String>(deltad.get(1));
		
		//This variable contains all the pairs of sets of the folds
		ArrayList<Pair<?,?>> pairs = new ArrayList<>();
		
		ImportTools tools = new ImportTools();
		ClusteringOperator co=new ClusteringOperator();
		int index = 1;
		int length = 0;
		
		//Create the folds
		for(int i=0; i<fnum; i++) {
			//Do the holdout
			ArrayList<ArrayList<ArrayList<Double>>> sets = eTools.doHoldoutDensity(matrix);
			
			//This variable will contain the cells of the resulting clustering
			//for the pair of sets
			Pair<HashMap<String, Vector<Integer>>, 
				 HashMap<String, Vector<Integer>>> pair = new Pair<>(null, null);
			
			char name = 'A';
			
			//For each set
			for(ArrayList<ArrayList<Double>> set: sets) {
				
				ArrayList<ArrayList<Double>> normalized = 
								tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, set);
				dataIO.printResultHorizontal(null, 
											 set, 
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
								co.executeForValidation(inDens, normalized, length, labels, minpts, eps);
				
				if(name == 'A')
					pair.setFirst(setClustering);
				else
					pair.setSecond(setClustering);
				
				//write down the clustering of the resulting holdout to file
				logIO.writeHoldoutLog2(setClustering, out, name, index);
				
				//switch the set name from 'A' to 'B'
				name++;
				
				length+=normalized.size(); //update last_cellId value
			}
			
			index++; //just for file name
			
			//Add the pair to the list
			pairs.add(pair);
		}
		
		//Compute jaccard and write the result to file
		StringBuilder builder=eTools.computeJaccard2(pairs);
		logIO.writeJaccardLog(builder, out);
	}
	
	/*public void executeValidation2(String logFile,
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
		Double north = new Double(infos.get(3).get(0)); //north of bbox
		Double east = new Double(infos.get(3).get(1));
		Double south = new Double(infos.get(3).get(2));
		Double west = new Double(infos.get(3).get(3));
		
		//number of cell of a side of the bbox
		int gnum = (int) Math.sqrt(Integer.parseInt(infos.get(3).get(4)));
		
		//Get the grid
		BoundingBox bbox = new BoundingBox(north, east, south, west);
		
    	ArrayList<BoundingBox> data = new ArrayList<BoundingBox>();
    	Grid grid=new Grid();
    	grid.setCellsNumber(gnum);
    	grid.setBbox(bbox);
    	grid.setStructure(data);
    	grid.createCells();
    	
		//Fill in the matrix of single venues
		//All the columns will be considered.
		ArrayList<ArrayList<String>> matrix = eTools.buildSinglesFromList(list);
		
		//Get the header
		ArrayList<String> header = eTools.getHeaderFromList(list);
		
		//Fill in the list of features for transformation
		//Only the categories will be considered
		ArrayList<String> features = eTools.getFeaturesFromListV(list);
		
		//Group the venues and get the value of each cell
		ArrayList<Double> bboxArea = eTools.getAreas(data);
		
		//This  variable will contain all the pairs of sets of the folds
		ArrayList<Pair<?,?>> pairs = new ArrayList<>();
		
		ImportTools tools = new ImportTools();
		ClusteringOperator co=new ClusteringOperator();
		int index = 1;
		int length = 0;
		
		for(int i=0; i<fnum; i++) {
			
			//Do the holdout
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>(matrix);
			ArrayList<ArrayList<ArrayList<String>>> holdoutPrint = eTools.doHoldOut(temp, 2);
			
			//Write to file the singles
			dataIO.printSinglesForValidation(holdoutPrint.get(0), 
											 header, 
											 out+"/fold_"+index, 
											 "/A-singles-matrix.csv");
			dataIO.printSinglesForValidation(holdoutPrint.get(1), 
					 						 header, 
					 						 out+"/fold_"+index, 
					 						 "/B-singles-matrix.csv");
			
			//I don't need timestamp, been_here, venue_id, venue_lat, venue_lng anymore
			//Cast also strings to double values
			ArrayList<ArrayList<ArrayList<Double>>> holdout = eTools.removeVenueInformations(holdoutPrint);
			
			//Group the sets to cell
			ArrayList<ArrayList<ArrayList<Double>>> initialSets = eTools.groupFolds(data, holdout);
			
			//Check if all the cells are included in each set
			//ArrayList<ArrayList<ArrayList<Double>>> sets = eTools.checkCells(data, initialSets);
			ArrayList<ArrayList<ArrayList<Double>>> sets = new ArrayList<ArrayList<ArrayList<Double>>>(initialSets);
			
			//This variable will contain the cells of the resulting clustering
			//for the pair of sets
			Pair<HashMap<String, Vector<Integer>>, 
				 HashMap<String, Vector<Integer>>> pair = new Pair<>(null, null);
			
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
				
				if(name == 'A')
					pair.setFirst(setClustering);
				else
					pair.setSecond(setClustering);
				
				//write down the clustering of the resulting holdout to file
				logIO.writeHoldoutLog2(setClustering, out, name, index);
				
				//switch the set name from 'A' to 'B'
				name++;
				
				length+=normalized.size(); //update last_cellId value
			}
			
			index++; //just for file name
			
			//Add the pair to the list
			pairs.add(pair);
		}
		
		//Compute jaccard and write the result to file
		StringBuilder builder=eTools.computeJaccard2(pairs);
		logIO.writeJaccardLog(builder, out);
    }*/
}