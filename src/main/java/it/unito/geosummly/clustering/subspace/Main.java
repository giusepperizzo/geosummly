package it.unito.geosummly.clustering.subspace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SUBCLU;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.distance.distancefunction.strings.LevenshteinDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.IntegerDistance;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class Main {
    private Double SUBCLU_esp = 0.01;
    private int SUBCLU_minpts = 20;
    
    private Double DBSCAN_esp = 0.0005;
    private int DBSCAN_minpts = 100;
    
    private Double GEOSUBCLU_esp = 0.1;
    private int GEOSUBCLU_minpts = 5;
    
    public static void main(String[] args) 
    {
        Main main = new Main();
        
        // SortedMap of doubles where key is the row of the dataset,
        // while values are the values of each cell expressed in doub
        
        //Database db = main.makeSimpleDatabase("subspace-simple-1.csv", 1000, new ListParameterization(), null);       
        Database db = main.buildFromMatrix("data/sample.csv");
        //Database db = main.buildFromMatrix("sample-subclu.csv");
        
        Collection<Index> indexes = db.getIndexes();
        for (Index i : indexes) {
            System.out.println(i.getLongName());
        }    
        
        Clustering<?> result = main.runGEOSUBCLU(db);
                
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
          //System.out.println(i);
          //System.out.println(map.size());
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
    
    public Clustering<?> runGEOSUBCLU (Database db) 
    {
        ListParameterization params = new ListParameterization();
        //params.addParameter(GEOSUBCLU.EPSILON_ID, GEOSUBCLU_esp);
        //params.addParameter(GEOSUBCLU.MINPTS_ID, GEOSUBCLU_minpts);
        //params.addParameter(FixedDBIDsFilter.IDSTART_ID, 1);
        
        // setup algorithm
        GEOSUBCLU<DoubleVector> geosubclu = ClassGenericsUtil.parameterizeOrAbort(GEOSUBCLU.class, params);

        // run GEOSUBCLU on database
        Clustering<SubspaceModel<DoubleVector>> result = geosubclu.run(db);
        return result;
    }

    private <T> Database buildFromMatrix (String file) 
    {
        List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();       
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            br.readLine();
            while ((line = br.readLine())!=null) {
                ArrayList<Double> temp = new ArrayList<>();
                String[] tokens = line.split(",");
                for (int j=0; j < tokens.length; j++ ) {
                    temp.add(Double.parseDouble(tokens[j]));
                }
                matrix.add(temp);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
    
    
//    private <T> Database makeSimpleDatabase (  
//                                                    String filename, 
//                                                    int expectedSize, 
//                                                    ListParameterization params, 
//                                                    Class<?>[] filters
//                                                   ) 
//    {
//        params.addParameter(FileBasedDatabaseConnection.INPUT_ID, filename);
//
//        List<Class<?>> filterlist = new ArrayList<>();
//        filterlist.add(FixedDBIDsFilter.class);
//        if(filters != null) {
//          for(Class<?> filter : filters) {
//            filterlist.add(filter);
//          }
//        }
//        params.addParameter(FileBasedDatabaseConnection.FILTERS_ID, filterlist);
//        params.addParameter(FixedDBIDsFilter.IDSTART_ID, 1);
//      
//        Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
//
//        db.initialize();
//        Relation<?> rel = db.getRelation(TypeUtil.ANY);     
//       
//        System.out.println("size of the relations: " + rel.size());
//        
//        return db;
//      }
}
