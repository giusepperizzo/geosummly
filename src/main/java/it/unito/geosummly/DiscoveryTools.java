package it.unito.geosummly;

import java.util.ArrayList;

public class DiscoveryTools {
	
	public DiscoveryTools() {}
	
	/**Get an array with the mean for each category values (column values)*/
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
			meanArray.add(mean); //mean value per column
		}
		return meanArray;
	}
	
	/**Create a matrix with standard deviation values starting by the matrix of densities*/
	public ArrayList<ArrayList<Double>> getStdMatrix(ArrayList<ArrayList<Double>> densMatrix) {
		ArrayList<ArrayList<Double>> stdMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> stdDevArray=new ArrayList<Double>();
		double mean=0; //mean of the element (of the same category) found
		double variance=0;
		double stdDev=0;
		
		for(int j=0; j<densMatrix.get(0).size(); j++) {
			mean=getMean(densMatrix, j);
			variance=getVariance(densMatrix, j, mean);
			stdDev=getStdDev(variance);
			stdDevArray.add(stdDev);
		}
		
		//Build the matrix
		for(int i=0; i<densMatrix.size(); i++) {
			ArrayList<Double> stdDevRecord=new ArrayList<Double>(stdDevArray);
			stdMatrix.add(stdDevRecord);
		}
		return stdMatrix;
	}
	
	/**Get the mean of a given array of values*/
	public double getMean(ArrayList<ArrayList<Double>> matrix, int index) {
		double sum=0; //sum of element (of the same category) found 
		double size=0; //number of element (of the same category) found
		for(int i=0; i<matrix.size(); i++) {
			sum+=matrix.get(i).get(index);
			size++;
		}
		return sum/size;
	}
	
	/**Get the variance of a given array of values*/
	public double getVariance(ArrayList<ArrayList<Double>> densMatrix, int index, double mean) {
		double value=0;
		double tmp=0;
		double size=0;
		for(ArrayList<Double> row: densMatrix) {
			value=row.get(index);
			tmp+=(mean-value)*(mean-value);
			size++;
		}
		return tmp/size;
	}
	
	/**Get the standard deviation given a variance value*/
	public double getStdDev(double variance) {
		return Math.sqrt(variance);
	}
	
	/**Get single density values with E(d)-1.96*std/radix(N)*/
	public ArrayList<Double> getSingleDensities(ArrayList<Double> meanDens, ArrayList<Double> std, double n) {
		ArrayList<Double> singleDensities=new ArrayList<Double>();
		double mF=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size();i++) {
			mF=meanDens.get(i);
			sD=std.get(i);
			density=mF-(1.96* (sD/Math.sqrt(n)) );
			singleDensities.add(density);
		}
		return singleDensities;
	}
	
	/**Get density values of categories combinations with E-1.96*std/radix(N)
	* E=E(d_m, cat1) * E(d_m, cat2) * E(d_m, cat3) * ... * (E(d_m, catn))
	* std = radix( sum( (XiYiZi...Kn - E(d_m,cat1)E(d_m,cat2)E(d_m, cat3)...(E(d_m, catn) )^2 ) / N ), Xi, Yi, Zi, ..., Ki are the individual values of the cell of the categories
	* N = |observation_cat1|*|observation_cat2|*|observation_cat3|* ... *|observation_catn| */
	public ArrayList<Double> getCombinations(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> toRet, ArrayList<Double> meanDens, int[] comb, int startIndex, int combCount, double n) {
		double mult;
		double sum;
		double value;
		double sD;
		double density;
		//if i have a combination of size combCount
		if(combCount == comb.length){
			mult=1;
			sum=0;
			//get the values of the combination
			for(int i = 0; i < combCount; i++){
				mult*= meanDens.get(comb[i]);
			}
			//get the corresponding values of the matrix
			for(int i=0; i<matrix.size();i++) {
				value=1;
				for(int j=0; j<combCount; j++) {
					value*=matrix.get(i).get(comb[j]);
				}
				sum+=Math.pow((value-mult), 2); // sum iteratively the square of the difference between values of combination and matrix
			}
			//get the density values
			sD=Math.sqrt(sum/n);
			density=mult-(1.96 * (sD/Math.sqrt(n)));
			toRet.add(density);
		}
		//recursive call for the next combinations
		else{
			for(int i = startIndex; i < meanDens.size(); i++){
				comb[combCount] = i;
				getCombinations(matrix, toRet, meanDens, comb, i+1, combCount+1, n);
			}
		}
		return toRet;
	}
	
	/**Change the feature label by replacing 'old' with 'last'*/
	public ArrayList<String> changeFeaturesLabel(String old, String last, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			label=features.get(i).replace(old, last);
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
	
	/**Get the feature labeled either for frequency, density, standard deviation or singles' deltad*/
	public ArrayList<String> getFeaturesLabel(String s, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
	
	/**Get the feature labeled for combinations of deltad values*/
	public ArrayList<String> getFeaturesForCombinations(ArrayList<String> toRet, ArrayList<String> features, int[] comb, int startIndex, int combCount) {
		if(combCount == comb.length){
			String str="deltad(";
			for(int i = 0; i < combCount; i++){
				//control in order to remove the last AND
				if(i==combCount-1)
					str+=features.get(comb[i]);
				else
					str+=features.get(comb[i])+" AND ";
			}
			str+=")";
			toRet.add(str);
		}
		else{
			for(int i = startIndex; i < features.size(); i++){
				comb[combCount] = i;
				getFeaturesForCombinations(toRet, features, comb, i+1, combCount+1);
			}
		}
		return toRet;
	}
}