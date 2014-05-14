package it.unito.geosummly.clustering.subspace;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SubspaceClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.Subspace;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.ProxyDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.database.relation.RelationUtil;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.DimensionSelectingSubspaceDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.SubspaceEuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.SubspaceLPNormDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.StepProgress;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Centroid;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * <p>
 * TODO rephrase it
 * 
 * @author Giuseppe Rizzo
 * 
 * @apiviz.uses DBSCAN
 * @apiviz.uses AbstractDimensionsSelectingDoubleDistanceFunction
 * @apiviz.has SubspaceModel
 * 
 * @param <V> the type of FeatureVector handled by this Algorithm
 */
@Title("GEOSUBCLU: Density connected Subspace Clustering on seed Geospatial Data")
@Description("Largely inspired by SUBCLU, GEOSUBCLU applies subspace clustering on a geospatial data set")
@Reference(authors = "Giuseppe Rizzo", title = "", booktitle = "")
public class GEOSUBCLU<V extends NumberVector<?>> 
	extends AbstractAlgorithm<Clustering<SubspaceModel<V>>> 
	implements SubspaceClusteringAlgorithm<SubspaceModel<V>> 
{
  /**
   * The logger for this class.
   */
  private static final Logging LOG = Logging.getLogger(GEOSUBCLU.class);

  /**
   * The distance function to determine the distance between database objects.
   * <p>
   * Default value: {@link SubspaceEuclideanDistanceFunction}
   * </p>
   * <p>
   * Key: {@code -subclu.distancefunction}
   * </p>
   */
  public static final OptionID DISTANCE_FUNCTION_ID = new OptionID("subclu.distancefunction", "Distance function to determine the distance between database objects.");

  /**
   * Parameter to specify the maximum radius of the neighborhood to be
   * considered, must be suitable to
   * {@link DimensionSelectingSubspaceDistanceFunction}.
   * <p>
   * Key: {@code -geosubclu.epsilon}
   * </p>
   */
  //public static final OptionID EPSILON_ID = new OptionID("geosubclu.epsilon", "The maximum radius of the neighborhood to be considered.");

  /**
   * Parameter to specify the threshold for minimum number of points in the
   * epsilon-neighborhood of a point, must be an integer greater than 0.
   * <p>
   * Key: {@code -geosubclu.minpts}
   * </p>
   */
//  public static final OptionID MINPTS_ID = new OptionID("geosubclu.minpts", "Threshold for minimum number of points in the epsilon-neighborhood of a point.");

  /**
   * Holds the instance of the distance function specified by
   * {@link #DISTANCE_FUNCTION_ID}.
   */
  //private DimensionSelectingSubspaceDistanceFunction<V, DoubleDistance> distanceFunction;

  /**
   * Holds the value of {@link #EPSILON_ID}.
   */
  //private DoubleDistance epsilon = new DoubleDistance(0.01);

  /**
   * Holds the value of {@link #MINPTS_ID}.
   */
  //private int minpts = 1 ;

  /**
   * Holds the result;
   */
  private Clustering<SubspaceModel<V>> result;

  private Map<Integer,String> FEATUREMAPPER ;
  
  private Map<String,Double> DELTAD ;
  
  private Map<String,Double> EPS ;
  
  private int DENSITY;
  
  private double epsValue;
  
  private StringBuilder sbLog;
  
  /**
   * Constructor.
   * 
   * @param distanceFunction Distance function
   * @param epsilon Epsilon value
   * @param minpts Minpts value
   */
  public GEOSUBCLU( DimensionSelectingSubspaceDistanceFunction<V, DoubleDistance> distanceFunction) 
  {
    super();
    //this.distanceFunction=distanceFunction;
    
    /*
     * 	initialize eps
     */
    EPS = new HashMap<>();
  }
  
  public void setFeatureMapper(HashMap<Integer, String> featuremapper) {
	  this.FEATUREMAPPER=featuremapper;
  }
  
  public void setDeltad(HashMap<String, Double> deltad) {
	  this.DELTAD=deltad;
  }
  
  public void setDensity(int density) {
	  this.DENSITY=density;
  }
  
  public void setEpsValue(double epsValue) {
	  this.epsValue=epsValue;
  }
  
  public void setSbLog(StringBuilder sb) {
	  this.sbLog=sb;
  }
  
  public StringBuilder getSbLog() {
	  return sbLog;
  }
  
  /**
   * Performs the GEOSUBCLU algorithm on the given database.
   * 
   * @param relation Relation to process
   * @return Clustering result
   */
  public Clustering<SubspaceModel<V>> run(Relation<V> relation) {
    final int dimensionality = RelationUtil.dimensionality(relation);

    StepProgress stepprog = LOG.isVerbose() ? new StepProgress(dimensionality) : null;

    // Generate all 3-dimensional clusters
    if (stepprog != null) {
      stepprog.beginStep(1, "Generate all 3-dimensional clusters.", LOG);
    }

    // mapping of dimensionality to set of subspaces
    HashMap<Integer, List<Subspace>> subspaceMap = new HashMap<>();

    // list of 3-dimensional subspaces containing clusters
    List<Subspace> s_3 = new ArrayList<>();
    subspaceMap.put(0, s_3);

    // mapping of subspaces to list of clusters
    TreeMap<Subspace, List<Cluster<Model>>> clusterMap = new TreeMap<>(new Subspace.DimensionComparator());

    //the first two dimensions are used as seeds for each cluster generation
    for (int d = 2; d < dimensionality; d++) {
      
      Subspace currentSubspace = new Subspace(d);
      //List<Cluster<Model>> clusters = runDBSCAN(relation, null, currentSubspace);
      List<Cluster<Model>> clusters = runDBSCAN(relation, null, currentSubspace, new FirstSubspaceEuclideanDistanceFunction(new BitSet()));

      if (LOG.isDebuggingFiner()) {
        StringBuilder msg = new StringBuilder();
        msg.append('\n').append(clusters.size()).append(" clusters in subspace ").append(currentSubspace.dimensonsToString()).append(": \n");
        for (Cluster<Model> cluster : clusters) {
          msg.append("      " + cluster.getIDs() + "\n");
        }
        LOG.debugFiner(msg.toString());
      }

      if (!clusters.isEmpty()) {
        s_3.add(currentSubspace);
        clusterMap.put(currentSubspace, clusters);
      }
    }

    // Generate (d+1)-dimensional clusters from d-dimensional clusters
    for (int d = 0; d < dimensionality - 1; d++) {
      if (stepprog != null) {
        stepprog.beginStep(d + 2, "Generate " + (d + 2) + "-dimensional clusters from " + (d + 1) + "-dimensional clusters.", LOG);
      }

      List<Subspace> subspaces = subspaceMap.get(d);
      if (subspaces == null || subspaces.isEmpty()) {
        if (stepprog != null) {
          for (int dim = d + 1; dim < dimensionality - 1; dim++) {
            stepprog.beginStep(dim + 2, "Generation of" + (dim + 2) + "-dimensional clusters not applicable, because no more " + (d + 2) + "-dimensional subspaces found.", LOG);
          }
        }
        break;
      }

      List<Subspace> candidates = generateSubspaceCandidates(subspaces);
      List<Subspace> s_d = new ArrayList<>();

      for (Subspace candidate : candidates) {
        Subspace bestSubspace = bestSubspace(subspaces, candidate, clusterMap);
        if (LOG.isDebuggingFine()) {
          LOG.debugFine("best subspace of " + candidate.dimensonsToString() + ": " + bestSubspace.dimensonsToString());
        }

        List<Cluster<Model>> bestSubspaceClusters = clusterMap.get(bestSubspace);
        List<Cluster<Model>> clusters = new ArrayList<>();
        for (Cluster<Model> cluster : bestSubspaceClusters) {
          List<Cluster<Model>> candidateClusters = runDBSCAN(relation, cluster.getIDs(), candidate, new SubspaceEuclideanDistanceFunction(new BitSet()));
          if (!candidateClusters.isEmpty()) {
            clusters.addAll(candidateClusters);
          }
        }

        if (LOG.isDebuggingFine()) {
          StringBuilder msg = new StringBuilder();
          msg.append(clusters.size() + " cluster(s) in subspace " + candidate + ": \n");
          for (Cluster<Model> c : clusters) {
            msg.append("      " + c.getIDs() + "\n");
          }
          LOG.debugFine(msg.toString());
        }

        if (!clusters.isEmpty()) {
          s_d.add(candidate);
          clusterMap.put(candidate, clusters);
        }
      }

      if (!s_d.isEmpty()) {
        subspaceMap.put(d + 1, s_d);
      }
    }

    result = new Clustering<>("GEOSUBCLU clustering", "geosubclu");
    
    for (Subspace subspace : clusterMap.descendingKeySet()) 
    {
      List<Cluster<Model>> clusters = clusterMap.get(subspace);
      
      for (Cluster<Model> cluster : clusters) 
      {
        Cluster<SubspaceModel<V>> newCluster = new Cluster<>(cluster.getIDs());
        newCluster.setModel(new SubspaceModel<>(subspace, Centroid.make(relation, cluster.getIDs()).toVector(relation)));
        //newCluster.setName("geocluster_" + numClusters++);
        String name = "c(";
        BitSet bs = subspace.getDimensions();
        
        int iteration = 1;
    	if(bs.cardinality()>1) {
    		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))  { 
    			//feature += FEATUREMAPPER.get(bs.nextSetBit(i)+1);
    			name+= FEATUREMAPPER.get(i);
    			if(iteration < bs.cardinality()) name += ",";
    			++iteration;
    		}
    	}
        else name += FEATUREMAPPER.get(bs.nextSetBit(0));
        
        name += ")";
        
        newCluster.setName(name);
        result.addToplevelCluster(newCluster);
        sbLog.append("\n"+newCluster.getName() + " has been generated from " + subspace.toString());
      }
    }
    return result;
  }

  /**
   * Returns the result of the algorithm.
   * 
   * @return the result of the algorithm
   */
  public Clustering<SubspaceModel<V>> getResult() {
    return result;
  }

  /**
   * Runs the DBSCAN algorithm on the specified partition of the database in the
   * given subspace. If parameter {@code ids} is null DBSCAN will be applied to
   * the whole database.
   * 
   * @param relation the database holding the objects to run DBSCAN on
   * @param ids the IDs of the database defining the partition to run DBSCAN on
   *        - if this parameter is null DBSCAN will be applied to the whole
   *        database
   * @param subspace the subspace to run DBSCAN on
 * @param firstSubspaceEuclideanDistanceFunction 
   * @return the clustering result of the DBSCAN run
   */
  private List<Cluster<Model>> runDBSCAN(
		  									Relation<V> relation, 
		  									DBIDs ids, 
		  									Subspace subspace, 
		  									SubspaceLPNormDistanceFunction dist
		  										
		  								) 
  {  
	BitSet bs = (BitSet) subspace.getDimensions().clone();
	
	String feature= "";
	int iteration = 1;
	if(bs.cardinality()>1) {
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))  { 
			//feature += FEATUREMAPPER.get(bs.nextSetBit(i)+1);
			feature += FEATUREMAPPER.get(i);
			if(iteration < bs.cardinality()) feature += " AND ";
			++iteration;
		}
	}
	else {
		feature = FEATUREMAPPER.get(bs.nextSetBit(0));
	}

	if (!DELTAD.containsKey(feature) ) 
		return new LinkedList<Cluster<Model>>();
	int minpts = DELTAD.get(feature).intValue();
	
	DoubleDistance epsilon = (EPS.containsKey(feature)) ? 
			new DoubleDistance (EPS.get(feature).doubleValue()) :
		    new DoubleDistance (epsValue);
	
	sbLog.append(bs.toString() + "." + feature + ".minpts=" + minpts+"\n");
	
	bs.set(0);
	bs.set(1);
	
	// distance function
    //distanceFunction.setSelectedDimensions(bs);
	dist.setSelectedDimensions(bs);
	
    ProxyDatabase proxy;
    if (ids == null) {
      // TODO: in this case, we might want to use an index - the proxy below
      // will prevent this!
      ids = relation.getDBIDs();
    }

    proxy = new ProxyDatabase(ids, relation);
    
    DBSCAN<V, DoubleDistance> dbscan = new DBSCAN<>(dist, epsilon, minpts);
    // run DBSCAN
    if (LOG.isVerbose()) {	
      LOG.verbose("\nRun DBSCAN on subspace " + subspace.dimensonsToString());
    }
    Clustering<Model> dbsres = dbscan.run(proxy);

    // separate cluster and noise
    List<Cluster<Model>> clusterAndNoise = dbsres.getAllClusters();
    List<Cluster<Model>> clusters = new ArrayList<>();
    for (Cluster<Model> c : clusterAndNoise) {
    	if (!c.isNoise()) {
    		DBIDs objects = c.getIDs();
    		if ( objects.size()>1 && objects.size()<DENSITY ) {
    			clusters.add(c);
    			//System.out.println("\tnumber of objects ci=" + objects.size());
    			sbLog.append("\tnumber of objects ci=" + objects.size()+"\n");
    		}
    	}
    }
    return clusters;
  }

  /**
   * Generates {@code d+1}-dimensional subspace candidates from the specified
   * {@code d}-dimensional subspaces.
   * 
   * @param subspaces the {@code d}-dimensional subspaces
   * @return the {@code d+1}-dimensional subspace candidates
   */
  private List<Subspace> generateSubspaceCandidates(List<Subspace> subspaces) {
    List<Subspace> candidates = new ArrayList<>();

    if (subspaces.isEmpty()) {
      return candidates;
    }

    // Generate (d+1)-dimensional candidate subspaces
    int d = subspaces.get(0).dimensionality();

    StringBuilder msgFine = new StringBuilder("\n");
    if (LOG.isDebuggingFiner()) {
      msgFine.append("subspaces ").append(subspaces).append('\n');
    }

    for (int i = 0; i < subspaces.size(); i++) {
      Subspace s1 = subspaces.get(i);
      for (int j = i + 1; j < subspaces.size(); j++) {
          Subspace s2 = subspaces.get(j);
          Subspace candidate = s1.join(s2);

          if (candidate != null) {
            if (LOG.isDebuggingFiner()) {
              msgFine.append("candidate: ").append(candidate.dimensonsToString()).append('\n');
            }
            // prune irrelevant candidate subspaces
            List<Subspace> lowerSubspaces = lowerSubspaces(candidate);
            if (LOG.isDebuggingFiner()) {
              msgFine.append("lowerSubspaces: ").append(lowerSubspaces).append('\n');
            }
            boolean irrelevantCandidate = false;
            for (Subspace s : lowerSubspaces) {
              if (!subspaces.contains(s)) {
                irrelevantCandidate = true;
                break;
              }
            }
            if (!irrelevantCandidate) {
              candidates.add(candidate);
            }
          }
        }
    }

    if (LOG.isDebuggingFiner()) {
      LOG.debugFiner(msgFine.toString());
    }
    if (LOG.isDebugging()) {
      StringBuilder msg = new StringBuilder();
      msg.append(d + 1).append("-dimensional candidate subspaces: ");
      for (Subspace candidate : candidates) {
        msg.append(candidate.dimensonsToString()).append(' ');
      }
      LOG.debug(msg.toString());
    }

    return candidates;
  }

  /**
   * Returns the list of all {@code (d-1)}-dimensional subspaces of the
   * specified {@code d}-dimensional subspace.
   * 
   * @param subspace the {@code d}-dimensional subspace
   * @return a list of all {@code (d-1)}-dimensional subspaces
   */
  private List<Subspace> lowerSubspaces(Subspace subspace) {
    int dimensionality = subspace.dimensionality();
    if (dimensionality <= 1) {
      return null;
    }

    // order result according to the dimensions
    List<Subspace> result = new ArrayList<>();
    BitSet dimensions = subspace.getDimensions();
    for (int dim = dimensions.nextSetBit(0); dim >= 0; dim = dimensions.nextSetBit(dim + 1)) {
      BitSet newDimensions = (BitSet) dimensions.clone();
      newDimensions.set(dim, false);
      result.add(new Subspace(newDimensions));
    }

    return result;
  }

  /**
   * Determines the {@code d}-dimensional subspace of the {@code (d+1)}
   * -dimensional candidate with minimal number of objects in the cluster.
   * 
   * @param subspaces the list of {@code d}-dimensional subspaces containing
   *        clusters
   * @param candidate the {@code (d+1)}-dimensional candidate subspace
   * @param clusterMap the mapping of subspaces to clusters
   * @return the {@code d}-dimensional subspace of the {@code (d+1)}
   *         -dimensional candidate with minimal number of objects in the
   *         cluster
   */
  private Subspace bestSubspace(List<Subspace> subspaces, Subspace candidate, TreeMap<Subspace, List<Cluster<Model>>> clusterMap) {
    Subspace bestSubspace = null;

    for (Subspace subspace : subspaces) {
      int min = Integer.MAX_VALUE;

      if (subspace.isSubspace(candidate)) {
        List<Cluster<Model>> clusters = clusterMap.get(subspace);
        for (Cluster<Model> cluster : clusters) {
          int clusterSize = cluster.size();
          if (clusterSize < min) {
            min = clusterSize;
            bestSubspace = subspace;
          }
        }
      }
    }

    return bestSubspace;
  }

  @Override
  public TypeInformation[] getInputTypeRestriction() {
    return TypeUtil.array(TypeUtil.NUMBER_VECTOR_FIELD);
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer<V extends NumberVector<?>> extends AbstractParameterizer {
    protected int minpts = 0;

    protected DoubleDistance epsilon = null;

    protected DimensionSelectingSubspaceDistanceFunction<V, DoubleDistance> distance = null;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      ObjectParameter<DimensionSelectingSubspaceDistanceFunction<V, DoubleDistance>> param = new ObjectParameter<>(DISTANCE_FUNCTION_ID, DimensionSelectingSubspaceDistanceFunction.class, SubspaceEuclideanDistanceFunction.class);
      if (config.grab(param)) {
        distance = param.instantiateClass(config);
      }
    }

    @Override
    protected GEOSUBCLU<V> makeInstance() {
      return new GEOSUBCLU<>(distance);
    }
  }
}
