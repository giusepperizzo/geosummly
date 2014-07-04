package it.unito.geosummly.tools;

import it.unito.geosummly.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class ImportTools {
	
	/**Get a list with all elements equal to zero*/
	public ArrayList<Double> buildListZero(int size) {
		
		ArrayList<Double> toRet=new ArrayList<Double>();
		int i=0;
		
		while(i<size) {
			toRet.add(0.0);
			i++;
		}
		return toRet;
	}
	
	/**
	 * Get all the areas of the grid cells 
	*/
	public ArrayList<Double> getAreas(ArrayList<BoundingBox> data) {
		
		ArrayList<Double> areas=new ArrayList<Double>();

    	for(BoundingBox b: data)
    		areas.add(b.getArea().doubleValue());
		
    	return areas;
	}
	
	/**
	 * Get all the areas of the grid cells by considering only the focal points
	*/
	public ArrayList<Double> getAreasFromFocalPoints(ArrayList<BoundingBox> data, 
													 int size) {
		
		ArrayList<Double> areas=new ArrayList<Double>();
		double edgeValue = getDistance(data.get(0).getCenterLat().doubleValue(), 
									   data.get(0).getCenterLng().doubleValue(), 
									   data.get(1).getCenterLat().doubleValue(), 
									   data.get(1).getCenterLng().doubleValue());
		double areaValue=Math.pow(edgeValue, 2);
		
		for(int i=0; i<size; i++)
			areas.add(areaValue);
		
		return areas;
	}
	
	/**Get (as bounding boxes) all the distinct focal coordinates of singles*/
	public ArrayList<BoundingBox> getFocalPoints(ArrayList<ArrayList<Double>> matrix) {
		
		ArrayList<BoundingBox> bbox=new ArrayList<BoundingBox>();
		BoundingBox b=new BoundingBox();
		b.setCenterLat( matrix.get(0).get(2) );
		b.setCenterLng( matrix.get(0).get(3) );
		bbox.add(b);
		double lat;
		double lng;
		
		for(int i=1;i<matrix.size();i++) {
			
			lat=matrix.get(i).get(2);
			lng=matrix.get(i).get(3);
			
			if((matrix.get(i-1).get(2)!=lat) || (matrix.get(i-1).get(3)!=lng)) {
				b=new BoundingBox();
				b.setCenterLat(lat);
				b.setCenterLng(lng);
				bbox.add(b);
			}
		}
		return bbox;
	}
	
	/**Group venues occurrences belonging to the same focal points*/
	public ArrayList<Double> groupSinglesToCell(BoundingBox b, 
												ArrayList<ArrayList<Double>> matrix) {
		
		double value;
		double cLat=b.getCenterLat().doubleValue(); //focal coordinates of the cell
		double cLng=b.getCenterLng().doubleValue();
		
		ArrayList<Double> toRet=buildListZero(matrix.get(0).size());
		toRet.set(0, cLat); //focal latitude of the cell
		toRet.set(1, cLng); //focal longitude of the cell
		
		//Grouping in cells
		for(int i=0;i<matrix.size();i++) {
			ArrayList<Double> record=matrix.get(i);
			
			//venues of the same cell
			if(record.get(0)==cLat && record.get(1)==cLng) {
				
				for(int j=2;j<record.size();j++) {
					//grouping by summing the occurrences
					value=toRet.get(j)+record.get(j);
					toRet.set(j, value);
				}
			}
		}
		return toRet;
	}
	
	/**Get a matrix with frequency values */
	public ArrayList<ArrayList<Double>> buildFrequencyMatrix(ArrayList<BoundingBox> data, 
															 ArrayList<ArrayList<Double>> venues) {
		
		ArrayList<ArrayList<Double>> frequency = 
								new ArrayList<ArrayList<Double>>();
		
		for(BoundingBox b: data) {
			frequency.add(groupSinglesToCell(b, venues));
		}
		
		return frequency;
	}
	
	/**Get a matrix with density values */
	public ArrayList<ArrayList<Double>> buildDensityMatrix(CoordinatesNormalizationType type, 
															ArrayList<ArrayList<Double>> matrix, 
															ArrayList<Double> area) {
		
		ArrayList<ArrayList<Double>> densMatrix = 
								new ArrayList<ArrayList<Double>>();
		if(type.equals(CoordinatesNormalizationType.NORM) || 
				type.equals(CoordinatesNormalizationType.NOTNORM)) {
			ArrayList<Double> densRecord;
			
			for(int i=0;i<matrix.size();i++) {
				densRecord=new ArrayList<Double>();
				densRecord.add(matrix.get(i).get(0)); //latitude
				densRecord.add(matrix.get(i).get(1)); //longitude
				
				//first 2 columns are for latitude and longitude, so j=2
				for(int j=2;j<matrix.get(i).size();j++) {
					densRecord.add(matrix.get(i).get(j)/area.get(i));
				}
				densMatrix.add(densRecord);
			}
		}
		else if(type.equals(CoordinatesNormalizationType.MISSING)) {
			ArrayList<Double> densRecord;
			
			for(int i=0;i<matrix.size();i++) {
				densRecord=new ArrayList<Double>();
				
				for(int j=0;j<matrix.get(i).size();j++) {
					densRecord.add(matrix.get(i).get(j)/area.get(i));
				}
				densMatrix.add(densRecord);
			}
		}
		return densMatrix;
	}
	
	/**Get a matrix normalized in [0,1]. Before normalization, densities are intra-feature normalized*/
	public ArrayList<ArrayList<Double>> buildNormalizedMatrix(
							CoordinatesNormalizationType type, 
							ArrayList<ArrayList<Double>> matrix) {
		
		ArrayList<ArrayList<Double>> intraFeatureMatrix = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> normalizedMatrix = 
							new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sumArray;
		
		switch (type) {
			case MISSING:
				
				//get all the sums of the features values per column
				sumArray=getSumArray(0, matrix);
				
				//get an intra-feature normalized matrix
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord = 
								getIntraFeatureNormalizationNoCoord(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
			case NORM:
				
				//get all the sums of the features values per column
				sumArray=getSumArray(2, matrix); //it starts from index 2 
												 //because first two are for 
												 //lat and lng
				
				//get an intra-feature normalized matrix except 
				//for the first two columns (lat and lng)
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord = 
								getIntraFeatureNormalization(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
			case NOTNORM:
				
				//get all the sums of the features values per column
				sumArray=getSumArray(2, matrix); // it starts from index 2 
												 //because first two are for 
												 //lat and lng
				
				//get an intra-feature normalized matrix except 
				//for the first two columns (lat and lng)
				for(ArrayList<Double> record: matrix) {
					ArrayList<Double> intraFeatureRecord = 
								getIntraFeatureNormalization(record, sumArray);
					intraFeatureMatrix.add(intraFeatureRecord);
				}
			break;
		}
		
		//get the arrays of min and max values
		ArrayList<Double> minArray=getMinArray(intraFeatureMatrix);
		ArrayList<Double> maxArray=getMaxArray(intraFeatureMatrix);
		
		//Shift all the values in [0,1] according to each 
		//min and max value of the column
		for(ArrayList<Double> record: intraFeatureMatrix) {
			ArrayList<Double> normalizedRecord=normalizeRow(type, 
															record, 
															minArray, 
															maxArray);
			normalizedMatrix.add(normalizedRecord);	
		}
		return normalizedMatrix;
	}
		
	/**Get the total number of elements of all the categories*/
	public ArrayList<Double> getSumArray(int start, ArrayList<ArrayList<Double>> matrix) {
		
		ArrayList<Double> sumArray=new ArrayList<Double>();
		double sum=0;
		
		for(int j=start; j<matrix.get(0).size(); j++) {
			
			sum=getSum(matrix, j);
			sumArray.add(sum);
		}
		
		return sumArray;
	}
	
	/**Get the total number of elements of a specific category*/
	public double getSum(ArrayList<ArrayList<Double>> matrix, int index) {
		
		double sum=0;
		
		for(ArrayList<Double> record: matrix) {
			sum+=record.get(index);
		}
		return sum;
	}
	
	/**Get an intra-feature normalized row of the matrix 
	 * without considering lat and lng coordinates
	*/
	public ArrayList<Double> getIntraFeatureNormalizationNoCoord(ArrayList<Double> record, 
																 ArrayList<Double> sumArray) {
		
		ArrayList<Double> normalizedRecord=new ArrayList<Double>();
		double currentValue=0.0;
		double normalizedValue=0.0;
		double denominator=0.0;
		
		for(int j=0; j<record.size(); j++) {
			
			currentValue=record.get(j); //get the value
			denominator=sumArray.get(j);
			
			//check if denominator is bigger than 0
			if(denominator > 0.0)
				normalizedValue=(currentValue/denominator); //intra-feature normalized value
			else
				normalizedValue=0.0;
			
			normalizedRecord.add(normalizedValue);
		}
		
		return normalizedRecord;
	}
	
	/**Get an intra-feature normalized row of the matrix*/
	public ArrayList<Double> getIntraFeatureNormalization(ArrayList<Double> record, 
														  ArrayList<Double> sumArray) {
		
		ArrayList<Double> normalizedRecord=new ArrayList<Double>();
		double currentValue=0;
		double denominator=0;
		double normalizedValue=0;
		normalizedRecord.add(record.get(0)); //latitude
		normalizedRecord.add(record.get(1)); //longitude
		
		for(int j=2; j<record.size(); j++) {
			currentValue=record.get(j);
			denominator=sumArray.get(j-2);
			
			//check if denominator is bigger than 0
			if(denominator > 0)
				normalizedValue=currentValue/denominator; //intra-feature normalized value
			else
				normalizedValue=0;
			
			normalizedRecord.add(normalizedValue);
		}
		
		return normalizedRecord;
	}
	
	/**Get the min value of a column*/
	public double getMin(ArrayList<ArrayList<Double>> matrix, int index) {
		
		double min=1*Double.MAX_VALUE;
		double current;
		
		for(ArrayList<Double> record: matrix) {
			
			current=record.get(index);
			
			if(current < min)
				min=current;
		}
		return min;
	}
	
	/**Get min values of all the columns of the matrix*/
	public ArrayList<Double> getMinArray(ArrayList<ArrayList<Double>> matrix){
		
		ArrayList<Double> minArray=new ArrayList<Double>();
		
		for(int i=0; i<matrix.get(0).size(); i++) {
			
			double min=getMin(matrix, i);
			minArray.add(min); //max value of column j
		}
		
		return minArray;
	}
	
	/**Get the max value of a column*/
	public double getMax(ArrayList<ArrayList<Double>> matrix, int index) {
		
		double max=-1*Double.MAX_VALUE;
		double current;
		
		for(ArrayList<Double> record: matrix) {
			
			current=record.get(index);
			
			if(current>max)
				max=current;
		}
		
		return max;
	}
	
	/**Get max values of all the columns of the matrix*/
	public ArrayList<Double> getMaxArray(ArrayList<ArrayList<Double>> matrix){
		
		ArrayList<Double> maxArray=new ArrayList<Double>();
		
		for(int i=0; i<matrix.get(0).size(); i++) {
			
			double max=getMax(matrix, i);
			maxArray.add(max); //max value of column j
		}
		
		return maxArray;
	}
	
	/**Normalize the values of a row in [0,1] with respect to 
	 * their own  min and max values
	*/
	public ArrayList<Double> normalizeRow(CoordinatesNormalizationType type, 
										  ArrayList<Double> array, 
										  ArrayList<Double> minArray, 
										  ArrayList<Double> maxArray) {
		
		ArrayList<Double> normalizedArray=new ArrayList<Double>();
		double normalizedValue;
		double min=0;
		double max=0;
		
		switch (type) {
			case NORM:
				
				for(int i=0;i<array.size();i++) {
					
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
			case NOTNORM:
				
				normalizedArray.add(array.get(0)); //latitude
				normalizedArray.add(array.get(1)); //longitude
				
				for(int i=2;i<array.size();i++) {
					
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
			case MISSING:
				
				for(int i=0;i<array.size();i++) {
					
					min=minArray.get(i);
					max=maxArray.get(i);
					normalizedValue=normalizeValues(min, max, array.get(i));
					normalizedArray.add(normalizedValue);
				}
			break;
		}
		
		return normalizedArray;
	}
	
	/**Normalize a value in [0,1]*/
	public double normalizeValues(double min, double max, double c) {
		
		double norm_c=0;
		
		if(max!=0 || min!=0)
			norm_c=(c-min)/(max-min);
		
		return norm_c;
	}
	
	/**Get the feature labeled either for 
	 * frequency, density or normalized density
	*/
	public ArrayList<String> getFeaturesLabel(CoordinatesNormalizationType type, 
											  String s, 
											  ArrayList<String> features) {
		
		ArrayList<String> featuresLabel=new ArrayList<String>();
		
		if(type.equals(CoordinatesNormalizationType.NORM) || 
				type.equals(CoordinatesNormalizationType.NOTNORM)) {
			
			String label="";
			featuresLabel.add("Timestamps(ms)"); //Timestamps
			featuresLabel.add("Latitude"); //Latitude
			featuresLabel.add("Longitude"); //Longitude
			
			//first 2 features area lat and lng so i=2 
			for(int i=2; i<features.size(); i++) {
				
				label=s+"("+features.get(i)+")";
				featuresLabel.add(label);
			}
		}
		else if(type.equals(CoordinatesNormalizationType.MISSING)) {
			
			String label="";
			
			for(int i=2;i<features.size();i++) {
				
				label=s+"("+features.get(i)+")";
				featuresLabel.add(label);
			}
		}
		
		return featuresLabel;
	}
	
	/**
	 * Get the header of the dataset of single venues
	 * The header will include only the values focal_latitude, focal_longitude, f1,...,fn.
	 * f1,....,fn are the venue categories.
	*/
	public ArrayList<String> getHeader(List<CSVRecord> list) {
		
		ArrayList<String> header = new ArrayList<String>();
		for(int i=5; i<list.get(0).size(); i++) {
			header.add(list.get(0).get(i));
		}
		return header;
	}
	
	/**
	 * Get the values of the dataset of single venues
	 * The columns included for the values correspond to 
	 * focal_latitude, focal_longitude, f1,...,fn.
	 * f1,....,fn are the venue categories.
	*/
	public ArrayList<ArrayList<Double>> getValues(List<CSVRecord> list) {
		
		ArrayList<ArrayList<Double>> venues = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rec;
		
		//i=1 because of the header
		for(int i=1;i<list.size();i++) {
			rec = new ArrayList<Double>();
			
			//we don't consider timestamp, been_here, venue_id, venue_lat, venue_lng
			for(int j=5;j<list.get(i).size();j++) {
				
				rec.add(Double.parseDouble(list.get(i).get(j)));
			}
			venues.add(rec);
		}
		
		return venues;
	}
	
	/** Haversine formula implementation. It returns the distance between 
	 * two points given latitude and longitude values in Km
	 */
	public double getDistance(double lat1, double lng1, double lat2, double lng2){
		
		double earthRadius = 6372.8; //in Km
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    //round to the 3rd digit
	    return Math.floor(dist*1000)/1000;
	}		
	
    public static void main(String[] args) {
    	ImportTools tools = new ImportTools();
        System.out.println(tools.getDistance(36.12, -86.67, 33.94, -118.40));
    }
}
