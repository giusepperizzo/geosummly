package it.unito.geosummly.discovery;

import fi.foyt.foursquare.api.FoursquareApiException;
import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.DataPrinter;
import it.unito.geosummly.FoursquareDataObject;
import it.unito.geosummly.FoursquareSearchVenues;
import it.unito.geosummly.Grid;
import it.unito.geosummly.InformationType;
import it.unito.geosummly.TransformationTools;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

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
		
		InformationType infoType=InformationType.CELL;
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 1**************************************/
		/***************************************************************************************/
		
		double north= 45.84724672082038; //coordinates of Mont Blanc
		double south= 45.81967790116538;
		double west= 6.848859786987305;
		double east= 6.888341903686523;
		
		int cellsNumber=20; //it corresponds to cellsNumber of class grid
		int sampleNumber=100; //number of samples of the sample area
		
		BoundingBox bbox=new BoundingBox(north, south, west, east); //Initialize the bounding box
		ArrayList<BoundingBox> data=new ArrayList<BoundingBox>(); //Data structure
		
		//Create the grid
		Grid grid=new Grid();
		grid.setCellsNumber(cellsNumber);
		grid.setBbox(bbox); //I need the bounding box in order to get the initial coordinates 
		grid.setStructure(data);
		grid.createRandomCells(sampleNumber);
		
		//Get the tools class and its support variables
		TransformationTools tools=new TransformationTools();
		HashMap<String, Integer> map=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		tools.setMap(tools.updateMapWithCell(map, features));
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		ArrayList<FoursquareDataObject> cellVenue;
		
		for(BoundingBox b: data){
			//Venues of a single cell
			cellVenue=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			//Generate a matrix with geopoints informations
			supportMatrix=tools.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix, cellVenue);
			bboxArea.add(b.getArea());
		}
		supportMatrix=tools.fixRowsLength(tools.getTotal(), supportMatrix); //update rows length for consistency
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 2**************************************/
		/***************************************************************************************/


		double north_1= 42.54296310520118; //coordinates of Gran Sasso National Park
		double south_1= 42.45588764197166;
		double west_1= 13.43353271484375;
		double east_1= 13.542022705078125;
		
		int cellsNumber_1=20; //it corresponds to cellsNumber of class grid
		int sampleNumber_1=100; //number of samples of the sample area
		
		BoundingBox bbox_1=new BoundingBox(north_1, south_1, west_1, east_1); //Initialize the bounding box
		ArrayList<BoundingBox> data_1=new ArrayList<BoundingBox>(); //Data structure
		
		//Create the grid
		Grid grid_1=new Grid();
		grid_1.setCellsNumber(cellsNumber_1);
		grid_1.setBbox(bbox_1); //I need the bounding box in order to get the initial coordinates 
		grid_1.setStructure(data_1);
		grid_1.createRandomCells(sampleNumber_1);
		
		//Get the tools class and its support variables
		TransformationTools tools_1=new TransformationTools();
		HashMap<String, Integer> map_1=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		tools_1.setMap(tools_1.updateMapWithCell(map_1, features));
		ArrayList<ArrayList<Double>> supportMatrix_1=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea_1=new ArrayList<Double>();
		ArrayList<FoursquareDataObject> cellVenue_1;
		
		for(BoundingBox b: data_1){
			//Venues of a single cell
			cellVenue_1=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			//Generate a matrix with geopoints informations
			supportMatrix_1=tools_1.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix_1, cellVenue_1);
			bboxArea_1.add(b.getArea());
		}
		supportMatrix_1=tools_1.fixRowsLength(tools_1.getTotal(), supportMatrix_1); //update rows length for consistency
		
		/***************************************************************************************/
		/*********************************VENUES OF AREA 3**************************************/
		/***************************************************************************************/
		
		double north_2= 42.54296310520118; //coordinates of Gran Sasso National Park
		double south_2= 42.45588764197166;
		double west_2= 13.43353271484375;
		double east_2= 13.542022705078125;
		
		int cellsNumber_2=20; //it corresponds to cellsNumber of class grid
		int sampleNumber_2=100; //number of samples of the sample area
		
		BoundingBox bbox_2=new BoundingBox(north_2, south_2, west_2, east_2); //Initialize the bounding box
		ArrayList<BoundingBox> data_2=new ArrayList<BoundingBox>(); //Data structure
		
		//Create the grid
		Grid grid_2=new Grid();
		grid_2.setCellsNumber(cellsNumber_2);
		grid_2.setBbox(bbox_2); //I need the bounding box in order to get the initial coordinates 
		grid_2.setStructure(data_2);
		grid_2.createRandomCells(sampleNumber_2);
		
		//Get the tools class and its support variables
		TransformationTools tools_2=new TransformationTools();
		HashMap<String, Integer> map_2=new HashMap<String, Integer>(); //HashMap of all the distinct categories
		tools_2.setMap(tools_2.updateMapWithCell(map_2, features));
		ArrayList<ArrayList<Double>> supportMatrix_2=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea_2=new ArrayList<Double>();
		ArrayList<FoursquareDataObject> cellVenue_2;
		
		for(BoundingBox b: data_2){
			//Venues of a single cell
			cellVenue_2=fsv.searchVenues(b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			//Generate a matrix with geopoints informations
			supportMatrix_2=tools_2.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix_2, cellVenue_2);
			bboxArea_2.add(b.getArea());
		}
		supportMatrix_2=tools_2.fixRowsLength(tools_2.getTotal(), supportMatrix_2); //update rows length for consistency
		
		/***************************************************************************************/
		/******************************CREATE THE BIG MATRIX**********************************/
		/***************************************************************************************/
		
		//get the frequency and density matrices
		ArrayList<ArrayList<Double>> frequencyMatrix=tools.sortMatrixNoCoord(supportMatrix, tools.getMap());
		ArrayList<ArrayList<Double>> densityMatrix=tools.buildDensityMatrixNoCoord(frequencyMatrix, bboxArea);
		ArrayList<ArrayList<Double>> frequencyMatrix_1=tools_1.sortMatrixNoCoord(supportMatrix_1, tools_1.getMap());
		ArrayList<ArrayList<Double>> densityMatrix_1=tools_1.buildDensityMatrixNoCoord(frequencyMatrix_1, bboxArea_1);
		ArrayList<ArrayList<Double>> frequencyMatrix_2=tools_2.sortMatrixNoCoord(supportMatrix_2, tools_2.getMap());
		ArrayList<ArrayList<Double>> densityMatrix_2=tools_2.buildDensityMatrixNoCoord(frequencyMatrix_2, bboxArea_2);
		
		//aggregate the matrices
		ArrayList<ArrayList<Double>> bigAreaFreq=new ArrayList<ArrayList<Double>>();
		bigAreaFreq.addAll(frequencyMatrix);
		bigAreaFreq.addAll(frequencyMatrix_1);
		bigAreaFreq.addAll(frequencyMatrix_2);
		ArrayList<ArrayList<Double>> bigAreaDens=new ArrayList<ArrayList<Double>>();
		bigAreaDens.addAll(densityMatrix);
		bigAreaDens.addAll(densityMatrix_1);
		bigAreaDens.addAll(densityMatrix_2);
		
		//get standard deviation values
		DiscoveryTools dt=new DiscoveryTools();
		ArrayList<Double> meanDensities=dt.getMeanArray(bigAreaDens);
		ArrayList<ArrayList<Double>> stdMatrix=dt.getStdMatrix(bigAreaDens);
		ArrayList<Double> stdSingles=new ArrayList<Double>(stdMatrix.get(0));
		double n=bigAreaDens.size();
		ArrayList<Double> deltadValues=new ArrayList<Double>();
		deltadValues.addAll(dt.getSingleDensities(meanDensities, stdSingles, n)); //add deltad values of singles
		deltadValues.addAll(dt.getPairDensities(bigAreaDens, meanDensities, n)); //add deltad values of couples
		deltadValues.addAll(dt.getTripleDensities(bigAreaDens, meanDensities, n)); //n*n*n
		deltadValues.addAll(dt.getQuadrupleDensities(bigAreaDens, meanDensities, n)); //n*n*n*n
		deltadValues.addAll(dt.getQuintupleDensities(bigAreaDens, meanDensities, n)); //n*n*n*n*n

		/***************************************************************************************/
		/******************************WRITE OUTPUT TO FILE**********************************/
		/***************************************************************************************/
		
		ArrayList<String> featureFreq=dt.getFeaturesLabel("f",features);
		ArrayList<String> featureDens=dt.getFeaturesLabel("density",features);
		ArrayList<String> featureStd=dt.getFeaturesLabel("std", features);
		ArrayList<String> featuresDeltad=new ArrayList<String>();
		featuresDeltad.addAll(dt.getFeaturesLabel("deltad", features)); //add features of singles
		featuresDeltad.addAll(dt.getFeaturesForPairs(features)); //add features of pairs
		featuresDeltad.addAll(dt.getFeaturesForTriples(features));
		featuresDeltad.addAll(dt.getFeaturesForQuadruples(features));
		featuresDeltad.addAll(dt.getFeaturesForQuintuples(features)); //QUESTA VARIABILE FEATURES CORRISPONDE ALLA PRIMA VARIABILE DICHIARATA NEL METODO
		
		// write down the matrices to file
		DataPrinter dp=new DataPrinter();
		dp.printResult(bigAreaFreq, featureFreq, "output/discovery/metropolitan/20140203-frequencyMetropolitanArea.csv");
		dp.printResult(bigAreaDens, featureDens, ""); //SELECT THE OUTPUT!
		dp.printResult(stdMatrix, featureStd, "");
		dp.printResultForDiscovery(deltadValues, featuresDeltad, "");
	}
}
