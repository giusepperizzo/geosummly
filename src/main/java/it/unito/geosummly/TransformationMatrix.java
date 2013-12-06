package it.unito.geosummly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * M*N Transformation matrix creation.
 * M is the number of bounding box cell.
 * N is the total number of categories found in the bounding box.
 * 
 * In the not-normalized matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the latitude and longitude values for the (M_i)th cell.
 * The cell C_ij, 0<i<M-1 and 2<j<N-1, let OCC_j be the occurrence of the (N_j)th category for the (M_i)th cell and
 * let TOT_j be the total number of categories found in the (N_j)th column. So, C_ij will contain a frequency value given by
 * (OCC_j/TOT_j). This means that all these values are intra-feature normalized.
 * 
 * In the normalized-matrix: 
 * The cell C_ij, 0<i<M-1 and 0<j<2, will contain the normalized latitude and longitude values in [0,1] for the (M_i)th cell.
 * For a cell C_ij, 0<i<M-1 and 2<j<N-1, let FREQ_j be the intra-feature normalized frequency of the (N_j)th category
 * for the (M_i)th cell and let AREA_i be the area value for M_i. So, C_ij will contain a density value given by (FREQ_J)/(AREA_i),
 * normalized in [0,1].
 *   
 */
public class TransformationMatrix {
	private ArrayList<ArrayList<Double>> frequencyMatrix; //data structure with frequencies
	private ArrayList<ArrayList<Double>> densityMatrix; //data structure with densities
	private ArrayList<ArrayList<Double>> normalizedMatrix; //data structure with densities after normalization
	private ArrayList<String> header;
	
	private HashMap<String, Integer> map; //Map category to index
	
	public static Logger logger = Logger.getLogger(TransformationMatrix.class.toString());
	
	public TransformationMatrix(){}
	
	
	public ArrayList<ArrayList<Double>> getFrequencyMatrix() {
		return frequencyMatrix;
	}

	public void setFrequencyMatrix(ArrayList<ArrayList<Double>> frequencyMatrix) {
		this.frequencyMatrix = frequencyMatrix;
	}


	public ArrayList<ArrayList<Double>> getDensityMatrix() {
		return densityMatrix;
	}

	public void setDensityMatrix(ArrayList<ArrayList<Double>> densityMatrix) {
		this.densityMatrix = densityMatrix;
	}
	
	public ArrayList<ArrayList<Double>> getNormalizedMatrix() {
		return normalizedMatrix;
	}

	public void setNormalizedMatrix(ArrayList<ArrayList<Double>> normalizedMatrix) {
		this.normalizedMatrix = normalizedMatrix;
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
		this.header = header;
	}
	
	//Update the hash map with new categories
	public void updateMap(ArrayList<String> categories) {
		for(String s: categories)
			if(!this.map.containsKey(s)) {
				this.map.put(s, this.map.size()+2); //first value in the map has to be 2
			}
	}
	
	//Build a row of the matrix
	public ArrayList<Double> fillRow(ArrayList<Integer> occurrences, ArrayList<String> distincts, double lat, double lng) {
		ArrayList<Double> row=new ArrayList<Double>();
		row.add(lat); //lat, lng and area are in position 0 and 1
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
	public void fixRowsLength(int tot_num, ArrayList<ArrayList<Double>> matrix) {
		for(ArrayList<Double> row: matrix)
			for(int i=row.size();i<tot_num;i++) {
				row.add(0.0);
			}	
	}
	
	public ArrayList<String> sortFeatures(HashMap<String,Integer> map) {
		ArrayList<String> sortedFeatures=new ArrayList<String>();
		Object[] keys= map.keySet().toArray();
		Arrays.sort(keys);
		sortedFeatures.add("Latitude");
		sortedFeatures.add("Longitude");
		for(int i=0;i<keys.length;i++)
			sortedFeatures.add((String) keys[i]);
		return sortedFeatures;
	}
	
	//Sort matrix alphabetically (for features)
	public ArrayList<ArrayList<Double>> sortMatrix(ArrayList<ArrayList<Double>> matrix, HashMap<String,Integer> map) {
		String feature="";
		int value=0;
		ArrayList<ArrayList<Double>> sortedMatrix=new ArrayList<ArrayList<Double>>();
		Object[] keys= map.keySet().toArray();
		Arrays.sort(keys);
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> sortedRecord=new ArrayList<>();
			sortedRecord.add(matrix.get(i).get(0));
			sortedRecord.add(matrix.get(i).get(1));
			for(int j=0;j<keys.length;j++) {
				feature=(String)keys[j];
				value=map.get(feature);
				sortedRecord.add(matrix.get(i).get(value));
			}
			sortedMatrix.add(sortedRecord);
		}
		return sortedMatrix;
	}
	
	//Matrix with densities
	public ArrayList<ArrayList<Double>> buildDensityMatrix(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> area) {
		ArrayList<ArrayList<Double>> densMatrix=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> densRecord=new ArrayList<Double>();
			densRecord.add(matrix.get(i).get(0)); //latitude
			densRecord.add(matrix.get(i).get(1)); //longitude
			for(int j=2;j<matrix.get(i).size();j++) {
				densRecord.add(matrix.get(i).get(j)/area.get(i));
			}
			densMatrix.add(densRecord);
		}
		return densMatrix;
	}
	
	//Matrix normalized in [0,1] for lat/lng and intra-feature for densities
	public ArrayList<ArrayList<Double>> buildNormalizedMatrix(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<ArrayList<Double>> normalizedMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		ArrayList<Double> minArray=new ArrayList<Double>();
		ArrayList<Double> maxArray=new ArrayList<Double>();
		double currentValue=0;
		double normalizedValue=0;
		
		//get all the min and max values of the columns for latitude and longitude
		for(int j=0; j<2; j++) {
			double[] minmax=getMinMax(matrix, j);
			minArray.add(minmax[0]); //min value of column j
			maxArray.add(minmax[1]); //max value of column j
		}
		
		//get all the sums of the features densities per column (it starts by 2 because first 2 cells are for lat and lng)
		for(int j=2; j<matrix.get(0).size(); j++) {
			sum=getSum(matrix, j);
			sumArray.add(sum);
		}
		
		//create the matrix
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> normalizedRecord=new ArrayList<Double>();
			for(int j=0;j<matrix.get(i).size();j++) {
				currentValue=matrix.get(i).get(j); //get the value
				if(j==0 || j==1) {
					normalizedValue=normalizeValues(minArray.get(j), maxArray.get(j), currentValue);
					normalizedRecord.add(normalizedValue);
				}
				else {
					if(sumArray.get(j-2)>0)
						normalizedValue=(currentValue/sumArray.get(j-2)); //intra-feature density
					else
						normalizedValue=0.0;
					normalizedRecord.add(normalizedValue);
				}
			}
			normalizedMatrix.add(normalizedRecord);
		}
		return normalizedMatrix;
	}
	
	//Get the total number of elements of a specific category
	public double getSum(ArrayList<ArrayList<Double>> array, int index) {
		double sum=0;
		for(int i=0; i<array.size(); i++) {
			sum+=array.get(i).get(index);
		}
		return sum;
	}
	
	//Get the min and max value of a column
	public double[] getMinMax(ArrayList<ArrayList<Double>> array, int index){
		double min=Double.MAX_VALUE;
		double max=-1*Double.MAX_VALUE;
		double current=0;
		double[] minmax=new double[2];
		
		for(int i=0; i<array.size(); i++) {
			current=array.get(i).get(index); //get the value
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
	
	//get the feature labeled either for frequency, density or normalized density
	public ArrayList<String> getFeaturesLabel(String s, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		featuresLabel.add(features.get(0)); //Latitude
		featuresLabel.add(features.get(1)); //Longitude
		for(int i=2;i<features.size();i++) {
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
}
