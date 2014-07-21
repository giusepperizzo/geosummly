package it.unito.geosummly.clustering.subspace;

import it.unito.geosummly.utils.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.QueryUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDMIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.HashSetModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.distance.DistanceDBIDList;
import de.lmu.ifi.dbs.elki.database.query.range.RangeQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.SubspaceLPNormDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.logging.progress.IndefiniteProgress;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DistanceParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

public class DBSCAN<O, D extends Distance<D>> extends AbstractDistanceBasedAlgorithm<O, D, Clustering<Model>> implements ClusteringAlgorithm<Clustering<Model>> {
	/**
	 * The logger for this class.
	 */
	private static final Logging LOG = Logging.getLogger(DBSCAN.class);

	/**
	 * Parameter to specify the maximum radius of the neighborhood to be
	 * considered, must be suitable to the distance function specified.
	 */
	public static final OptionID EPSILON_ID = new OptionID("dbscan.epsilon", "The maximum radius of the neighborhood to be considered.");

	/**
	 * Holds the value of {@link #EPSILON_ID}.
	 */
	private D epsilon;

	private ArrayList<Pair<Double, Double>> boundaries;

	/**
	 * Parameter to specify the threshold for minimum number of points in the
	 * epsilon-neighborhood of a point, must be an integer greater than 0.
	 */
	public static final OptionID MINPTS_ID = new OptionID("dbscan.minpts", "Threshold for minimum number of points in the epsilon-neighborhood of a point.");

	/**
	 * Holds the value of {@link #MINPTS_ID}.
	 */
	protected int minpts;

	/**
	 * Holds a list of clusters found.
	 */
	protected List<ModifiableDBIDs> resultList;

	/**
	 * Holds a set of noise.
	 */
	protected ModifiableDBIDs noise;

	/**
	 * Holds a set of processed ids.
	 */
	protected ModifiableDBIDs processedIDs;

	/**
	 * Constructor with parameters.
	 * 
	 * @param distanceFunction Distance function
	 * @param epsilon Epsilon value
	 * @param minpts Minpts parameter
	 */
	public DBSCAN(DistanceFunction<? super O, D> distanceFunction, D epsilon, int minpts) {
		super(distanceFunction);
		this.epsilon = epsilon;
		this.minpts = minpts;
	}

	/**
	 * Performs the DBSCAN algorithm on the given database.
	 */
	public Clustering<Model> run(Relation<O> relation) 
	{
		RangeQuery<O, D> rangeQuery = QueryUtil.getRangeQuery(relation, getDistanceFunction());

		int size = relation.size();
		//final int size = getDensity(null, relation, null, (FirstSubspaceEuclideanDistanceFunction) getDistanceFunction());

		FiniteProgress objprog = LOG.isVerbose() ? new FiniteProgress("Processing objects", size, LOG) : null;
		IndefiniteProgress clusprog = LOG.isVerbose() ? new IndefiniteProgress("Number of clusters", LOG) : null;
		resultList = new ArrayList<>();
		noise = DBIDUtil.newHashSet();
		processedIDs = DBIDUtil.newHashSet(size);
		if(size < minpts) {
			// The can't be any clusters
			noise.addDBIDs(relation.getDBIDs());
			//objprog.setProcessed(noise.size(), LOG); //FIXME raises a bug
		}
		else {
			for(DBIDIter iditer = relation.iterDBIDs(); iditer.valid(); iditer.advance()) {
				if(!processedIDs.contains(iditer)) {
					expandCluster(relation, rangeQuery, iditer, objprog, clusprog);
				}
				if(objprog != null && clusprog != null) {
					objprog.setProcessed(processedIDs.size(), LOG);
					clusprog.setProcessed(resultList.size(), LOG);
				}
				if(processedIDs.size() == size) {
					break;
				}
			}
		}
		// Finish progress logging
		if(objprog != null) {
			objprog.ensureCompleted(LOG);
		}
		if(clusprog != null) {
			clusprog.setCompleted(LOG);
		}

		Clustering<Model> result = new Clustering<>("DBSCAN Clustering", "dbscan-clustering");
		for(ModifiableDBIDs res : resultList) {
			Cluster<Model> c = new Cluster<Model>(res, ClusterModel.CLUSTER);
			result.addToplevelCluster(c);
		}

		Cluster<Model> n = new Cluster<Model>(noise, true, ClusterModel.CLUSTER);
		result.addToplevelCluster(n);

		return result;
	}

	/**
	 * DBSCAN-function expandCluster.
	 * <p/>
	 * Border-Objects become members of the first possible cluster.
	 * 
	 * @param relation Database relation to run on
	 * @param rangeQuery Range query to use
	 * @param startObjectID potential seed of a new potential cluster
	 * @param objprog the progress object for logging the current status
	 */
	protected void expandCluster(
			Relation<O> relation, 
			RangeQuery<O, D> rangeQuery, 
			DBIDRef startObjectID, 
			FiniteProgress objprog, 
			IndefiniteProgress clusprog
			) 
	{
		DistanceDBIDList<D> neighbors = rangeQuery.getRangeForDBID(startObjectID, epsilon);

		//int size = neighbors.size(); 
		int size = getDensity(relation, neighbors, (SubspaceLPNormDistanceFunction) getDistanceFunction());

		// startObject is no core-object
		// if(neighbors.size() < minpts) {
		if( size <= minpts ) {
			noise.add(startObjectID);
			processedIDs.add(startObjectID);
			if(objprog != null && clusprog != null) {
				objprog.setProcessed(processedIDs.size(), LOG);
				clusprog.setProcessed(resultList.size(), LOG);
			}
			return;
		}

		// try to expand the cluster
		HashSetModifiableDBIDs seeds = DBIDUtil.newHashSet();
		ModifiableDBIDs currentCluster = DBIDUtil.newArray();
		
		// this allows to consider single objects as cluster items
		if( neighbors.size() == 0 ) currentCluster.add(startObjectID);
		
		for(DBIDIter seed = neighbors.iter(); seed.valid(); seed.advance()) {
			if(!processedIDs.contains(seed)) {
				currentCluster.add(seed);
				processedIDs.add(seed);
				seeds.add(seed);
			}
			else if(noise.contains(seed)) {
				currentCluster.add(seed);
				noise.remove(seed);
			}
		}
		seeds.remove(startObjectID);

		while(seeds.size() > 0) {
			DBIDMIter o = seeds.iter();
			DistanceDBIDList<D> neighborhood = rangeQuery.getRangeForDBID(o, epsilon);
			o.remove();

			size = getDensity(relation, neighbors, (SubspaceLPNormDistanceFunction) getDistanceFunction());
			//if(neighborhood.size() >= minpts) {
				if( size > minpts ) {
					for(DBIDIter neighbor = neighborhood.iter(); neighbor.valid(); neighbor.advance()) {
						boolean inNoise = noise.contains(neighbor);
						boolean unclassified = !processedIDs.contains(neighbor);
						if(inNoise || unclassified) {
							if(unclassified) {
								seeds.add(neighbor);
							}
							currentCluster.add(neighbor);
							processedIDs.add(neighbor);
							if(inNoise) {
								noise.remove(neighbor);
							}
						}
					}
				}

				if(processedIDs.size() == relation.size() && noise.size() == 0) {
					break;
				}

				if(objprog != null && clusprog != null) {
					objprog.setProcessed(processedIDs.size(), LOG);
					int numClusters = currentCluster.size() > minpts ? resultList.size() + 1 : resultList.size();
					clusprog.setProcessed(numClusters, LOG);
				}
		}
		
		size = getDensity(relation, currentCluster, (SubspaceLPNormDistanceFunction) getDistanceFunction());

		//if(currentCluster.size() >= minpts) {
		if( size > minpts) {
			resultList.add(currentCluster);
		}
		else {
			noise.addDBIDs(currentCluster);
			noise.add(startObjectID);
			processedIDs.add(startObjectID);
		}
	}
	
	private int getDensity(  
			Relation<O> relation, 
			DBIDs ids,
			SubspaceLPNormDistanceFunction distanceFunction
			)  
	{
		
		Relation<DoubleVector> vectors = relation.getDatabase().getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
		BitSet bs = (BitSet) distanceFunction.getSelectedDimensions().clone();

		// not consider lat and lng
		bs.set(0, 2, false);
		
		// consider the density of the core element
		Double density = 0.0;
		// then the cardinality of the set is 1
		int cardinality = 0;

		//relation contains also the startObject
		if (relation != null) 
		{	
			for(DBIDIter seed = ids.iter(); seed.valid(); seed.advance()) 
			{	    	
				Double cellDensity = 1.0;   	
				for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))  
				{
					double dn = vectors.get(seed).doubleValue(i);
					double dmax = boundaries.get(i).getSecond();
					double dmin = boundaries.get(i).getFirst();
					double d = dn * (dmax - dmin) + dmin;
					cellDensity *= d;
				}
				density += cellDensity;
				cardinality ++;
			}  	
				
		}
		return (int) Math.floor(density/cardinality); //(int) (density/cardinality);
	}

	@Override
	public TypeInformation[] getInputTypeRestriction() {
		return TypeUtil.array(getDistanceFunction().getInputTypeRestriction());
	}

	@Override
	protected Logging getLogger() {
		return LOG;
	}

	public ArrayList<Pair<Double, Double>> getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(ArrayList<Pair<Double, Double>> boundaries) {
		this.boundaries = boundaries;
	}

	public static class Parameterizer<O, D extends Distance<D>> extends AbstractDistanceBasedAlgorithm.Parameterizer<O, D> 
	{
		protected D epsilon = null;

		protected int minpts = 0;

		@Override
		protected void makeOptions(Parameterization config) {
			super.makeOptions(config);
			DistanceParameter<D> epsilonP = new DistanceParameter<>(EPSILON_ID, distanceFunction);
			if(config.grab(epsilonP)) {
				epsilon = epsilonP.getValue();
			}

			IntParameter minptsP = new IntParameter(MINPTS_ID);
			minptsP.addConstraint(new GreaterConstraint(0));
			if(config.grab(minptsP)) {
				minpts = minptsP.getValue();
			}
		}

		@Override
		protected DBSCAN<O, D> makeInstance() {
			return new DBSCAN<>(distanceFunction, epsilon, minpts);
		}
	}
}