package it.unito.geosummly;

import it.unito.geosummly.clustering.subspace.GEOSUBCLU;
import it.unito.geosummly.clustering.subspace.InMemoryDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;

import java.util.HashMap;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SUBCLU;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class ClusteringOperator {

	public static Logger logger = Logger.getLogger(ClusteringOperator.class.toString());

	private Double SUBCLU_esp = 0.01;
    private int SUBCLU_minpts = 20;

    public void execute(String inDens, String inNorm, String inDeltad, String inSingles, String out, String method) throws IOException {
    	
    	//Read all the csv files
    	CSVDataIO dataIO=new CSVDataIO();
    	List<CSVRecord> listDens=dataIO.readCSVFile(inDens);
    	List<CSVRecord> listNorm=dataIO.readCSVFile(inNorm);
    	List<CSVRecord> listDeltad=dataIO.readCSVFile(inDeltad);
    	List<CSVRecord> listSingles=dataIO.readCSVFile(inSingles);

		//fill in the matrix of normalized values
		ArrayList<ArrayList<Double>> normMatrix=new ArrayList<ArrayList<Double>>();
		for(CSVRecord r: listNorm) {
			//we exclude the header
			if(!r.get(0).contains("Timestamp")) {
				ArrayList<Double> record=new ArrayList<Double>();
				//we don't have to consider timepstamp values, so i=1
				for(int i=1;i<r.size();i++)
					record.add(Double.parseDouble(r.get(i)));
				normMatrix.add(record);
			}
		}
    	
    	//build the database from the normalized matrix
		Database db=buildFromMatrix(normMatrix);

		//fill in the hashmaps and get the value only if it's greater than 0 and less than cells number
		HashMap<Integer, String> featuresMap=new HashMap<Integer, String>();
	    HashMap<String, Double> deltadMap=new HashMap<String, Double>();
		for(CSVRecord r: listDeltad) {
			double d=Math.floor(Double.parseDouble(r.get(1))); //floor of deltad value
			if((d > 0) && (d < listNorm.size()-1)) {
				int mSize=featuresMap.size();
				String feature=(String) r.get(0).replace("deltad", "").replaceAll("\\(", "").replaceAll("\\)", ""); //take only feature name
				featuresMap.put(mSize+2, feature);
				deltadMap.put(feature, d);
			}
		}
        
        /*Collection<Index> indexes = db.getIndexes();
        for (Index i : indexes) {
            System.out.println(i.getLongName());
        }*/
        
        Clustering<?> result = runGEOSUBCLU(db, featuresMap, deltadMap);
                
        //we do not really need Outliers, since the definition is given here http://elki.dbs.ifi.lmu.de/wiki/Tutorial/Outlier
        /*ArrayList<OutlierResult> ors = ResultUtil.filterResults(result, OutlierResult.class);
        System.out.print("outlier:");
        for (OutlierResult o : ors) {
            Relation<Double> scores = o.getScores();
            for (DBIDIter iter = scores.iterDBIDs(); iter.valid(); iter.advance()) {
                System.out.println(DBIDUtil.toString(iter) + " " + scores.get(iter));
            }
        }
        System.out.println();*/
        
        ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
        HashMap<Integer, String> clustersName=new HashMap<Integer, String>(); //key, cluster name
        HashMap<Integer, ArrayList<Integer>> cellsOfCluster=new HashMap<Integer, ArrayList<Integer>>(); //key, cell_ids 
        HashMap<Integer, ArrayList<String>> venuesOfCell=new HashMap<Integer, ArrayList<String>>(); //cell_id, venue_ids
        
        for(Clustering<?> c: cs) {
        	//get all the clusters
        	for(Cluster<?> cluster: c.getAllClusters()) {
        		int index=clustersName.size();
        		//put the cluster name in the map
        		clustersName.put(index, cluster.getName());
        		ArrayList<Integer> cells=new ArrayList<Integer>();
        		//get all the cell_ids for the selected cluster 
        		for(DBIDIter iter=cluster.getIDs().iter(); iter.valid(); iter.advance()) {
        			int cellId=Integer.parseInt(DBIDUtil.toString(iter));
        			cells.add(cellId);
        			ArrayList<String> venueIdRec=new ArrayList<String>();
        			boolean found=false;
        			boolean added=false;
        			//get all the single venues for the selected cell
        			for(int i=0;i<listSingles.size() && !found;i++) {
        				CSVRecord r=listSingles.get(i); //venue information
        				//we don't have to consider the header
        				if(!r.get(0).contains("Timestamp")) {
        					String lat=listDens.get(cellId).get(1); //focal latitude
        					String lng=listDens.get(cellId).get(2); //focal longitude
        					//check if the venue belong to the cell
        					if(r.get(5).equals(lat) && r.get(6).equals(lng)) {
        						venueIdRec.add(r.get(2)); //add the id_venue
        						added=true;
        					} else if(added) found=true; //since venues of the same cell are consecutive, we stop the loop once we found different coordinate values
        				}
        			}
        			venuesOfCell.put(cellId, venueIdRec);
        		}
        		cellsOfCluster.put(index, cells);
        	}
        }
        
        System.out.println("\nclusters:");
        for(int i=0;i<cellsOfCluster.size(); i++) {
        	ArrayList<Integer> i_rec=cellsOfCluster.get(i);
        	for(Integer integer: i_rec)
        		System.out.print(integer+" ");
        	System.out.println();
        }
        
        Set<Integer> keys=venuesOfCell.keySet();
        for(Integer i: keys) {
        	System.out.println("\nVenues for cell "+i+":");
        	for(String s: venuesOfCell.get(i))
        		System.out.print(s+" ");
        	System.out.println();
        }

        /*int j = 0;
        HashMap<Integer, Integer> map = new HashMap<>();
        for (Clustering<?> c : cs) {
          for (Cluster<?> cluster : c.getAllClusters()) 
          {
            for (DBIDIter iter = cluster.getIDs().iter(); iter.valid(); iter.advance()) 
            {
              System.out.print(DBIDUtil.toString(iter)+" ");
              ++j;
              map.put(Integer.parseInt(DBIDUtil.toString(iter)), 0);
            }
            System.out.println();
          }
        }*/
        
        /*System.out.println("--------------------------------------------------");
        Integer i = 0;
        String[] labels = new String[ db.getRelation(TypeUtil.ANY).size() ];
        for (Clustering<?> c : cs) 
            for (Cluster<?> cluster : c.getAllClusters()) {
                if(i != 0) {
                    for (DBIDIter iter = cluster.getIDs().iter(); iter.valid(); iter.advance()) {
                        //System.out.println(DBIDUtil.asInteger(iter));
                        if (labels[ DBIDUtil.asInteger(iter) - 1 ] == null)     
                            labels[ DBIDUtil.asInteger(iter) - 1 ] = "Label".concat(i.toString()) ;
                    }
                }
                ++i;
            }
        
        for (i = 0; i<labels.length;  i++) {
            //System.out.println(new Integer(i+1).toString().concat(" ").concat(labels[i]));
            System.out.println( (labels[i]== null ) ? "0" : labels[i]);
        }
        
        List<Clustering<? extends Model>> clusterresults =
                                        ResultUtil.getClusteringResults(result);
        for (Clustering<?> c : clusterresults){
            
        }*/     
    }
    
    /**Set SUBCLU parameters and run the algorithm*/
    public Clustering<?> runSUBCLU (Database db) 
    {
        ListParameterization params = new ListParameterization();
        params.addParameter(SUBCLU.EPSILON_ID, SUBCLU_esp);
        params.addParameter(SUBCLU.MINPTS_ID, SUBCLU_minpts);
        
        // setup algorithm
        SUBCLU<DoubleVector> subclu = ClassGenericsUtil.parameterizeOrAbort(SUBCLU.class, params);

        // run SUBCLU on database
        Clustering<SubspaceModel<DoubleVector>> result = subclu.run(db);
        return result;
    }
    
    /**Set GEOSUBCLU parameters and run the algorithm*/
    public Clustering<?> runGEOSUBCLU (Database db, HashMap<Integer, String> map, HashMap<String, Double>deltad) 
    {
        ListParameterization params = new ListParameterization();
        
        // setup algorithm
        GEOSUBCLU<DoubleVector> geosubclu = ClassGenericsUtil.parameterizeOrAbort(GEOSUBCLU.class, params);
        geosubclu.setFeatureMapper(map);
        geosubclu.setDeltad(deltad);

        // run GEOSUBCLU on database
        Clustering<SubspaceModel<DoubleVector>> result = geosubclu.run(db);
        return result;
    }
    
    /**Build a Database from the matrix of normalized density values*/
    private <T> Database buildFromMatrix (ArrayList<ArrayList<Double>> matrix) 
    {       
        double[][] data = new double[matrix.size()][];
        for (int i=0; i<matrix.size(); i++) {
            data[i] = new double[matrix.get(i).size()];
            for(int j=0; j<matrix.get(i).size(); j++) {
                data[i][j] = (matrix.get(i)).get(j);
            }
        }
        
        List<Class<?>> filterlist = new ArrayList<>();
        filterlist.add(FixedDBIDsFilter.class);
        
        Database db = new InMemoryDatabase(new ArrayAdapterDatabaseConnection(data), null);
                        
        db.initialize();
        //Relation<?> rel = db.getRelation(TypeUtil.ANY);     
                
        //System.out.println("size of the relations: " + rel.size());
        return db;
    }
}