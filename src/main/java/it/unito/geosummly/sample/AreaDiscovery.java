package it.unito.geosummly.sample;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.FoursquareDataObject;
import it.unito.geosummly.FoursquareSearchVenues;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class AreaDiscovery {
	
	public static Logger logger = Logger.getLogger(AreaDiscovery.class.toString());
	
	public static void main(String[] args) throws UnknownHostException, FoursquareApiException {
		double north= 48.86985261204524; //coordinates of Paris (city center)
		double south= 48.840486362012925;
		double west= 2.3219776258338243;
		double east= 2.384977351175621;
		
		int cellsNumber=20; //it corresponds to cellsNumber of class grid 
		int sampleNumber=100; //number of samples of the sample area
		ArrayList<BoundingBox> gridStructure=new ArrayList<BoundingBox>();
		ArrayList<ArrayList<Double>> freqStructure=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> devStructure=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		ArrayList<String> features=new ArrayList<String>(); //Sorted list of the distinct category (related to the hash map)
		SampleArea sA=new SampleArea(north, south, west, east);
		sA.setGridStructure(gridStructure);
		sA.setFreqStructure(freqStructure);
		sA.setDevStructure(devStructure);
		sA.setCellsNumber(cellsNumber);
		sA.setSampleNumber(sampleNumber);
		sA.setMap(map);
		sA.setFeatures(features);
		sA.createSampleGrid(); //fill the gridStructure with 'sampleNumber' samples
		ArrayList<Double> row_of_matrix; //row of the matrixStructure of the sample area (one for each box);
		
		int cat_num=0; //total number of categories of a box
		int tot_num=0; //overall number of categories
		ArrayList<String> distinct_list; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list; //list of the occurrences of the distinct categories for a single cell
		
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo;
		for(BoundingBox b: sA.getGridStructure()){
			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			cat_num=fsv.getCategoriesNumber(venueInfo);//set the total number of categories of the cell
			distinct_list=fsv.createCategoryList(venueInfo); 
			occurrences_list=fsv.getCategoryOccurences(venueInfo, distinct_list);
			sA.updateMap(distinct_list); //update the hash map
			row_of_matrix=sA.fillRecord(occurrences_list, distinct_list, cat_num, b.getArea()); //create a consistent row (related to the categories)
			if(tot_num < row_of_matrix.size())
				tot_num=row_of_matrix.size(); //update the overall number of categories
			sA.addRecord(row_of_matrix);
		}
		sA.fixRecordsLength(tot_num); //update rows length for consistency
		sA.createStdDevMatrix(sA.getFreqStructure()); //get the matrix with standard deviation values
		
		// write down the matrix of normalized frequencies and of standard deviation values to a file		
		ByteArrayOutputStream bout_freq = new ByteArrayOutputStream();
		OutputStreamWriter osw_freq = new OutputStreamWriter(bout_freq);
		ByteArrayOutputStream bout_dev = new ByteArrayOutputStream();
		OutputStreamWriter osw_dev = new OutputStreamWriter(bout_dev);
        try {
            CSVPrinter csv_freq = new CSVPrinter(osw_freq, CSVFormat.DEFAULT);
            CSVPrinter csv_dev = new CSVPrinter(osw_dev, CSVFormat.DEFAULT);
		
            // write the header of the matrix
            ArrayList<String> hdr=sA.getFeatures();
            for(String s: hdr) {
            	csv_freq.print(s);
            	csv_dev.print(s);
            }
            csv_freq.println();
            csv_dev.println();
            
            // iterate per each row of the matrix
            ArrayList<ArrayList<Double>> freq=sA.getFreqStructure();
            ArrayList<ArrayList<Double>> dev=sA.getDevStructure();
            
            for(ArrayList<Double> a: freq) {
            	for(Double d: a) {
            		csv_freq.print(d);
            	}
            	csv_freq.println();
            }
            csv_freq.flush();
            
            for(ArrayList<Double> a: dev) {
            	for(Double d: a) {
            		csv_dev.print(d);
            	}
            	csv_dev.println();
            }
            csv_dev.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		OutputStream outputStream_freq;
		OutputStream outputStream_dev;
        try {
            outputStream_freq = new FileOutputStream ("output/samples/freqMetroParis.csv");
            outputStream_dev = new FileOutputStream ("output/samples/devMetroParis.csv");
            bout_freq.writeTo(outputStream_freq);
            bout_dev.writeTo(outputStream_dev);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
