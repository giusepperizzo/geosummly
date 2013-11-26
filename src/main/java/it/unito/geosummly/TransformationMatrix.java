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
	private ArrayList<ArrayList<Double>> not_normalized_matrix; //data structure before normalization
	private ArrayList<ArrayList<Double>> normalized_matrix; //data structure after normalization
	private HashMap<String, Integer> map; //Map category to index
	private ArrayList<String> header; //Sorted list of categories by column index 
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationMatrix(){}
	
	
	public ArrayList<ArrayList<Double>> getNotNormalizedMatrix() {
		return not_normalized_matrix;
	}

	public void setNotNormalizedMatrix(ArrayList<ArrayList<Double>> not_normalized_matrix) {
		this.not_normalized_matrix = not_normalized_matrix;
	}


	public ArrayList<ArrayList<Double>> getNormalizedMatrix() {
		return normalized_matrix;
	}
	
	public void setNormalizedMatrix(ArrayList<ArrayList<Double>> normalized_matrix) {
		this.normalized_matrix = normalized_matrix;
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
	
	public String toString() {
		String s= "Matrix Rows: "+not_normalized_matrix.size()+"\nMatrix Columns:"+not_normalized_matrix.get(0).size()+"\nAll categories occurrences:";
		for(ArrayList<Double> r : not_normalized_matrix) {
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
	public ArrayList<Double> fillRow(ArrayList<Integer> occurrences, ArrayList<String> distincts, double lat, double lng, double area) {
		ArrayList<Double> row=new ArrayList<Double>();
		row.add(lat); //lat, lng and area are in position 0 and 1
		row.add(lng);
		for(int i=0; i<this.map.size(); i++) {
			row.add(0.0);
		}
		for(int i=0;i<distincts.size();i++){
			int category_index=this.map.get(distincts.get(i)); //get the category corresponding to its occurrence value
			double occ= (double) occurrences.get(i);
			row.set(category_index, occ/(area/1000)); //put the density value in the "right" position    
		}
		return row;
	}
	
	//Fix the row length to have rows with the same length
	public void fixRowsLength(int tot_num, ArrayList<ArrayList<Double>> matrix) {
		for(ArrayList<Double> row: matrix)
			for(int i=row.size();i<tot_num;i++) {
				row.add(0.0);
			}	
	}
	
	public void buildNotNormalizedMatrix(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		double currentValue=0;
		double norm_density=0;
		
		//get all the sums of the features occurrences per column (it starts by 2 because first 2 cells are for lat and lng)
		for(int j=2; j<matrix.get(0).size(); j++) {
			sum=getSum(matrix, j);
			sumArray.add(sum);
		}
		
		//create the matrix
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> normalizedRecord=new ArrayList<Double>();
			normalizedRecord.add(matrix.get(i).get(0));
			normalizedRecord.add(matrix.get(i).get(1));
			for(int j=2;j<matrix.get(i).size();j++) {
				currentValue=matrix.get(i).get(j);
				norm_density=(currentValue/sumArray.get(j-2));
				normalizedRecord.add(norm_density);
			}
			this.not_normalized_matrix.add(normalizedRecord);
		}
	}
	
	//Build a transformation matrix with normalized values in [0,1]
	public void buildNormalizedMatrix(ArrayList<ArrayList<Double>> matrix) {
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
			this.normalized_matrix.add(normalizedRecord);
		}
	}
	
	//Get the total number of elements of a specific category
	public double getSum(ArrayList<ArrayList<Double>> array, int index) {
		double sum=0;
		for(int i=0; i<array.size(); i++) {
			sum+=array.get(i).get(index);
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
}
