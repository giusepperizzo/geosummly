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
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 1**************************************/
		/***************************************************************************************/
		
		double north= 45.10975600522702; //coordinates of Turin for local discovery
		double south= 45.04393354716772;
		double west= 7.630176544189453;
		double east= 7.734889984130859;
		
		int cellsNumber=20; //it corresponds to cellsNumber of class grid
		int sampleNumber=100; //number of samples of the sample area
		ArrayList<BoundingBox> gridStructure=new ArrayList<BoundingBox>();
		ArrayList<ArrayList<Double>> occurrenceStructure=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> densityStructure=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		SampleArea sA=new SampleArea(north, south, west, east);
		sA.setGridStructure(gridStructure);
		sA.setOccurrenceStructure(occurrenceStructure);
		sA.setDensityStructure(densityStructure);
		sA.setCellsNumber(cellsNumber);
		sA.setSampleNumber(sampleNumber);
		sA.setMap(map);
		sA.updateMap(features);
		sA.setFeatures(features);
		sA.createSampleGrid(); //fill the gridStructure with 'sampleNumber' samples
		
		ArrayList<String> distinct_list; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list; //list of the occurrences of the distinct categories for a single cell
		
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo;
		for(BoundingBox b: sA.getGridStructure()){
			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			distinct_list=fsv.createCategoryList(venueInfo); 
			occurrences_list=fsv.getCategoryOccurences(venueInfo, distinct_list);
			sA.fillRecord(occurrences_list, distinct_list, b.getArea()); //create a consistent row (related to the categories)
		}
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 2**************************************/
		/***************************************************************************************/

		/*double north_1= 51.537473682752406; //coordinates of London city center
		double south_1= 51.48159670633237;
		double west_1= -0.1802444038912654;
		double east_1= -0.05201335530728102;
		
		int cellsNumber_1=20; //it corresponds to cellsNumber of class grid
		int sampleNumber_1=100; //number of samples of the sample area
		ArrayList<BoundingBox> gridStructure_1=new ArrayList<BoundingBox>();
		ArrayList<ArrayList<Double>> occurrenceStructure_1=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> densityStructure_1=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map_1=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		SampleArea sA_1=new SampleArea(north_1, south_1, west_1, east_1);
		sA_1.setGridStructure(gridStructure_1);
		sA_1.setOccurrenceStructure(occurrenceStructure_1);
		sA_1.setDensityStructure(densityStructure_1);
		sA_1.setCellsNumber(cellsNumber_1);
		sA_1.setSampleNumber(sampleNumber_1);
		sA_1.setMap(map_1);
		sA_1.updateMap(features);
		sA_1.setFeatures(features);
		sA_1.createSampleGrid(); //fill the gridStructure with 'sampleNumber' samples
		
		ArrayList<String> distinct_list_1; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list_1; //list of the occurrences of the distinct categories for a single cell
		
		FoursquareSearchVenues fsv_1=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo_1;
		for(BoundingBox b: sA_1.getGridStructure()){
			//Venues of a single cell
			venueInfo_1=fsv_1.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			distinct_list_1=fsv_1.createCategoryList(venueInfo_1); 
			occurrences_list_1=fsv_1.getCategoryOccurences(venueInfo_1, distinct_list_1);
			sA_1.fillRecord(occurrences_list_1, distinct_list_1, b.getArea()); //create a consistent row (related to the categories)
		}
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 3**************************************/
		/***************************************************************************************/
		
		/*double north_2= 41.91875659707589; //coordinates of Rome city center
		double south_2= 41.87409864599624;
		double west_2= 12.451157569885254;
		double east_2= 12.518448829650879;
		
		int cellsNumber_2=20; //it corresponds to cellsNumber of class grid
		int sampleNumber_2=100; //number of samples of the sample area
		ArrayList<BoundingBox> gridStructure_2=new ArrayList<BoundingBox>();
		ArrayList<ArrayList<Double>> occurrenceStructure_2=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> densityStructure_2=new ArrayList<ArrayList<Double>>();
		HashMap<String, Integer> map_2=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		SampleArea sA_2=new SampleArea(north_2, south_2, west_2, east_2);
		sA_2.setGridStructure(gridStructure_2);
		sA_2.setOccurrenceStructure(occurrenceStructure_2);
		sA_2.setDensityStructure(densityStructure_2);
		sA_2.setCellsNumber(cellsNumber_2);
		sA_2.setSampleNumber(sampleNumber_2);
		sA_2.setMap(map_2);
		sA_2.updateMap(features);
		sA_2.setFeatures(features);
		sA_2.createSampleGrid(); //fill the gridStructure with 'sampleNumber' samples
		
		ArrayList<String> distinct_list_2; //list of all the distinct categories for a single cell
		ArrayList<Integer> occurrences_list_2; //list of the occurrences of the distinct categories for a single cell
		
		FoursquareSearchVenues fsv_2=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo_2;
		for(BoundingBox b: sA_2.getGridStructure()){
			//Venues of a single cell
			venueInfo_2=fsv_2.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			distinct_list_2=fsv_2.createCategoryList(venueInfo_2); 
			occurrences_list_2=fsv_2.getCategoryOccurences(venueInfo_2, distinct_list_2);
			sA_2.fillRecord(occurrences_list_2, distinct_list_2, b.getArea()); //create a consistent row (related to the categories)
		}*/
		
		/***************************************************************************************/
		/******************************CREATE THE BIG MATRIX**********************************/
		/***************************************************************************************/
		
		ArrayList<ArrayList<Double>> bigAreaOcc=new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> bigAreaDens=new ArrayList<ArrayList<Double>>();
		ClassOfArea metropolitan=new ClassOfArea("Local discovery", bigAreaOcc, bigAreaDens, sA.getOccurrenceStructure(), sA.getDensityStructure());
		//ClassOfArea metropolitan=new ClassOfArea("Residential", bigAreaOcc, bigAreaDens, sA.getOccurrenceStructure(), sA_1.getOccurrenceStructure(), sA_2.getOccurrenceStructure(), sA.getDensityStructure(), sA_1.getDensityStructure(), sA_2.getDensityStructure());
		ArrayList<Double> meanDensities=metropolitan.getMeanArray(metropolitan.getBigAreaDens());
		ArrayList<ArrayList<Double>> stdMatrix=metropolitan.getStdMatrix(metropolitan.getBigAreaDens());
		ArrayList<Double> stdSingles=new ArrayList<Double>(stdMatrix.get(0));
		double n=metropolitan.getBigAreaDens().size();
		ArrayList<Double> singleDensities=metropolitan.getSingleDensities(meanDensities, stdSingles, n);
		ArrayList<Double> pairDensities=metropolitan.getPairDensities(metropolitan.getBigAreaDens(), meanDensities, n*n);
		ArrayList<Double> tripleDensities=metropolitan.getTripleDensities(metropolitan.getBigAreaDens(), meanDensities, n*n*n);
		ArrayList<String> featureFreq=metropolitan.getFeaturesLabel("f",features);
		ArrayList<String> featureDens=metropolitan.getFeaturesLabel("density",features);
		ArrayList<String> featureStd=metropolitan.getFeaturesLabel("std", features);
		ArrayList<String> singleFeatures=metropolitan.getFeaturesLabel("deltad", features);
		ArrayList<String> pairFeatures= metropolitan.getFeaturesForPairs(features);
		ArrayList<String> tripleFeatures= metropolitan.getFeaturesForTriples(features);
		
		/***************************************************************************************/
		/******************************WRITE OUTPUT TO CSV**********************************/
		/***************************************************************************************/
		
		// write down the matrices of densities and standard deviation values to a file		
		ByteArrayOutputStream bout_freq = new ByteArrayOutputStream();
		OutputStreamWriter osw_freq = new OutputStreamWriter(bout_freq);
		ByteArrayOutputStream bout_dens = new ByteArrayOutputStream();
		OutputStreamWriter osw_dens = new OutputStreamWriter(bout_dens);
		ByteArrayOutputStream bout_std = new ByteArrayOutputStream();
		OutputStreamWriter osw_std = new OutputStreamWriter(bout_std);
		ByteArrayOutputStream bout_deltad = new ByteArrayOutputStream();
		OutputStreamWriter osw_deltad = new OutputStreamWriter(bout_deltad);
        try {
            CSVPrinter csv_freq = new CSVPrinter(osw_freq, CSVFormat.DEFAULT);
            CSVPrinter csv_dens = new CSVPrinter(osw_dens, CSVFormat.DEFAULT);
            CSVPrinter csv_std = new CSVPrinter(osw_std, CSVFormat.DEFAULT);
            CSVPrinter csv_deltad = new CSVPrinter(osw_deltad, CSVFormat.DEFAULT);
		
            // write the header of the matrix for frequency, density and standard deviation
            for(int i=0;i<features.size();i++) {
            	csv_freq.print(featureFreq.get(i));
            	csv_dens.print(featureDens.get(i));
            	csv_std.print(featureStd.get(i));
            }
            csv_freq.println();
            csv_dens.println();
            csv_std.println();
            
            
            // write the values of the matrix for frequency, density and standard deviation
            for(ArrayList<Double> a: metropolitan.getBigAreaOcc()) {
            	for(Double d: a) {
            		csv_freq.print(d);
            	}
            	csv_freq.println();
            }
            csv_freq.flush();
            
            for(ArrayList<Double> a: metropolitan.getBigAreaDens()) {
            	for(Double d: a) {
            		csv_dens.print(d);
            	}
            	csv_dens.println();
            }
            csv_dens.flush();
            
            for(ArrayList<Double> a: stdMatrix) {
            	for(Double d: a) {
            		csv_std.print(d);
            	}
            	csv_std.println();
            }
            csv_std.flush();
            
            //write down values for density of singles, pairs, triples
            for(int i=0;i<singleDensities.size();i++) {
            	csv_deltad.print(singleFeatures.get(i));
        		csv_deltad.print(singleDensities.get(i));
            	csv_deltad.println();
            }
            csv_deltad.println();
	        for(int i=0;i<pairDensities.size();i++) {
	        	csv_deltad.print(pairFeatures.get(i));
	    		csv_deltad.print(pairDensities.get(i));
	        	csv_deltad.println();
	        }
	        csv_deltad.println();
	        for(int i=0;i<pairDensities.size();i++) {
	        	csv_deltad.print(tripleFeatures.get(i));
	    		csv_deltad.print(tripleDensities.get(i));
	        	csv_deltad.println();
	        }
	        csv_deltad.flush();
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		OutputStream outputStream_freq;
		OutputStream outputStream_dens;
		OutputStream outputStream_std;
		OutputStream outputStream_deltad;
        try {
            outputStream_freq = new FileOutputStream ("output/discovery/metropolitan/local discovery/frequencyMetropolitanTurin.csv");
            outputStream_dens = new FileOutputStream ("output/discovery/metropolitan/local discovery/densityMetropolitanTurin.csv");
            outputStream_std = new FileOutputStream ("output/discovery/metropolitan/local discovery/stdMetropolitanTurin.csv");
            outputStream_deltad = new FileOutputStream ("output/discovery/metropolitan/local discovery/deltadMetropolitanTurin.csv");
            bout_freq.writeTo(outputStream_freq);
            bout_dens.writeTo(outputStream_dens);
            bout_std.writeTo(outputStream_std);
            bout_deltad.writeTo(outputStream_deltad);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
