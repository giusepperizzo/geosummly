package it.unito.geosummly;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class DiscoveryOperator {
	
	public DiscoveryOperator() {}
	
	public void execute(String in, String out, int comb) throws IOException {
		
		//Read csv file and create the matrix without coordinate values
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		FileReader reader =new FileReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
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
			matrix.add(rec);
		}
			
		//Get standard deviation values
		DiscoveryTools dt=new DiscoveryTools();
		ArrayList<Double> meanDensities=dt.getMeanArray(matrix);
		ArrayList<ArrayList<Double>> stdMatrix=dt.getStdMatrix(matrix);
		ArrayList<Double> stdSingles=new ArrayList<Double>(stdMatrix.get(0));
		double n=matrix.size();
		
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
			deltadValues.addAll(dt.getCombinations(matrix, new ArrayList<Double>(), meanDensities, combinations, 0, 0, n));
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
			System.out.println("combination="+j+" eseguita");
		}
		
		//Write down the matrices to file
		DataPrinter dp=new DataPrinter();
		dp.printResultHorizontal(null, stdMatrix, dt.getFeaturesLabel("std", feat), out+ "/std-values.csv");
		dp.printResultVertical(deltadValues, featuresDeltad, out+ "/deltad-values.csv");
	}
}
