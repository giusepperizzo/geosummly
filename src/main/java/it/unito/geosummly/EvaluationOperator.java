package it.unito.geosummly;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class EvaluationOperator {
	
	public EvaluationOperator() {}
	
	public void executeCorrectness(String in, String out, int mnum) throws IOException{
		
		//Read csv file without considering coordinate values
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		FileReader reader =new FileReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
		
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
		DataPrinter dp=new DataPrinter();
		
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
			dp.printResultHorizontal(null, densityRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "density_rnd", feat), out+"/random-density-transformation-matrix-"+i+".csv");
			dp.printResultHorizontal(null, normalizedRandomMatrix, tools.getFeaturesLabel(CoordinatesNormalizationType.MISSING, "normalized_density_rnd", feat), out+"/random-normalized-transformation-matrix-"+i+".csv");
		}
	}
	
	public void executeValidation(String in, String out, int fnum) throws IOException {
		
		//Read csv file without considering the first three columns: timestamp, beenHere, venueId
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		FileReader reader =new FileReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
		
		//Remove columns
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			for(int j=3;j<list.get(k).size();j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		
		//Remove columns + venueLat, venueLng, focalLat, focalLng
		for(int k=7;k<list.get(0).size();k++) {
			features.add(list.get(0).get(k));
		}

		//Create fnum matrices of singles with N/fnum random venues for each matrix
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices=new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> lastMatrix=new ArrayList<ArrayList<Double>>(matrix);
		ArrayList<ArrayList<Double>> ithMatrix;
		int dimension=matrix.size()/fnum;
		int randomValue;
		Random random = new Random();
		for(int i=0;i<fnum-1;i++) {
			ithMatrix=new ArrayList<ArrayList<Double>>();
			for(int j=0;j<dimension;j++) {
				randomValue=random.nextInt(lastMatrix.size()); //random number between 0 (included) and lastMatrix.size() (excluded)
				ithMatrix.add(lastMatrix.get(randomValue));
			}
			allMatrices.add(ithMatrix);
			lastMatrix.removeAll(ithMatrix);
		}
		allMatrices.add(lastMatrix);
		
		//Group the venues
		TransformationTools tools=new TransformationTools();
		ArrayList<BoundingBox> data=tools.getBoxes(matrix);
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped=new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> ithGrouped;
		for(ArrayList<ArrayList<Double>> m: allMatrices) {
			ithGrouped=new ArrayList<ArrayList<Double>>();
			for(BoundingBox b: data) {
				ithGrouped.add(tools.groupSinglesToCell(b, m));
			}
			allGrouped.add(ithGrouped);
		}
		
		//Get the areas
		double edgeValue=tools.getDistance(data.get(0).getCenterLat(), data.get(0).getCenterLng(), data.get(1).getCenterLat(), data.get(1).getCenterLng());
		double areaValue=Math.pow(edgeValue, 2);
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		for(int i=0; i<matrix.size();i++)
			bboxArea.add(areaValue);
		
		//Get the map for transformation
		HashMap<String, Integer> map=new HashMap<>();
		int mapIndex=2;
		for(String s: features) {
			map.put(s, mapIndex);
			mapIndex++;
		}
		
		//Transform all the random matrices and write the to file
		DataPrinter dp=new DataPrinter();
		TransformationMatrix ithTm;
		ArrayList<ArrayList<Double>> ithFrequency;
		ArrayList<ArrayList<Double>> ithDensity;
		ArrayList<ArrayList<Double>> ithNormalized;
		int index=0; //used for file name
		for(ArrayList<ArrayList<Double>> grouped: allGrouped) {
			ithTm=new TransformationMatrix();
			ithFrequency=tools.sortMatrix(CoordinatesNormalizationType.NORM, grouped, map);
			ithTm.setFrequencyMatrix(ithFrequency);
			ithDensity=tools.buildDensityMatrix(CoordinatesNormalizationType.NORM, ithTm.getFrequencyMatrix(), bboxArea);
			ithTm.setDensityMatrix(ithDensity);
			ithNormalized=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, ithTm.getDensityMatrix());
			ithTm.setNormalizedMatrix(ithNormalized);

			ithTm.setHeader(tools.sortFeatures(map));
			
			//write down the transformation matrices to file
			index++; //just for file name
			dp.printResultHorizontal(null, ithTm.getFrequencyMatrix(), tools.getFeaturesLabel(CoordinatesNormalizationType.NORM, "f", ithTm.getHeader()), out+"/frequency-transformation-matrix-fold"+index+".csv");
			dp.printResultHorizontal(null, ithTm.getDensityMatrix(), tools.getFeaturesLabel(CoordinatesNormalizationType.NORM, "density", ithTm.getHeader()), out+"/density-transformation-matrix-fold"+index+".csv");
			dp.printResultHorizontal(null, ithTm.getNormalizedMatrix(), tools.getFeaturesLabel(CoordinatesNormalizationType.NORM, "normalized_density", ithTm.getHeader()), out+"/normalized-transformation-matrix-fold"+index+".csv");
		}
	}
}
