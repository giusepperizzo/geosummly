package it.unito.geosummly.discovery;

import java.util.ArrayList;

public class ClassOfArea {
	private String type;
	private ArrayList<ArrayList<Double>> bigAreaOcc;
	private ArrayList<ArrayList<Double>> bigAreaDens;
	
	
	public ClassOfArea(String type, ArrayList<ArrayList<Double>> bigAreaOcc, ArrayList<ArrayList<Double>> bigAreaDens, ArrayList<ArrayList<Double>> area_1_occ, ArrayList<ArrayList<Double>> area_2_occ, ArrayList<ArrayList<Double>> area_1_dens, ArrayList<ArrayList<Double>> area_2_dens) {
		this.type=type;
		this.bigAreaOcc=bigAreaOcc;
		this.bigAreaDens=bigAreaDens;
		for(ArrayList<Double> record: area_1_occ)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_2_occ)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_1_dens)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_2_dens)
			this.bigAreaOcc.add(record);
	}
	
	public ClassOfArea(String type, ArrayList<ArrayList<Double>> bigAreaOcc, ArrayList<ArrayList<Double>> bigAreaDens, ArrayList<ArrayList<Double>> area_1_occ, ArrayList<ArrayList<Double>> area_2_occ, ArrayList<ArrayList<Double>> area_3_occ, ArrayList<ArrayList<Double>> area_1_dens, ArrayList<ArrayList<Double>> area_2_dens, ArrayList<ArrayList<Double>> area_3_dens ) {
		this.type=type;
		this.bigAreaOcc=bigAreaOcc;
		this.bigAreaDens=bigAreaDens;
		for(ArrayList<Double> record: area_1_occ)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_2_occ)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_3_occ)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_1_dens)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_2_dens)
			this.bigAreaOcc.add(record);
		for(ArrayList<Double> record: area_3_dens)
			this.bigAreaOcc.add(record);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<ArrayList<Double>> getBigAreaOcc() {
		return bigAreaOcc;
	}

	public void setBigAreaOcc(ArrayList<ArrayList<Double>> bigAreaOcc) {
		this.bigAreaOcc = bigAreaOcc;
	}
	
	public ArrayList<ArrayList<Double>> getBigAreaDens() {
		return bigAreaDens;
	}

	public void setBigAreaDens(ArrayList<ArrayList<Double>> bigAreaDens) {
		this.bigAreaDens = bigAreaDens;
	}
	
	//get an array with the mean for each category
	public ArrayList<Double> getMeanArray(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<Double> meanArray=new ArrayList<Double>();
		double sum=0;
		double total=0;
		double mean=0;
		for(int i=0;i<matrix.get(0).size();i++) {
			sum=0;
			total=0;
			mean=0;
			for(int j=0;j<matrix.size();j++) {
				sum+=matrix.get(j).get(i);
				total++;
			}
			mean=sum/total;
			meanArray.add(mean); //mean densities per column
		}
		return meanArray;
	}
	
	//Create a matrix with standard deviation values starting by the matrix of densities
	public ArrayList<ArrayList<Double>> getStdMatrix(ArrayList<ArrayList<Double>> freqMatrix) {
		ArrayList<ArrayList<Double>> stdMatrix=new ArrayList<ArrayList<Double>>();
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
			stdMatrix.add(stdDevRecord);
		}
		return stdMatrix;
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
	
	//Get single density values with E(d)-1.96*std
	public ArrayList<Double> getSingleDensities(ArrayList<Double> meanDens, ArrayList<Double> std) {
		ArrayList<Double> singleDensities=new ArrayList<Double>();
		double mF=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size();i++) {
			mF=meanDens.get(i);
			sD=std.get(i);
			density=mF-(1.96*sD);
			singleDensities.add(density);
		}
		return singleDensities;
	}
	
	//Get pair density values with E-1.96*std/radix(N)
	//E=E(f_n, cat1)E(f_n, cat2)
	//std = radix( sum( (XiYi - E(f_n,cat1)E(f_n,cat2))^2 ) / N ), Xi and Yi are the individual values of the cell of the categories
	//N = |observation(cat1)||observation(cat2)|
	public ArrayList<Double> getPairDensities(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> meanFreq) {
		ArrayList<Double> pairDensities=new ArrayList<Double>();
		double sum=0;
		double n=matrix.size()*matrix.size();
		double mF_1=0;
		double mF_2=0;
		double value_1=0;
		double value_2=0;
		double difference=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanFreq.size()-1;i++) {
			for(int j=i+1;j<meanFreq.size();j++) {
				sum=0;
				mF_1=meanFreq.get(i);
				mF_2=meanFreq.get(j);
				for(int k=0;k<matrix.size();k++) {
					value_1=matrix.get(k).get(i);
					value_2=matrix.get(k).get(j);
					difference=(value_1*value_2)-(mF_1*mF_2);
					sum+=Math.pow(difference,2);
				}
				sD=Math.sqrt(sum/n);
				density=(mF_1*mF_2)-(1.96* (sD/Math.sqrt(n)) );
				pairDensities.add(density);
			}
		}
		return pairDensities;
	}
	
	public ArrayList<String> getFeaturesForPairs(ArrayList<String> features) {
		String combination="";
		ArrayList<String> pairFeatures=new ArrayList<String>();
		for(int i=0;i<features.size()-1;i++) {
			for(int j=i+1;j<features.size();j++) {
				combination="d("+features.get(i)+" AND "+features.get(j)+")";
				pairFeatures.add(combination);
			}
		}
		return pairFeatures;
	}
}
