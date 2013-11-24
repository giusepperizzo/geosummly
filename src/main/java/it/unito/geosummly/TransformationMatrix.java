package it.unito.geosummly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * M*N Transformation matrix creation.
 * M is the number of bounding box cell.
 * N is the total number of categories found in the bounding box.
 * 
 * In the original matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the latitude and longitude values for the (M_i)th cell.
 * The cell C_ij, 0<i<M-1 and 2<j<N-1, will contain the occurrence of the (N_j)th category for the (M_i)th cell.
 * 
 * In the normalized matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the normalized latitude and longitude values for the (M_i)th cell.
 * For a cell C_ij, 0<i<M-1 and 2<j<N-1, let OCC_j be the occurrence of the (N_j)th category for the (M_i)th cell, let AREA_i be 
 * the area value for M_i and let TOT_j be the total number of categories found in the (N_j)th column. So, C_ij will contain a density
 * value given by (OCC_j/TOT_j)/(AREA_i/1000).
 *   
 */
public class TransformationMatrix {
	private ArrayList<ArrayList<Double>> original_matrix; //data structure before normalization
	private ArrayList<ArrayList<Double>> matrix; //data structure after normalization
	private HashMap<String, Integer> map; //Map category to index
	private ArrayList<String> header; //Sorted list of categories by column index 
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationMatrix(){}
	
	
	public ArrayList<ArrayList<Double>> getOriginalMatrix() {
		return original_matrix;
	}

	public void setOriginalMatrix(ArrayList<ArrayList<Double>> original_matrix) {
		this.original_matrix = original_matrix;
	}


	public ArrayList<ArrayList<Double>> getMatrix() {
		return matrix;
	}
	
	public void setMatrix(ArrayList<ArrayList<Double>> matrix) {
		this.matrix = matrix;
	}
	
	public HashMap<String, Integer> getMap() {
		return map;
	}
	
	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}
	
	public ArrayList<String> getHeader() {
		return header;
	}

	public void setHeader(ArrayList<String> header) {
		header.add("Latitude"); //First two values (columns) of the header have to be lat and lng
		header.add("Longitude");
		this.header = header;
	}

	public void addRow(ArrayList<Double> row) {
		this.original_matrix.add(row);
	}
	
	public String toString() {
		String s= "Matrix Rows: "+matrix.size()+"\nMatrix Columns:"+matrix.get(0).size()+"\nAll categories occurrences:";
		for(ArrayList<Double> r : matrix) {
			for(Double d: r)
				s+=d+", ";
			s+="\n";
		}
		return s;
	}
	
	//Update the hash map with new categories
	public void updateMap(ArrayList<String> categories) {
		for(String s: categories)
			if(!this.map.containsKey(s)) {
				this.map.put(s, this.map.size()+2); //first value in the map has to be 2
				this.header.add(s);
			}
	}
	
	//Build a row of the matrix
	public ArrayList<Double> fillRow(ArrayList<Integer> occurrences, ArrayList<String> distincts, double lat, double lng) {
		ArrayList<Double> row=new ArrayList<Double>();
		row.add(lat); //lat, lng and area are in position 0, 1 and 2
		row.add(lng);
		for(int i=0; i<this.map.size(); i++) {
			row.add(0.0);
		}
		for(int i=0;i<distincts.size();i++){
			int category_index=this.map.get(distincts.get(i)); //get the category corresponding to its occurrence value
			double occ= (double) occurrences.get(i);
			row.set(category_index, occ); //put the occurrence value in the "right" position    
		}
		return row;
	}
	
	//Fix the row length to have rows with the same length
	public void fixRowsLength(int tot_num) {
		for(ArrayList<Double> row: this.original_matrix)
			for(int i=row.size();i<tot_num;i++) {
				row.add(0.0);
			}	
	}
	
	//Build a transformation matrix with normalized coordinates and density values
	public void buildMatrix(ArrayList<ArrayList<Double>> original, ArrayList<Double> area) {
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		double currentValue=0;
		double density=0;
		double normalizedLat=0;
		double normalizedLng=0;
		
		//get min and max values of latitude and longitude
		double[] minmaxLat=getMinMax(original, 0);
		double[] minmaxLng=getMinMax(original, 1);
		double minLat=minmaxLat[0];
		double maxLat=minmaxLat[1];
		double minLng=minmaxLng[0];
		double maxLng=minmaxLng[1];
		
		//get all the sums of the features occurrences per column (it starts by 2 because first 2 cells are for lat and lng)
		for(int j=2; j<original.get(0).size(); j++) {
			sum=getSum(original, j);
			sumArray.add(sum);
		}
		
		//create the matrix
		for(int i=0;i<original.size();i++) {
			ArrayList<Double> normalizedRecord=new ArrayList<Double>();
			normalizedLat=normalizeCoordinate(minLat, maxLat, original.get(i).get(0));
			normalizedLng=normalizeCoordinate(minLng, maxLng, original.get(i).get(1));
			normalizedRecord.add(normalizedLat);
			normalizedRecord.add(normalizedLng);
			for(int j=2;j<original.get(i).size();j++) {
				currentValue=original.get(i).get(j);
				density=(currentValue/sumArray.get(j-2)) / (area.get(i)/1000); //density=frequency/area
				normalizedRecord.add(density);
			}
			this.matrix.add(normalizedRecord);
		}
	}
	
	//Get the total number of elements of a specific category
	public double getSum(ArrayList<ArrayList<Double>> original, int index) {
		double sum=0;
		for(int i=0; i<original.size(); i++) {
			sum+=original.get(i).get(index);
		}
		return sum;
	}
	
	//Get the min and max values of a coordinate
	public double[] getMinMax(ArrayList<ArrayList<Double>> original, int index){
		double min=Double.MAX_VALUE;
		double max=-1*Double.MAX_VALUE;
		double current=0;
		double[] minmax=new double[2];
		
		for(int i=0; i<original.size(); i++) {
			current=original.get(i).get(index);
			if(current<min)
				min=current;
			if(current>max)
				max=current;
		}
		minmax[0]=min;
		minmax[1]=max;
		return minmax;
	}
	
	//Normalize coordinate value in range [min,max]
	public double normalizeCoordinate(double min, double max, double c) {
		double norm_c=(c-min)/(max-min);
		return norm_c;
	}
}
