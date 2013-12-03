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
	private ArrayList<ArrayList<Double>> occurrenceStructure; //matrix of occurrences
	private ArrayList<ArrayList<Double>> densityStructure; //matrix of densities
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
	
	public ArrayList<ArrayList<Double>> getOccurrenceStructure() {
		return occurrenceStructure;
	}
	
	public void setOccurrenceStructure(ArrayList<ArrayList<Double>> occurrenceStructure) {
		this.occurrenceStructure = occurrenceStructure;
	}
	
	public ArrayList<ArrayList<Double>> getDensityStructure() {
		return densityStructure;
	}

	public void setDensityStructure(ArrayList<ArrayList<Double>> freqStructure) {
		this.densityStructure = freqStructure;
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
	public void fillRecord(ArrayList<Integer> occurrences, ArrayList<String> distincts, double area) {
		ArrayList<Double> record_occ=new ArrayList<Double>();
		ArrayList<Double> record=new ArrayList<Double>();
		for(int i=0; i<this.map.size(); i++) {
			record_occ.add(0.0);
			record.add(0.0);
		}
		for(int i=0;i<distincts.size();i++){
			int category_index=this.map.get(distincts.get(i)); //get the category corresponding to its occurrence value
			double occ= (double) occurrences.get(i);
			record_occ.set(category_index, occ);
			record.set(category_index, occ/area); //put the density value in the "right" position
		}
		this.occurrenceStructure.add(record_occ);
		this.densityStructure.add(record);
	}
	
	//Get the total number of elements of a specific category
	public double getSum(ArrayList<ArrayList<Double>> matrix, int index) {
		double sum=0;
		for(int i=0; i<matrix.size(); i++) {
			sum+=matrix.get(i).get(index);
		}
		return sum;
	}
}
