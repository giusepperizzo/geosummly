package it.unito.geosummly.discovery;

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
		ArrayList<String> features=new ArrayList<String>();
		features.add("Arts & Entertainment");
		features.add("College & University");
		features.add("Event");
		features.add("Food");
		features.add("Nightlife Spot");
		features.add("Outdoors & Recreation");
		features.add("Professional & Other Places");
		features.add("Residence");
		features.add("Shop & Service");
		features.add("Travel & Transport");
		
		double north= 42.54296310520118; //coordinates of GranSasso
		double south= 42.45588764197166;
		double west= 13.43353271484375;
		double east= 13.542022705078125;
		
		int cellsNumber=20; //it corresponds to cellsNumber of class grid
		int sampleNumber=100; //number of samples of the sample area
		ArrayList<BoundingBox> gridStructure=new ArrayList<BoundingBox>();
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> densityStructure=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> devStructure=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		SampleArea sA=new SampleArea(north, south, west, east);
		sA.setGridStructure(gridStructure);
		sA.setDensityStructure(densityStructure);
		sA.setDevStructure(devStructure);
		sA.setCellsNumber(cellsNumber);
		sA.setSampleNumber(sampleNumber);
		sA.setMap(map);
		sA.updateMap(features);
		sA.setFeatures(features);
		sA.createSampleGrid(); //fill the gridStructure with 'sampleNumber' samples
		ArrayList<Double> row_of_matrix; //one row per box;
		
		ArrayList<String> distinct_list; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list; //list of the occurrences of the distinct categories for a single cell
		
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo;
		for(BoundingBox b: sA.getGridStructure()){
			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			distinct_list=fsv.createCategoryList(venueInfo); 
			occurrences_list=fsv.getCategoryOccurences(venueInfo, distinct_list);
			row_of_matrix=sA.fillRecord(occurrences_list, distinct_list, b.getArea()); //create a consistent row (related to the categories)
			supportMatrix.add(row_of_matrix);
		}
		
		ArrayList<ArrayList<Double>> not_norm_dens=sA.getNotNormalizedDensities(supportMatrix); //get a structure with intra-feature normalized densities
		sA.getNormalizedDensities(not_norm_dens); //density values normalized in [0,1]
		sA.createStdDevMatrix(sA.getDensityStructure()); //get the matrix with standard deviation values
		
		// write down the matrices of densities and standard deviation values to a file		
		ByteArrayOutputStream bout_density = new ByteArrayOutputStream();
		OutputStreamWriter osw_density = new OutputStreamWriter(bout_density);
		ByteArrayOutputStream bout_dev = new ByteArrayOutputStream();
		OutputStreamWriter osw_dev = new OutputStreamWriter(bout_dev);
        try {
            CSVPrinter csv_density = new CSVPrinter(osw_density, CSVFormat.DEFAULT);
            CSVPrinter csv_dev = new CSVPrinter(osw_dev, CSVFormat.DEFAULT);
		
            // write the header of the matrix
            ArrayList<String> hdr=sA.getFeatures();
            for(String s: hdr) {
            	csv_density.print(s);
            	csv_dev.print(s);
            }
            csv_density.println();
            csv_dev.println();
            
            // iterate per each row of the matrix
            ArrayList<ArrayList<Double>> dens=sA.getDensityStructure();
            ArrayList<ArrayList<Double>> dev=sA.getDevStructure();
            
            for(ArrayList<Double> a: dens) {
            	for(Double d: a) {
            		csv_density.print(d);
            	}
            	csv_density.println();
            }
            csv_density.flush();
            
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
		
		OutputStream outputStream_density;
		OutputStream outputStream_dev;
        try {
            outputStream_density = new FileOutputStream ("output/discovery/densityNaturalGranSasso.csv");
            outputStream_dev = new FileOutputStream ("output/discovery/devNaturalGranSasso.csv");
            bout_density.writeTo(outputStream_density);
            bout_dev.writeTo(outputStream_dev);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
