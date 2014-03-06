package it.unito.geosummly;

import it.unito.geosummly.clustering.subspace.GEOSUBCLU;
import it.unito.geosummly.clustering.subspace.InMemoryDatabase;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SUBCLU;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class ClusteringOperator {
	
	public static Logger logger = Logger.getLogger(ClusteringOperator.class.toString());
	
	private Double SUBCLU_esp = 0.01;
    private int SUBCLU_minpts = 20;
	
    public void execute(String inNorm, String inDeltad, String inSingles, String out, String method) throws IOException {
    	
    	//Read normalized csv file
		FileReader readerNorm =new FileReader(inNorm);
    	CSVParser parserNorm = new CSVParser(readerNorm, CSVFormat.EXCEL);
		List<CSVRecord> listNorm = parserNorm.getRecords();
		parserNorm.close();
		
		//fill in the matrix of normalized values
		ArrayList<ArrayList<Double>> normMatrix=new ArrayList<ArrayList<Double>>();
		for(CSVRecord r: listNorm) {
			//we exclude the header
			if(!r.get(0).contains("timestamp")) {
				ArrayList<Double> record=new ArrayList<Double>();
				//we don't have to consider timepstamp values, so i=1
				for(int i=1;i<r.size();i++)
					record.add(Double.parseDouble(r.get(i)));
				normMatrix.add(record);
			}
		}
    	
    	//build the database from the normalized matrix
		Database db=buildFromMatrix(normMatrix);
    	
    	//Read deltad csv file
		FileReader readerDeltad =new FileReader(inDeltad);
    	CSVParser parserDeltad = new CSVParser(readerDeltad, CSVFormat.EXCEL);
		List<CSVRecord> listDeltad = parserDeltad.getRecords();
		parserDeltad.close();
		
		//fill in the hashmaps and get the value only if it's greater than 0
		HashMap<Integer, String> featuresMap=new HashMap<Integer, String>();
	    HashMap<String, Double> deltadMap=new HashMap<String, Double>();
		for(CSVRecord r: listDeltad) {
			double d=Math.floor(Double.parseDouble(r.get(1))); //floor of deltad value
			if(d > 0) {
				int mSize=featuresMap.size();
				String feature=(String) r.get(0).replace("deltad", "").replaceAll("\\(", "").replaceAll("\\)", ""); //take only feature name
				featuresMap.put(mSize+2, feature);
				deltadMap.put(feature, d);
			}
		}
        
        Collection<Index> indexes = db.getIndexes();
        for (Index i : indexes) {
            System.out.println(i.getLongName());
        }    
        
        Clustering<?> result = runGEOSUBCLU(db, featuresMap, deltadMap);
                
        //we do not really need Outliers, since the definition is given here http://elki.dbs.ifi.lmu.de/wiki/Tutorial/Outlier
        ArrayList<OutlierResult> ors = ResultUtil.filterResults(result, OutlierResult.class);
        System.out.print("outlier:");
        for (OutlierResult o : ors) {
            Relation<Double> scores = o.getScores();
            for (DBIDIter iter = scores.iterDBIDs(); iter.valid(); iter.advance()) {
                System.out.println(DBIDUtil.toString(iter) + " " + scores.get(iter));
            }
        }
        
        System.out.println("\nclusters:");
        ArrayList<Clustering<?>> cs = ResultUtil.filterResults(result, Clustering.class);
        int j = 0;
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
        }
        
        System.out.println("--------------------------------------------------");
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
            
        }        
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
        Relation<?> rel = db.getRelation(TypeUtil.ANY);     
                
        //System.out.println("size of the relations: " + rel.size());
        return db;
    }
}
