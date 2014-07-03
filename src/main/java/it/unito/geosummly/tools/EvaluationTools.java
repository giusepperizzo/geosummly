package it.unito.geosummly.tools;

import it.unito.geosummly.BoundingBox;
import it.unito.geosummly.utils.Pair;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.distribution.NormalDistribution;

import jp.ndca.similarity.distance.Jaccard;

public class EvaluationTools {

	public EvaluationTools() {
	}

	/**
	 * Fill in the matrix of aggregate (frequency) values from a list of CSV
	 * records. The header won't be considered. Timestamp, latitude and
	 * longitude columns won't be considered.
	 */
	public ArrayList<ArrayList<Double>> buildAggregatesFromList(
			List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		
		// remove the header, so i=1
		for (int k = 1; k < list.size(); k++) {
			ArrayList<Double> rec = new ArrayList<Double>();
			// remove timestamp, latitude and longitude columns, so j=3
			for (int j = 3; j < list.get(k).size(); j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		return matrix;
	}

	/**
	 * Fill in the matrix of single venues from a list of CSV records. The
	 * header won't be considered. Timestamp, been here and venue id columns
	 * won't be considered.
	 */
	public ArrayList<ArrayList<Double>> buildSinglesFromList(
			List<CSVRecord> list) {
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		// remove the header, so i=1
		for (int k = 1; k < list.size(); k++) {
			ArrayList<Double> rec = new ArrayList<Double>();
			// remove timestamp, been_here, id_venue columns, so j=3
			for (int j = 3; j < list.get(k).size(); j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		return matrix;
	}

	/**
	 * Fill in the list of features from a list of CSV records. Timestamp column
	 * won't be considered.
	 */
	public ArrayList<String> getFeaturesFormList(List<CSVRecord> list) {
		ArrayList<String> features = new ArrayList<String>();
		// remove timestamp column, so i=1
		for (int i = 1; i < list.get(0).size(); i++) {
			features.add(list.get(0).get(i));
		}
		return features;
	}

	/**
	 * Fill in the random matrix of aggregate (frequency) values from the
	 * original matrix of aggregate (frequency) values.
	 */
	public ArrayList<ArrayList<Double>> buildFrequencyRandomMatrix(int size,
			ArrayList<Double> minArray, ArrayList<Double> maxArray) {
		ArrayList<ArrayList<Double>> frm = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> randomRecord;
		double randomValue;
		int min;
		int max;
		int i = 0;
		int j = 0;

		// matrix.size() records per matrix
		while (i < size) {
			randomRecord = new ArrayList<Double>();
			// get randomly the feature values
			while (j < minArray.size()) {
				min = minArray.get(j).intValue();
				max = maxArray.get(j).intValue();
				randomValue = min + (int) (Math.random() * (max - min + 1)); // random
																				// number
																				// from
																				// min
																				// to
																				// max
																				// included
				randomRecord.add(randomValue);
				j++;
			}
			frm.add(randomRecord);
			j = 0;
			i++;
		}
		return frm;
	}

	/**
	 * Fill in the list of features from a list of CSV records. Timestamp, been
	 * here, venue id, venue latitude, venue longitude, centroid latitude and
	 * centroid longitude columns won't be considered.
	 */
	public ArrayList<String> getFeaturesFromList(List<CSVRecord> list) {

		ArrayList<String> features = new ArrayList<String>();

		// Don't consider timestamp, been here, venue id, venueLat, venueLng,
		// focalLat, focalLng, so k=7
		for (int i = 7; i < list.get(0).size(); i++)
			features.add(list.get(0).get(i));

		return features;
	}

	/**
	 * Fill in the list of timestamp values from a list of CSV records.
	 */
	public ArrayList<Long> getTimestampsFromList(List<CSVRecord> list) {
		ArrayList<Long> timestamps = new ArrayList<Long>();
		// we don't have to consider the header so i=1
		for (int i = 1; i < list.size(); i++)
			timestamps.add(Long.parseLong(list.get(i).get(0)));
		return timestamps;
	}

	/**
	 * Create the folds for cross-validation. The result will be a list of fnum
	 * matrices of singles with N/fnum random venues for each matrix.
	 */
	public ArrayList<ArrayList<ArrayList<Double>>> createFolds(
						ArrayList<ArrayList<Double>> matrix, int fnum) {
		
		ArrayList<ArrayList<ArrayList<Double>>> allMatrices = 
							new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> ithMatrix;
		int dimension = matrix.size() / fnum;
		int randomValue;
		Random random = new Random();
		
		for(int i=0; i<fnum; i++) {
			ithMatrix = new ArrayList<ArrayList<Double>>();
			
			for(int j=0; j<dimension; j++) {
				randomValue = random.nextInt(matrix.size()); // random number
																// between 0
																// (included)
																// and current
																// matrix.size()
																// (excluded)
				ithMatrix.add(matrix.get(randomValue));
			}
			
			allMatrices.add(ithMatrix);
			matrix.removeAll(ithMatrix);
		}
		
		return allMatrices;
	}

	/**
	 * Remove the columns venue_latitude and venue_longitude from the folds
	 */
	public ArrayList<ArrayList<ArrayList<Double>>> removeVenueCoordinates(ArrayList<ArrayList<ArrayList<Double>>> folds) {
		
		ArrayList<ArrayList<ArrayList<Double>>> newFolds = 
								new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> newF;
		ArrayList<Double> newRecord;
		
		for(ArrayList<ArrayList<Double>> f: folds)  {
			//create the new fold
			newF = new ArrayList<ArrayList<Double>>();
			for(ArrayList<Double> rec: f) {
				newRecord = new ArrayList<Double>();
				//create the new record without venue coordinates
				for(int i=2; i<rec.size(); i++) {
					newRecord.add(rec.get(i));
				}
				newF.add(newRecord);
			}
			//add the new fold to the folds list
			newFolds.add(newF);
		}
		
		return newFolds;
	}
	
	/**Get (as bounding boxes) all the distinct focal coordinates of singles*/
	public ArrayList<BoundingBox> getFocalPoints(ArrayList<ArrayList<Double>> matrix) {
		ArrayList<BoundingBox> bbox=new ArrayList<BoundingBox>();
		BoundingBox b=new BoundingBox();
		b.setCenterLat(new BigDecimal(matrix.get(0).get(2)));
		b.setCenterLng(new BigDecimal(matrix.get(0).get(3)));
		bbox.add(b);
		double lat;
		double lng;
		
		for(int i=1;i<matrix.size();i++) {
			lat=matrix.get(i).get(2);
			lng=matrix.get(i).get(3);
			
			//control only the previous venue because only
			//consecutive venues have the same focal points
			if((matrix.get(i-1).get(2)!=lat) || (matrix.get(i-1).get(3)!=lng)) {
				b=new BoundingBox();
				b.setCenterLat(new BigDecimal(lat));
				b.setCenterLng(new BigDecimal(lng));
				bbox.add(b);
			}
		}
		return bbox;
	}
	
	/** Haversine formula implementation. It returns the distance in kilometers between 
	 * two points given their latitude and longitude values
	 */
	public double getDistance(double lat1, double lng1, double lat2, double lng2){
		
		double earthRadius = 6371; //in km
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    DecimalFormat df = new DecimalFormat("#.###");
	    String value = df.format(dist);
	    value = value.replace(",", ".");
	    
	    return Double.parseDouble(value);
	}
	
	/**
	 * Get all the areas in (squared kilometers) of the grid cells 
	 * by considering only the focal points
	*/
	public ArrayList<Double> getAreasFromFocalPoints(ArrayList<BoundingBox> data, 
												     					int size) {
		
		ArrayList<Double> areas=new ArrayList<Double>();
		
		double edgeValue = getDistance(data.get(0).getCenterLat().doubleValue(), 
									   data.get(0).getCenterLng().doubleValue(), 
									   data.get(1).getCenterLat().doubleValue(),
									   data.get(1).getCenterLng().doubleValue());
		double areaValue=Math.pow(edgeValue, 2);
		
		for(int i=0; i<size ;i++)
			areas.add(areaValue);
		
		return areas;
	}
	
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
	
	/**Group venues occurrences belonging to the same focal points*/
	public ArrayList<Double> groupSinglesToCell(BoundingBox b, ArrayList<ArrayList<Double>> matrix) {
		
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
					value=toRet.get(j)+record.get(j); //grouping by summing the occurrences
					toRet.set(j, value);
				}
			}
		}
		return toRet;
	}

	/**
	 * Group the folds from single venues to cells.
	 */
	public ArrayList<ArrayList<ArrayList<Double>>> groupFolds(
						ArrayList<BoundingBox> data, 
						ArrayList<ArrayList<ArrayList<Double>>> allMatrices) {
		
		ArrayList<ArrayList<ArrayList<Double>>> allGrouped = 
									new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> ithGrouped;
		
		for (ArrayList<ArrayList<Double>> m : allMatrices) {
			ithGrouped = new ArrayList<ArrayList<Double>>();
			
			for (BoundingBox b : data) {
				ithGrouped.add(groupSinglesToCell(b, m));
			}
			allGrouped.add(ithGrouped);
		}
		
		return allGrouped;
	}

	/**
	 * Compute the Jaccard similarity coefficient between the holdouts
	 */
	public StringBuilder computeJaccard(
			List<HashMap<String, Vector<Integer>>> holdoutList) {
		StringBuilder builder = new StringBuilder();
		Jaccard jacc = new Jaccard();
		Double jaccOnLabels = 0.0;
		Double jaccOnSet = 0.0;
		HashMap<String, Vector<Double>> jaccOnSets = new HashMap<>();
		int iterations = 0;
		for (int i = 0; i < holdoutList.size() - 1; i++) {
			HashMap<String, Vector<Integer>> ho1 = holdoutList.get(i);
			for (int j = i + 1; j < holdoutList.size(); j++) {
				builder.append("pair (" + i + "," + j + ")\n");

				HashMap<String, Vector<Integer>> ho2 = holdoutList.get(j);
				builder.append("\tjaccard_labels="
						+ jacc.calc(ho1.keySet().toArray(), ho2.keySet()
								.toArray()) + "\n");

				// get cluster names from first set
				Set<String> cluster_names = new HashSet<String>();
				cluster_names.addAll(ho1.keySet());
				cluster_names.addAll(ho2.keySet());

				Double jaccOnPair = 0.0;
				for (String name : cluster_names) {
					Vector<Integer> ho1_objects = (ho1.get(name) == null) ? new Vector<Integer>()
							: ho1.get(name);
					Vector<Integer> ho2_objects = (ho2.get(name) == null) ? new Vector<Integer>()
							: ho2.get(name);
					builder.append("\tjaccard_on_set(" + name + ")="
							+ jacc.calc(ho1_objects, ho2_objects) + "\n");

					if (!jaccOnSets.containsKey(name)) {
						Vector<Double> v = new Vector<>();
						v.add(jacc.calc(ho1_objects, ho2_objects));
						jaccOnSets.put(name, v);
					} else {
						Vector<Double> v = jaccOnSets.get(name);
						jaccOnSets.remove(name);
						v.add(jacc.calc(ho1_objects, ho2_objects));
						jaccOnSets.put(name, v);
					}

					jaccOnPair += jacc.calc(ho1_objects, ho2_objects);
				}
				builder.append("\tjaccard_on_set_average=" + jaccOnPair
						/ cluster_names.size() + "\n");

				jaccOnLabels += jacc.calc(ho1.keySet().toArray(), ho2.keySet()
						.toArray());
				jaccOnSet += jaccOnPair / cluster_names.size();
				iterations++;
			}
		}
		builder.append("#####\n#  Totals\n####\n");
		builder.append("avg_jaccard_labels=" + jaccOnLabels / iterations + "\n");
		builder.append("avg_jaccard_objects=" + jaccOnSet / iterations + "\n");

		for (Entry<String, Vector<Double>> entry : jaccOnSets.entrySet()) {
			Double counter = 0.0;
			for (Double d : entry.getValue()) {
				counter += d;
			}
			builder.append("avg_jaccard_cluster(" + entry.getKey() + ")="
					+ counter / entry.getValue().size() + "\n");
		}
		return builder;
	}
	
	/**
	 * Compute the Jaccard similarity coefficient between the pair of holdouts, computed 
	 * in a x-fold cross validation 
	 */
	public StringBuilder computeJaccard2(List<Pair<?,?>> folds) 
	{
		StringBuilder builder = new StringBuilder();
		Jaccard jacc = new Jaccard();
		Double jaccOnLabels = 0.0;
		Double jaccOnSet = 0.0;
		
		HashMap<String, Vector<Double>> jaccOnSets = new HashMap<>();

		for (int i=0; i < folds.size(); i++)
		{
			Pair<?,?> fold = folds.get(i);
			
			// locally copy set A
			HashMap<String, Vector<Integer>> ho1 = (HashMap<String, Vector<Integer>>) fold.getFirst();
			// locally copy set B
			HashMap<String, Vector<Integer>> ho2 = (HashMap<String, Vector<Integer>>) fold.getSecond();
			builder.append("pair (A,B) of fold=" + (i+1) + "\n");
	
			builder.append("\tjaccard_labels=" + 
					jacc.calc(ho1.keySet().toArray(), ho2.keySet().toArray()) + "\n");
			
			// get cluster names from first set
			Set<String> cluster_names = new HashSet<String>();
			cluster_names.addAll(ho1.keySet());
			// get cluster names from second set
			cluster_names.addAll(ho2.keySet());
	
			Double jaccOnPair = 0.0;
			for (String name : cluster_names) 
			{
				Vector<Integer> ho1_objects = (ho1.get(name) == null) ? new Vector<Integer>()
						: ho1.get(name);
				Vector<Integer> ho2_objects = (ho2.get(name) == null) ? new Vector<Integer>()
						: ho2.get(name);
				builder.append("\tjaccard_on_set(" + name + ")="
						+ jacc.calc(ho1_objects, ho2_objects) + "\n");
	
				if (!jaccOnSets.containsKey(name)) {
					Vector<Double> v = new Vector<>();
					v.add(jacc.calc(ho1_objects, ho2_objects));
					jaccOnSets.put(name, v);
				} else {
					Vector<Double> v = jaccOnSets.get(name);
					jaccOnSets.remove(name);
					v.add(jacc.calc(ho1_objects, ho2_objects));
					jaccOnSets.put(name, v);
				}
	
				jaccOnPair += jacc.calc(ho1_objects, ho2_objects);
			}
			builder.append("\tjaccard_on_set_average=" + 
					jaccOnPair / cluster_names.size() + "\n");
			
			jaccOnLabels += jacc.calc(ho1.keySet().toArray(), ho2.keySet()
					.toArray());
			jaccOnSet += jaccOnPair / cluster_names.size();
		}
	
		//end x folds
		builder.append("#####\n#  Totals\n####\n");
		builder.append("avg_jaccard_labels=" + jaccOnLabels / folds.size() + "\n");
		builder.append("avg_jaccard_objects=" + jaccOnSet / folds.size() + "\n");
		for (Entry<String, Vector<Double>> entry : jaccOnSets.entrySet()) 
		{
			Double counter = 0.0;
			for (Double d : entry.getValue()) {
				counter += d;
			}
			builder.append("avg_jaccard_cluster(" + entry.getKey() + ")="
					+ counter / entry.getValue().size() + "\n");
		}
		return builder;
	}	
	

	/**
	 * Compute the ratio in percentage between the SSE of clustering output on
	 * the entire dataset (real_SSE) and the minimum SSE value of correctness
	 * experiment (random_SSE). discard= (real_SSE * 100) / random_SSE
	 */
	public double getSSERatio(ArrayList<Double> SSEs, double cl_sse) 
	{
		Collections.sort(SSEs);
		double min = SSEs.get(0); // get the minimum of SSEs
		double discard = (cl_sse * 100) / min;

		return discard;
	}
	
	
	public double getPvalue(ArrayList<Double> SSEs, double cl_sse) {
		Collections.sort(SSEs);
		
//		for (Double sse : SSEs) 
//			System.out.println("EvaluationTools425: " + sse);
		
		double mean = getMean(SSEs.toArray(new Double[] {}));
		double std = getStd( getVariance(SSEs.toArray(new Double[] {}), mean) );
		
	    /*
	      	68,3% = P{ μ -      σ < X <  μ +      σ }
			95,0% = P{ μ - 1,96 σ < X <  μ + 1,96 σ }	
			95,5% = P{ μ - 2    σ < X <  μ + 2    σ }
			99,0% = P{ μ - 2,58 σ < X <  μ + 2,58 σ }
			99,7% = P{ μ - 3    σ < X <  μ + 3    σ }
	     */
		
		//http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/NormalDistribution.html
		NormalDistribution norm = new NormalDistribution(mean, std);
		double density = norm.density(cl_sse);
		
//		System.out.println("EvaluationTools439: " + density + " having mean=" + mean + " and std=" + std);
		
		return density;
	}

	private double getVariance(Double[] array, double mean) 
	{
		double value=0;
		for(Double d: array) {
			value+=(mean-d)*(mean-d);
		}
		return value/array.length;
	}

	/** Change the feature label by replacing 'old' with 'last' */
	public ArrayList<String> changeFeaturesLabel(String old, 
												 String last,
												 ArrayList<String> features) {
		
		String label = "";
		ArrayList<String> featuresLabel = new ArrayList<String>();
		
		for (int i=0; i<features.size(); i++) {
			
			//remove character and parenthesis
			label = features.get(i).replaceFirst(old, last)
					.replaceAll("\\(", "").replaceAll("\\)", "");
			
			featuresLabel.add(label);
		}
		
		return featuresLabel;
	}

	/**
	 * Get the feature labeled either for frequency, density or normalized
	 * density without timestamp column
	 */
	public ArrayList<String> getFeaturesLabelNoTimestamp(
								CoordinatesNormalizationType type, 
								String s,
								ArrayList<String> features) {
		
		ArrayList<String> featuresLabel = new ArrayList<String>();
		
		if (type.equals(CoordinatesNormalizationType.NORM) ||
			type.equals(CoordinatesNormalizationType.NOTNORM)) {
			
			String label = "";
			featuresLabel.add(features.get(0)); // Latitude
			featuresLabel.add(features.get(1)); // Longitude
			
			for (int i = 2; i < features.size(); i++) {
				label = s + "(" + features.get(i) + ")";
				featuresLabel.add(label);
			}
		} else if (type.equals(CoordinatesNormalizationType.MISSING)) {
			String label = "";
			for (int i = 2; i < features.size(); i++) {
				label = s + "(" + features.get(i) + ")";
				featuresLabel.add(label);
			}
		}
		return featuresLabel;
	}
	
	public ArrayList<ArrayList<Double>> build(List<CSVRecord> list) {
		
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		
		//remove the header, so i=1
		for(int k=1; k<list.size(); k++) {
			
			ArrayList<Double> rec = new ArrayList<Double>();
			
			//remove timestamp comlumn, so j=1
			for(int j=1; j<list.get(k).size(); j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			
			matrix.add(rec);
		}
		
		return matrix;
	}
	
	public ArrayList<String> getFeaturesLabel(String s, ArrayList<String> features) {

		ArrayList<String> featuresLabel=new ArrayList<String>();
		
		String label="";
		featuresLabel.add("Latitude"); //Latitude
		featuresLabel.add("Longitude"); //Longitude
	
		//first 2 features area lat and lng so i=2 
		for(int i=2; i<features.size(); i++) {
	
			label=s+"("+features.get(i)+")";
			featuresLabel.add(label);
		}
		
		return featuresLabel;
	}
	
	public ArrayList<ArrayList<Double>> buildNorm(ArrayList<ArrayList<Double>> matrix) {
		
		Random r = new Random();
		int r_size = matrix.get(0).size()-2; //we don't considerate coordinates
		ArrayList<ArrayList<Double>> randomNorm = new ArrayList<ArrayList<Double>>();
		
		for(ArrayList<Double> record: matrix) {
			ArrayList<Double> randomRec = new ArrayList<Double>();
			randomRec.add(record.get(0)); //latitude
			randomRec.add(record.get(1)); //longitude
			
			int i=0;
			while (i < r_size) 
			{
				double value = r.nextDouble(); //r.nextGaussian();
				//increment record position only if the value is between 0 and 1 included
				if( (value >= 0.0) && (value <= 1.0) ) {
					randomRec.add(value);
					i++;
				}
			}
			randomNorm.add(randomRec);
		}
		return randomNorm;
	}
	
	public ArrayList<ArrayList<Double>> buildNormalizedUniformly(ArrayList<ArrayList<Double>> matrix) 
	{
		Random r = new Random();
		int n_rows = matrix.size(); 
		int n_columns = matrix.get(0).size();
		ArrayList<ArrayList<Double>> randomNorm = new ArrayList<ArrayList<Double>>();
		
		for (int i=0; i<n_rows; i++){
			ArrayList<Double> randomRec = new ArrayList<Double>();
			
			for (int j=0; j<n_columns; j++)
				randomRec.add(r.nextDouble());

			randomNorm.add(randomRec);

		}
		return randomNorm;
	}	
	
	public double getMean(Double[] array) 
	{
		double total = 0;

		for(int i = 0; i < array.length; i++){
		   total += array[i]; // this is the calculation for summing up all the values
		}
		return total / (1.0 *array.length); //mean
	}
	
	public double getStd(double variance) {
		return Math.sqrt(variance);
	}

	//TODO
	//yet to complete
	public ArrayList<ArrayList<ArrayList<Double>>> doHoldOut( 
					ArrayList<ArrayList<Double>> matrix, 
					int num) 
	{
		ArrayList<ArrayList<ArrayList<Double>>> result = 
				new ArrayList<ArrayList<ArrayList<Double>>>();
		
		ArrayList<ArrayList<Double>> ithMatrix;
		int dimension = matrix.size() / num;
		int randomValue;
		Random random = new Random();
		
		for(int i=0; i<num; i++) {
		ithMatrix = new ArrayList<ArrayList<Double>>();
		
		for(int j=0; j<dimension; j++) {
			randomValue = random.nextInt(matrix.size()); // random number
															// between 0
															// (included)
															// and current
															// matrix.size()
															// (excluded)
			ithMatrix.add(matrix.get(randomValue));
		}
		
		result.add(ithMatrix);
		matrix.removeAll(ithMatrix);
		}
		
		return result;
	}
}
