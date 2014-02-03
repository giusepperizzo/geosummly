package it.unito.geosummly.discovery;

import java.util.ArrayList;

public class DiscoveryTools {
	
	public DiscoveryTools() {}
	
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
			meanArray.add(mean); //mean value per column
		}
		return meanArray;
	}
	
	//Create a matrix with standard deviation values starting by the matrix of densities
	public ArrayList<ArrayList<Double>> getStdMatrix(ArrayList<ArrayList<Double>> densMatrix) {
		ArrayList<ArrayList<Double>> stdMatrix=new ArrayList<ArrayList<Double>>();
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
			stdMatrix.add(stdDevRecord);
		}
		return stdMatrix;
	}
	
	//Calculate the mean of a given array of values
	public double getMean(ArrayList<ArrayList<Double>> matrix, int index) {
		double sum=0; //sum of element (of the same category) found 
		double size=0; //number of element (of the same category) found
		for(int i=0; i<matrix.size(); i++) {
			sum+=matrix.get(i).get(index);
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
	
	//Get single density values with E(d)-1.96*std/radix(N)
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
	
	//Get pair density values with E-1.96*std/radix(N)
	//E=E(d_m, cat1)E(d_m, cat2)
	//std = radix( sum( (XiYi - E(d_m,cat1)E(d_m,cat2))^2 ) / N ), Xi and Yi are the individual values of the cell of the categories
	//N = |observation|
	public ArrayList<Double> getPairDensities(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> meanDens, double n) {
		ArrayList<Double> pairDensities=new ArrayList<Double>();
		double sum=0;
		double mF_1=0;
		double mF_2=0;
		double value_1=0;
		double value_2=0;
		double difference=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size()-1;i++) {
			for(int j=i+1;j<meanDens.size();j++) {
				sum=0;
				mF_1=meanDens.get(i);
				mF_2=meanDens.get(j);
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
	
	//Get triple density values with E-1.96*std/radix(N)
	//E=E(d_m, cat1)E(d_m, cat2)E(d_m, cat3)
	//std = radix( sum( (XiYiZi - E(d_m,cat1)E(d_m,cat2)E(d_m, cat3) )^2 ) / N ), Xi, Yi and Zi are the individual values of the cell of the categories
	//N = |observation_cat1|*|observation_cat2|*|observation_cat3|
	public ArrayList<Double> getTripleDensities(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> meanDens, double n) {
		ArrayList<Double> tripleDensities=new ArrayList<Double>();
		double sum=0;
		double mF_1=0;
		double mF_2=0;
		double mF_3=0;
		double value_1=0;
		double value_2=0;
		double value_3=0;
		double difference=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size()-2;i++) {
			for(int j=i+1;j<meanDens.size()-1;j++) {
				for(int k=j+1;k<meanDens.size();k++) {
					sum=0;
					mF_1=meanDens.get(i);
					mF_2=meanDens.get(j);
					mF_3=meanDens.get(k);
					for(int z=0;z<matrix.size();z++) {
						value_1=matrix.get(z).get(i);
						value_2=matrix.get(z).get(j);
						value_3=matrix.get(z).get(k);
						difference=(value_1*value_2*value_3)-(mF_1*mF_2*mF_3);
						sum+=Math.pow(difference,2);
					}
					sD=Math.sqrt(sum/n);
					density=(mF_1*mF_2*mF_3)-(1.96* (sD/Math.sqrt(n)) );
					tripleDensities.add(density);
				}
			}
		}
		return tripleDensities;
	}
	
	//Get quadruple density values with E-1.96*std/radix(N)
	//E=E(d_m, cat1)E(d_m, cat2)E(d_m, cat3)E(d_m, cat4)
	//std = radix( sum( (XiYiZiWi - E(d_m,cat1)E(d_m,cat2)E(d_m, cat3)E(d_m, cat4) )^2 ) / N ), Xi, Yi, Zi and Wi are the individual values of the cell of the categories
	//N = |observation|
	public ArrayList<Double> getQuadrupleDensities(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> meanDens, double n) {
		ArrayList<Double> quadrupleDensities=new ArrayList<Double>();
		double sum=0;
		double mF_1=0;
		double mF_2=0;
		double mF_3=0;
		double mF_4=0;
		double value_1=0;
		double value_2=0;
		double value_3=0;
		double value_4=0;
		double difference=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size()-3;i++) {
			for(int j=i+1;j<meanDens.size()-2;j++) {
				for(int k=j+1;k<meanDens.size()-1;k++) {
					for(int l=k+1;l<meanDens.size();l++) {
						sum=0;
						mF_1=meanDens.get(i);
						mF_2=meanDens.get(j);
						mF_3=meanDens.get(k);
						mF_4=meanDens.get(l);
						for(int z=0;z<matrix.size();z++) {
							value_1=matrix.get(z).get(i);
							value_2=matrix.get(z).get(j);
							value_3=matrix.get(z).get(k);
							value_4=matrix.get(z).get(l);
							difference=(value_1*value_2*value_3*value_4)-(mF_1*mF_2*mF_3*mF_4);
							sum+=Math.pow(difference,2);
						}
						sD=Math.sqrt(sum/n);
						density=(mF_1*mF_2*mF_3*mF_4)-(1.96* (sD/Math.sqrt(n)) );
						quadrupleDensities.add(density);
					}
				}
			}
		}
		return quadrupleDensities;
	}
	
	//Get quintuple density values with E-1.96*std/radix(N)
	//E=E(d_m, cat1)E(d_m, cat2)E(d_m, cat3)E(d_m, cat4)E(d_m, cat5)
	//std = radix( sum( (XiYiZiWiKi - E(d_m,cat1)E(d_m,cat2)E(d_m, cat3)E(d_m, cat4)E(d_m, cat5) )^2 ) / N ), Xi, Yi, Zi, Wi and Ki are the individual values of the cell of the categories
	//N = |observation|
	public ArrayList<Double> getQuintupleDensities(ArrayList<ArrayList<Double>> matrix, ArrayList<Double> meanDens, double n) {
		ArrayList<Double> quintupleDensities=new ArrayList<Double>();
		double sum=0;
		double mF_1=0;
		double mF_2=0;
		double mF_3=0;
		double mF_4=0;
		double mF_5=0;
		double value_1=0;
		double value_2=0;
		double value_3=0;
		double value_4=0;
		double value_5=0;
		double difference=0;
		double sD=0;
		double density=0;
		for(int i=0;i<meanDens.size()-4;i++) {
			for(int j=i+1;j<meanDens.size()-3;j++) {
				for(int k=j+1;k<meanDens.size()-2;k++) {
					for(int l=k+1;l<meanDens.size()-1;l++) {
						for(int m=l+1;m<meanDens.size();m++) {
							sum=0;
							mF_1=meanDens.get(i);
							mF_2=meanDens.get(j);
							mF_3=meanDens.get(k);
							mF_4=meanDens.get(l);
							mF_5=meanDens.get(m);
							for(int z=0;z<matrix.size();z++) {
								value_1=matrix.get(z).get(i);
								value_2=matrix.get(z).get(j);
								value_3=matrix.get(z).get(k);
								value_4=matrix.get(z).get(l);
								value_5=matrix.get(z).get(m);
								difference=(value_1*value_2*value_3*value_4*value_5)-(mF_1*mF_2*mF_3*mF_4*mF_5);
								sum+=Math.pow(difference,2);
							}
							sD=Math.sqrt(sum/n);
							density=(mF_1*mF_2*mF_3*mF_4*mF_5)-(1.96* (sD/Math.sqrt(n)) );
							quintupleDensities.add(density);
						}
					}
				}
			}
		}
		return quintupleDensities;
	}
	
	//get the feature labeled either for frequency, density, standard deviation or singles' deltad
	public ArrayList<String> getFeaturesLabel(String s, ArrayList<String> features) {
		String label="";
		ArrayList<String> featuresLabel=new ArrayList<String>();
		for(int i=0;i<features.size();i++) {
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		return featuresLabel;
	}
	
	//get the feature labeled for pairs' deltad
	public ArrayList<String> getFeaturesForPairs(ArrayList<String> features) {
		String combination="";
		ArrayList<String> pairFeatures=new ArrayList<String>();
		for(int i=0;i<features.size()-1;i++) {
			for(int j=i+1;j<features.size();j++) {
				combination="deltad("+features.get(i)+" AND "+features.get(j)+")";
				pairFeatures.add(combination);
			}
		}
		return pairFeatures;
	}
	
	//get the feature labeled for triples' deltad
	public ArrayList<String> getFeaturesForTriples(ArrayList<String> features) {
		String combination="";
		ArrayList<String> tripleFeatures=new ArrayList<String>();
		for(int i=0;i<features.size()-2;i++) {
			for(int j=i+1;j<features.size()-1;j++) {
				for(int k=j+1;k<features.size();k++) {
					combination="deltad("+features.get(i)+" AND "+features.get(j)+" AND "+features.get(k)+")";
					tripleFeatures.add(combination);
				}
			}
		}
		return tripleFeatures;
	}
	
	//get the feature labeled for quadruples' deltad
	public ArrayList<String> getFeaturesForQuadruples(ArrayList<String> features) {
		String combination="";
		ArrayList<String> quadrupleFeatures=new ArrayList<String>();
		for(int i=0;i<features.size()-3;i++) {
			for(int j=i+1;j<features.size()-2;j++) {
				for(int k=j+1;k<features.size()-1;k++) {
					for(int l=k+1;l<features.size();l++) {
						combination="deltad("+features.get(i)+" AND "+features.get(j)+" AND "+features.get(k)+" AND "+features.get(l)+")";
						quadrupleFeatures.add(combination);
					}
				}
			}
		}
		return quadrupleFeatures;
	}
	
	//get the feature labeled for quadruples' deltad
	public ArrayList<String> getFeaturesForQuintuples(ArrayList<String> features) {
		String combination="";
		ArrayList<String> quintupleFeatures=new ArrayList<String>();
		for(int i=0;i<features.size()-4;i++) {
			for(int j=i+1;j<features.size()-3;j++) {
				for(int k=j+1;k<features.size()-2;k++) {
					for(int l=k+1;l<features.size()-1;l++) {
						for(int m=l+1;m<features.size();m++) {
							combination="deltad("+features.get(i)+" AND "+features.get(j)+" AND "+features.get(k)+" AND "+features.get(l)+" AND "+features.get(m)+")";
							quintupleFeatures.add(combination);
						}
					}
				}
			}
		}
		return quintupleFeatures;
	}
}
