package it.unito.geosummly.sample;

import java.util.ArrayList;
import java.util.HashMap;

import it.unito.geosummly.BoundingBox;

public class SampleArea {
	
	private double north;
	private double south;
	private double west;
	private double east;
	private int cellsNumber;
	private int sampleNumber;
	private ArrayList<BoundingBox> gridStructure;
	private ArrayList<ArrayList<Double>> freqStructure; //matrix of normalized frequencies
	private ArrayList<ArrayList<Double>> devStructure; //matrix of standard deviations
	private HashMap<String, Integer> map; //Map category to index
	private ArrayList<String> features; //Sorted list of categories by column index
	
	public SampleArea(double north, double south, double west, double east) {
		this.north=north;
		this.south=south;
		this.west=west;
		this.east=east;
	}
	
	public double getNorth() {
		return north;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public double getSouth() {
		return south;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public double getWest() {
		return west;
	}

	public void setWest(double west) {
		this.west = west;
	}

	public double getEast() {
		return east;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public int getCellsNumber() {
		return cellsNumber;
	}

	public void setCellsNumber(int cellsNumber) {
		this.cellsNumber = cellsNumber;
	}

	public int getSampleNumber() {
		return sampleNumber;
	}

	public void setSampleNumber(int sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public ArrayList<BoundingBox> getGridStructure() {
		return gridStructure;
	}
	
	public void setGridStructure(ArrayList<BoundingBox> gridStructure) {
		this.gridStructure = gridStructure;
	}
	
	public ArrayList<ArrayList<Double>> getFreqStructure() {
		return freqStructure;
	}

	public void setFreqStructure(ArrayList<ArrayList<Double>> freqStructure) {
		this.freqStructure = freqStructure;
	}
	
	public ArrayList<ArrayList<Double>> getDevStructure() {
		return devStructure;
	}

	public void setDevStructure(ArrayList<ArrayList<Double>> devStructure) {
		this.devStructure = devStructure;
	}

	public HashMap<String, Integer> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}

	public ArrayList<String> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<String> features) {
		this.features = features;
	}
	
	public void createSampleGrid() {
		BoundingBox singleCell;
		int cellnum=this.cellsNumber;
		double cellWidth=(east-west)/cellnum; //Width of a single cell
		double cellHeight=(north-south)/cellnum;
		double northSingleCell;
		double southSingleCell;
		double westSingleCell;
		double eastSingleCell;
		int randomEastFactor;
		int randomSouthFactor;
		int i=0;
		while(i<this.sampleNumber) {
			randomSouthFactor=((int) (Math.random()*(cellnum-1)))+1;
			randomEastFactor=((int) (Math.random()*(cellnum-1)))+1; //integer number in [1, cellnum-1] in order to not exceed the grid coordinate values
			southSingleCell=north-(cellHeight*randomSouthFactor);
			northSingleCell=southSingleCell+cellHeight;
			eastSingleCell=west+(cellWidth*randomEastFactor);
			westSingleCell=eastSingleCell-cellWidth;
			singleCell=new BoundingBox(northSingleCell, southSingleCell, westSingleCell, eastSingleCell);
			gridStructure.add(singleCell);
			i++;
		}
	}
	
	//Add a record to the matrix structure
	public void addRecord(ArrayList<Double> record) {
		this.freqStructure.add(record);
	}
	
	//Update the hash map with new categories
	public void updateMap(ArrayList<String> categories) {
		for(String s: categories)
			if(!this.map.containsKey(s)) {
				this.map.put(s, this.map.size());
				this.features.add(s);
			}
	}
	
	//Build a record of the matrix structure
	public ArrayList<Double> fillRecord(ArrayList<Integer> occurrences, ArrayList<String> distincts, int cat_num, double area) {
		ArrayList<Double> record=new ArrayList<Double>();
		for(int i=0; i<this.map.size(); i++) {
			record.add(0.0);
		}
		for(int i=0;i<distincts.size();i++){
			int category_index=this.map.get(distincts.get(i)); //get the category corresponding to its occurrence value
			double occ= (double) occurrences.get(i);
			double num=(double) cat_num;
			record.set(category_index, (occ/num)/(area/1000)); //put the occurrence value (normalized with the corresponding cell area) in the "right" position
		}
		return record;
	}
	
	//Fix the record length to have columns with the same length value
	public void fixRecordsLength(int tot_num) {
		for(ArrayList<Double> record: this.freqStructure)
			for(int i=record.size();i<tot_num;i++) {
				record.add(0.0);
			}	
	}
	
	//Create a matrix with standard deviation values starting by the matrix of normalized frequencies
	public void createStdDevMatrix(ArrayList<ArrayList<Double>> freqMatrix) {
		ArrayList<Double> stdDevArray=new ArrayList<Double>();
		double mean=0; //mean of the element (of the same category) found
		double variance=0;
		double stdDev=0;
		
		//get all the standard deviation values
		for(int j=0; j<freqMatrix.get(0).size(); j++) {
			mean=getMean(freqMatrix, j);
			variance=getVariance(freqMatrix, j, mean);
			stdDev=getStdDev(variance);
			stdDevArray.add(stdDev);
		}
		
		//Build the matrix
		for(int i=0; i<freqMatrix.size(); i++) {
			ArrayList<Double> stdDevRecord=new ArrayList<Double>(stdDevArray);
			this.devStructure.add(stdDevRecord);
		}
	}
	
	//Calculate the mean of a given array of values
	public double getMean(ArrayList<ArrayList<Double>> freqMatrix, int index) {
		double sum=0; //sum of element (of the same category) found 
		double size=0; //number of element (of the same category) found
		for(int i=0; i<freqMatrix.size(); i++) {
			sum+=freqMatrix.get(i).get(index);
			size++;
		}
		return sum/size;
	}
	
	//Calculate the variance of a given array of values
	public double getVariance(ArrayList<ArrayList<Double>> freqMatrix, int index, double mean) {
		double value=0;
		double tmp=0;
		double size=0;
		for(int i=0; i<freqMatrix.size(); i++) {
			value=freqMatrix.get(i).get(index);
			tmp+=(mean-value)*(mean-value);
			size++;
		}
		return tmp/size;
	}
	
	//Calculate the standard deviation given a variance value
	public double getStdDev(double variance) {
		return Math.sqrt(variance);
	}
}
