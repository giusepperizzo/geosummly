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
@Description("Algorithm to detect arbitrarily shaped and positioned clusters in subspaces. SUBCLU delivers for each subspace the same clusters DBSCAN would have found, when applied to this subspace seperately.")
@Reference(authors = "Giuseppe Rizzo", title = "", booktitle = "")
public class GEOSUBCLU<V extends NumberVector<?>> extends AbstractAlgorithm<Clustering<SubspaceModel<V>>> implements SubspaceClusteringAlgorithm<SubspaceModel<V>> {
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
  private DimensionSelectingSubspaceDistanceFunction<V, DoubleDistance> distanceFunction;

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
    this.distanceFunction=distanceFunction;
    
    //init feature mapper
    /*FEATUREMAPPER = new HashMap<>();
    FEATUREMAPPER.put(2,"Arts & Entertainment");
    FEATUREMAPPER.put(3,"College & University");
    FEATUREMAPPER.put(4,"Event");
    FEATUREMAPPER.put(5,"Food");
    FEATUREMAPPER.put(6,"Nightlife Spot");
    FEATUREMAPPER.put(7,"Outdoors & Recreation");
    FEATUREMAPPER.put(8,"Professional & Other Places");
    FEATUREMAPPER.put(9,"Residence");
    FEATUREMAPPER.put(10,"Shop & Service");
    FEATUREMAPPER.put(11,"Travel & Transport");*/

  	/* 
  	 * 		init deltad
  	 */
    /*DELTAD = new HashMap<>();
    DELTAD.put("Arts & Entertainment",Math.floor(4.39827769041815));
    DELTAD.put("College & University", Math.floor(2.8435212153219));
    DELTAD.put("Food", Math.floor(28.9779728367082));
    DELTAD.put("Nightlife Spot", Math.floor(4.69751876400029));
    DELTAD.put("Outdoors & Recreation", Math.floor(7.88767295530474));
    DELTAD.put("Professional & Other Places", Math.floor(19.5187643664804));
    DELTAD.put("Residence", Math.floor(14.8529298768337));
    DELTAD.put("Shop & Service", Math.floor(22.5399897538332));
    DELTAD.put("Travel & Transport", Math.floor(10.4789984336736));*/
    
    // all combinations of pairs
    //DELTAD.put("Arts & Entertainment AND College & University", 20.0);
//    DELTAD.put("Arts & Entertainment AND Food", 202.0);
//    DELTAD.put("Arts & Entertainment AND Nightlife Spot", 39.0);
//    DELTAD.put("Arts & Entertainment AND Outdoors & Recreation", 57.0);
//    DELTAD.put("Arts & Entertainment AND Professional & Other Places", 137.0);
//    DELTAD.put("Arts & Entertainment AND Residence", 107.0);
//    DELTAD.put("Arts & Entertainment AND Shop & Service",159.0);
//    DELTAD.put("Arts & Entertainment AND Travel & Transport",78.0);
//    DELTAD.put("College & University AND Food", 238.0);
//    DELTAD.put("College & University AND Nightlife Spot", 47.0);
//    DELTAD.put("College & University AND Outdoors & Recreation", 68.0);
//    DELTAD.put("College & University AND Professional & Other Places", 161.0);
//    DELTAD.put("College & University AND Residence", 127.0);
//    DELTAD.put("College & University AND Shop & Service", 188.0);
//    DELTAD.put("College & University AND Travel & Transport", 92.0);
//    DELTAD.put("Food AND Nightlife Spot", 231.0);
//    DELTAD.put("Food AND Outdoors & Recreation", 337.0);
//    DELTAD.put("Nightlife Spot AND Outdoors & Recreation", 66.0);
//    DELTAD.put("Nightlife Spot AND Professional & Other Places", 158.0);
//    DELTAD.put("Nightlife Spot AND Residence", 124.0);
//    DELTAD.put("Nightlife Spot AND Shop & Service", 183.0);
//    DELTAD.put("Nightlife Spot AND Travel & Transport",90.0);
//    DELTAD.put("Outdoors & Recreation AND Professional & Other Places",	229.0);
//    DELTAD.put("Outdoors & Recreation AND Residence",179.0);
//    DELTAD.put("Outdoors & Recreation AND Shop & Service", 266.0);
//    DELTAD.put("Outdoors & Recreation AND Travel & Transport",131.0);
    
    /*
     * 	init eps
     */
    EPS = new HashMap<>();
  }
  
  public void setFeatureMapper(HashMap<Integer, String> featuremapper) {
	  this.FEATUREMAPPER=featuremapper;
  }
  
  public void setDeltad(HashMap<String, Double> deltad) {
	  this.DELTAD=deltad;
  }
  
  /**
   * Performs the SUBCLU algorithm on the given database.
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

    // list of 1-dimensional subspaces containing clusters
    List<Subspace> s_1 = new ArrayList<>();
    subspaceMap.put(0, s_1);

    // mapping of subspaces to list of clusters
    TreeMap<Subspace, List<Cluster<Model>>> clusterMap = new TreeMap<>(new Subspace.DimensionComparator());

    //the first two dimensions are used as seeds for each cluster generation
    for (int d = 2; d < dimensionality; d++) {
      
      Subspace currentSubspace = new Subspace(d);
      List<Cluster<Model>> clusters = runDBSCAN(relation, null, currentSubspace);

      if (LOG.isDebuggingFiner()) {
        StringBuilder msg = new StringBuilder();
        msg.append('\n').append(clusters.size()).append(" clusters in subspace ").append(currentSubspace.dimensonsToString()).append(": \n");
        for (Cluster<Model> cluster : clusters) {
          msg.append("      " + cluster.getIDs() + "\n");
        }
        LOG.debugFiner(msg.toString());
      }

      if (!clusters.isEmpty()) {
        s_1.add(currentSubspace);
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
          List<Cluster<Model>> candidateClusters = runDBSCAN(relation, cluster.getIDs(), candidate);
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

    // build result
    //int numClusters = 1;
    result = new Clustering<>("GEOSUBCLU clustering", "geosubclu");
    
//    StringBuilder sb = new StringBuilder();
//    List<String> dataset = new ArrayList<>(); 
//    Map<String,Vector<Integer>> sets = new HashMap<>();
//    List<Entry<String,Double>> SSEs = new LinkedList<>();
    
//    try {
//		String input_dataset = FileUtils.readFileToString(new File("/home/rizzo/Workspace/geosummly/output/evaluation/clustering correctness/density-transformation-matrix.csv"), "utf8");
//		String[] rows = input_dataset.split("\n");
//		for(String row : rows)
//			dataset.add(row);
//    } catch (IOException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	}
    
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
        System.out.println(newCluster.getName() + 
        		" has been generated from " + subspace.toString());
        
//        Vector<Integer> ids_sorted = new Vector<Integer>();
//        
//        for (DBIDIter iter = cluster.getIDs().iter(); iter.valid(); iter.advance()) 
//        {
//        	ids_sorted.add(DBIDUtil.asInteger(iter));
////            V o = relation.get(iter);
////            o.getValue(dimension);
//        }
//        Collections.sort(ids_sorted);        
//               
//        double sse = 0.0;
//        double sum_distance = 0.0;
//        int total_number = 0;
//        for (DBIDIter i1 = cluster.getIDs().iter(); i1.valid(); i1.advance()) 
//        {
//        	V o1 = relation.get(i1);
//        	for (DBIDIter i2 = cluster.getIDs().iter(); i2.valid(); i2.advance()) 
//            {
//        		V o2 = relation.get(i2);
//        		int dimension = o1.getDimensionality();
//        		double sum_squared = 0.0;
//        		for (int i=0; i<dimension; i++) {
//        			double d1 = o1.getValue(i).doubleValue();
//        			double d2 = o2.getValue(i).doubleValue();
//        			sum_squared += (d1-d2)*(d1-d2);
//        		}
//        		sum_distance += sum_squared;
//            	total_number++;
//            }
//        }
//        sse = sum_distance * 1/(2*total_number);
//        Entry<String,Double> entry = new AbstractMap.SimpleEntry<String,Double>(newCluster.getName(),sse);
//        SSEs.add(entry);	
//        
//        System.out.print("\t");
//        for (Integer id : ids_sorted) {
//        	System.out.print(id+" ");
//        	//sb.append(id + "," + newCluster.getName()+"\n");
//        	String record = dataset.get(id-1);
//        	String[] fields = record.split(",");
//        	sb.append(fields[0] + "," + fields[1] + "," + newCluster.getName()+"\n");
//        }
//        System.out.print("\n");
//        
//        if( !sets.containsKey(newCluster.getName()) )
//        	sets.put(newCluster.getName(), ids_sorted);
//        
//        else{
//        	Vector<Integer> temp = sets.get(newCluster.getName());
//        	sets.remove(newCluster.getName());
//        	temp.addAll(ids_sorted);
//        	Collections.sort(temp);
//        	sets.put(newCluster.getName(), temp);
//        }
      }
    }

    /*
     * 	output serialization 
     */
//    System.out.println("Aggregate Results");
//    for (Entry<String,Vector<Integer>> e :sets.entrySet()) {
//  	  System.out.print(e.getKey());
//  	  System.out.print(";");
//  	  for (Integer i : e.getValue()) 
//  		  System.out.print(i + " ");
//  	  System.out.print("\n");
//    }
//    
//    System.out.println();
//    System.out.println("SSE figures");
//    Double sse = 0.0;
//    for (Entry<String,Double> e : SSEs) {
//    	System.out.printf("SSE(%s)=%s\n", e.getKey(), e.getValue());
//    	sse += e.getValue(); 
//    }
//    System.out.printf("SSE_total=%s\n",sse);
//    
//    try {
//		FileUtils.writeStringToFile(new File("output.csv"), sb.toString());
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    
//    if (stepprog != null) {
//      stepprog.setCompleted(LOG);
//    }
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
   * @return the clustering result of the DBSCAN run
   */
  private List<Cluster<Model>> runDBSCAN(
		  									Relation<V> relation, 
		  									DBIDs ids, 
		  									Subspace subspace
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

	//if (DELTAD.containsKey(feature)) 

	if (!DELTAD.containsKey(feature) ) 
		return new LinkedList<Cluster<Model>>();
	int minpts = DELTAD.get(feature).intValue();
	
	DoubleDistance epsilon = (EPS.containsKey(feature)) ? 
			new DoubleDistance (EPS.get(feature).doubleValue()) :
		    new DoubleDistance (0.12);
			
	System.out.println(bs.toString() + "." + feature + ".minpts=" + minpts+";eps="+epsilon);
	
	bs.set(0);
	bs.set(1);
	
	// distance function
    distanceFunction.setSelectedDimensions(bs);

    ProxyDatabase proxy;
    if (ids == null) {
      // TODO: in this case, we might want to use an index - the proxy below
      // will prevent this!
      ids = relation.getDBIDs();
    }

    proxy = new ProxyDatabase(ids, relation);
    
    DBSCAN<V, DoubleDistance> dbscan = new DBSCAN<>(distanceFunction, epsilon, minpts);
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
    	System.out.println("\tnumber of objects ci=" + objects.size());
    	if ( objects.size()>1 && objects.size()<350 ) clusters.add(c);
    	//if ( objects.size()>1 ) clusters.add(c);
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

//      DistanceParameter<DoubleDistance> epsilonP = new DistanceParameter<>(EPSILON_ID, distance);
//      if (config.grab(epsilonP)) {
//        epsilon = epsilonP.getValue();
//      }
//
//      IntParameter minptsP = new IntParameter(MINPTS_ID);
//      minptsP.addConstraint(new GreaterConstraint(0));
//      if (config.grab(minptsP)) {
//        minpts = minptsP.getValue();
//      }
    }

    @Override
    protected GEOSUBCLU<V> makeInstance() {
      return new GEOSUBCLU<>(distance);
    }
  }
}
