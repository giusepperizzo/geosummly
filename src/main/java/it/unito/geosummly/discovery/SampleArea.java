package it.unito.geosummly.discovery;

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
	private ArrayList<ArrayList<Double>> densityStructure; //matrix of densities
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
	
	public ArrayList<ArrayList<Double>> getDensityStructure() {
		return densityStructure;
	}

	public void setDensityStructure(ArrayList<ArrayList<Double>> densityStructure) {
		this.densityStructure = densityStructure;
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
	
	//Update the hash map with new categories
	public void updateMap(ArrayList<String> categories) {
		for(String s: categories)
			if(!this.map.containsKey(s)) {
				this.map.put(s, this.map.size());
			}
	}
	
	//Build a record of the densityStructure
	public ArrayList<Double> fillRecord(ArrayList<Integer> occurrences, ArrayList<String> distincts, double area) {
		ArrayList<Double> record=new ArrayList<Double>();
		for(int i=0; i<this.map.size(); i++) {
			record.add(0.0);
		}
		for(int i=0;i<distincts.size();i++){
			int category_index=this.map.get(distincts.get(i)); //get the category corresponding to its occurrence value
			double occ= (double) occurrences.get(i);
			record.set(category_index, occ/(area/1000)); //put the density value in the "right" position
		}
		return record;
	}
	
	//Get records with density values (density=frequency/area) with intra-feature normalization
	public ArrayList<ArrayList<Double>> getNotNormalizedDensities(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<ArrayList<Double>> tmpStructure=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		double currentValue=0;
		double norm_density=0;
		
		//Get all the sums of the features occurrences per column
		for(int j=0; j<matrix.size(); j++) {
			sum=getSum(matrix, j);
			sumArray.add(sum);
		}
		
		//Build records
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> normalizedRecord=new ArrayList<Double>();
			for(int j=0;j<matrix.get(i).size();j++) {
				currentValue=matrix.get(i).get(j);
				norm_density=(currentValue/sumArray.get(j)); //density=frequency/area
				normalizedRecord.add(norm_density);
			}
			tmpStructure.add(normalizedRecord);
		}
		return tmpStructure;
	}
	
	//Density values normalized in [0,1]
	public void getNormalizedDensities(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<Double> minArray=new ArrayList<Double>();
		ArrayList<Double> maxArray=new ArrayList<Double>();
		double currentValue=0;
		double normalizedValue=0;
		
		//get all the min and max values of the columns
		for(int j=0; j<matrix.get(0).size(); j++) {
			double[] minmax=getMinMax(matrix, j);
			minArray.add(minmax[0]); //min value of column j
			maxArray.add(minmax[1]); //max value of column j
		}
		
		//create the matrix
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> normalizedRecord=new ArrayList<Double>();
			for(int j=0;j<matrix.get(i).size();j++) {
				currentValue=matrix.get(i).get(j);
				normalizedValue=normalizeValues(minArray.get(j), maxArray.get(j), currentValue);
				normalizedRecord.add(normalizedValue);
			}
			this.densityStructure.add(normalizedRecord);
		}
	}
	
	//Get the total number of elements of a specific category
	public double getSum(ArrayList<ArrayList<Double>> matrix, int index) {
		double sum=0;
		for(int i=0; i<matrix.size(); i++) {
			sum+=matrix.get(i).get(index);
		}
		return sum;
	}
	
	//Get the min and max values of a coordinate
	public double[] getMinMax(ArrayList<ArrayList<Double>> array, int index){
		double min=Double.MAX_VALUE;
		double max=-1*Double.MAX_VALUE;
		double current=0;
		double[] minmax=new double[2];
		
		for(int i=0; i<array.size(); i++) {
			current=array.get(i).get(index);
			if(current<min)
				min=current;
			if(current>max)
				max=current;
		}
		minmax[0]=min;
		minmax[1]=max;
		return minmax;
	}
	
	//Normalize values in [0,1]
	public double normalizeValues(double min, double max, double c) {
		double norm_c=(c-min)/(max-min);
		return norm_c;
	}
	
	//Create a matrix with standard deviation values starting by the matrix of densities
	public void createStdDevMatrix(ArrayList<ArrayList<Double>> densMatrix) {
		ArrayList<Double> stdDevArray=new ArrayList<Double>();
		double mean=0; //mean of the element (of the same category) found
		double variance=0;
		double stdDev=0;
		
		//get all the standard deviation values
		for(int j=0; j<densMatrix.get(0).size(); j++) {
			mean=getMean(densMatrix, j);
			variance=getVariance(densMatrix, j, mean);
			stdDev=getStdDev(variance);
			stdDevArray.add(stdDev);
		}
		
		//Build the matrix
		for(int i=0; i<densMatrix.size(); i++) {
			ArrayList<Double> stdDevRecord=new ArrayList<Double>(stdDevArray);
			this.devStructure.add(stdDevRecord);
		}
	}
	
	//Calculate the mean of a given array of values
	public double getMean(ArrayList<ArrayList<Double>> densMatrix, int index) {
		double sum=0; //sum of element (of the same category) found 
		double size=0; //number of element (of the same category) found
		for(int i=0; i<densMatrix.size(); i++) {
			sum+=densMatrix.get(i).get(index);
			size++;
		}
		return sum/size;
	}
	
	//Calculate the variance of a given array of values
	public double getVariance(ArrayList<ArrayList<Double>> densMatrix, int index, double mean) {
		double value=0;
		double tmp=0;
		double size=0;
		for(int i=0; i<densMatrix.size(); i++) {
			value=densMatrix.get(i).get(index);
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
