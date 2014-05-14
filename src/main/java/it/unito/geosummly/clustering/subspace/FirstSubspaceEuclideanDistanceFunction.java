package it.unito.geosummly.clustering.subspace;

import java.util.BitSet;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.SubspaceLPNormDistanceFunction;

public class FirstSubspaceEuclideanDistanceFunction extends SubspaceLPNormDistanceFunction {

  public FirstSubspaceEuclideanDistanceFunction(BitSet dimensions) {
    super(2.0, dimensions);
  }

  /**
   * Provides the Euclidean distance between two given feature vectors in the
   * selected dimensions.
   * 
   * @param v1 first feature vector
   * @param v2 second feature vector
   * @return the Euclidean distance between two given feature vectors in the
   *         selected dimensions
   */
  @Override
  public double doubleDistance(NumberVector<?> v1, NumberVector<?> v2) 
  {
    if(v1.getDimensionality() != v2.getDimensionality()) {
      throw new IllegalArgumentException("Different dimensionality of FeatureVectors\n  " + "first argument: " + v1 + "\n  " + "second argument: " + v2);
    }
        
    double sqrDist = 0;
    int last=-1;
    for(int d = dimensions.nextSetBit(0); d >= 0; d = dimensions.nextSetBit(d + 1)) {
      final double delta = v1.doubleValue(d) - v2.doubleValue(d);
      last = d;
      sqrDist += delta * delta;
    }
    
//	if( v1.doubleValue(last) == v2.doubleValue(last) && v1.doubleValue(last) == 0.0) {
//		return Double.POSITIVE_INFINITY;
//	}
    
    if( v1.doubleValue(last) == 0.0 || v2.doubleValue(last) == 0.0 ) 
    	return Double.POSITIVE_INFINITY;
    
    else return Math.sqrt(sqrDist);
  }
}