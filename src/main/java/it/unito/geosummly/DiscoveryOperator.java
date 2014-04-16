package it.unito.geosummly;

import it.unito.geosummly.io.CSVDataIO;
import it.unito.geosummly.tools.DiscoveryTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVRecord;

public class DiscoveryOperator {
	
	private final int SAMPLES=50;
	public static Logger logger = Logger.getLogger(SamplingOperator.class.toString());
	
	public void execute(String in, String out, int comb, int rnum) throws IOException {
		
		//Read csv file and create the dataset without coordinate values
		CSVDataIO dataIO=new CSVDataIO();
		List<CSVRecord> list=dataIO.readCSVFile(in);
		
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> dataset = new ArrayList<ArrayList<Double>>();
		int index=1;
		if(list.get(0).get(1).contains("Latitude") || list.get(0).get(1).contains("Longitude"))
			index=3;
		
		for(int k=index;k<list.get(0).size();k++) {
			features.add(list.get(0).get(k));
		}
		
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			for(int j=index;j<list.get(k).size(); j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			dataset.add(rec);
		}
		
		//check rnum parameter
		if(rnum<=0)
			rnum=dataset.size();
		
		DiscoveryTools dt=new DiscoveryTools();
		
		//Matrix of means
		ArrayList<ArrayList<Double>> avgFiftyMat=dt.getMeanMatrix(dataset, SAMPLES, rnum);
		
		//Array with the means of the means previously computed
		ArrayList<Double> meansOfMeans=dt.getMeanArray(avgFiftyMat);
		
		//Deltad values of single features
		ArrayList<Double> deltadValues=new ArrayList<Double>();
		
		//Deltad values of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the values to combine
			Arrays.fill(combinations, -1);
			deltadValues.addAll(dt.getDeltadCombinations (avgFiftyMat, new ArrayList<Double>(), meansOfMeans, combinations, 0, 0, (SAMPLES-1) ));
			logger.log(Level.INFO, "All combinations of "+j+" values executed");
		}
		
		//Label of single features
		ArrayList<String >feat=dt.changeFeaturesLabel("density", "", features);
		ArrayList<String> featuresDeltad=new ArrayList<String>();
		featuresDeltad.addAll(dt.getFeaturesLabel("deltad", feat));
		
		//Label of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the features to combine
			Arrays.fill(combinations, -1);
			featuresDeltad.addAll(dt.getFeaturesForCombinations(new ArrayList<String>(), feat, combinations, 0, 0));
			logger.log(Level.INFO, "All combinations of "+j+" features executed");
		}
		
		//OLD DELTAD CALCULATION
		
		//Get rnum random cells
		/*if(rnum>0) {
			ArrayList<ArrayList<Double>> matrixRnd = new ArrayList<ArrayList<Double>>();
			Random r=new Random();
			int rnd=0;
			for(int i=0;i<rnum;i++) {
				rnd=r.nextInt(dataset.size());
				matrixRnd.add(dataset.get(rnd));
			}
			
			dataset=new ArrayList<ArrayList<Double>>(matrixRnd); //put the random cells in the matrix
		}
		
		//DiscoveryTools dt=new DiscoveryTools();
		
		ArrayList<Double> meanDensities=dt.getMeanArray(dataset);
		ArrayList<ArrayList<Double>> stdMatrix=dt.getStdMatrix(dataset);
		ArrayList<Double> stdSingles=new ArrayList<Double>(stdMatrix.get(0));
		double n=dataset.size();
		
		//The option combination have to be less or equal than the number of features
		if(comb > features.size())
			comb=features.size();
		
		//Deltad values of single features
		ArrayList<Double> deltadValues=new ArrayList<Double>();
		deltadValues.addAll(dt.getSingleDensities(meanDensities, stdSingles, n)); //add deltad values of singles
		
		//Deltad values of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the values to combine
			Arrays.fill(combinations, -1);
			deltadValues.addAll(dt.getCombinations(dataset, new ArrayList<Double>(), meanDensities, combinations, 0, 0, n));
			logger.log(Level.INFO, "All combinations of "+j+" values executed");
		}
		
		//Label of single features
		ArrayList<String >feat=dt.changeFeaturesLabel("density", "", features);
		ArrayList<String> featuresDeltad=new ArrayList<String>();
		featuresDeltad.addAll(dt.getFeaturesLabel("deltad", feat));
		
		//Label of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the features to combine
			Arrays.fill(combinations, -1);
			featuresDeltad.addAll(dt.getFeaturesForCombinations(new ArrayList<String>(), feat, combinations, 0, 0));
			logger.log(Level.INFO, "All combinations of "+j+" features executed");
		}*/
		
		//Write down the matrices to file
		//dataIO.printResultHorizontal(null, stdMatrix, dt.getFeaturesLabel("std", feat), out, "/std-values.csv");*/
		dataIO.printResultVertical(deltadValues, featuresDeltad, out, "/deltad-values.csv");
	}
}
