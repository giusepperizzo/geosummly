package it.unito.geosummly.clustering.subspace;

import com.sun.org.apache.xpath.internal.operations.Mod;
import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.hierarchical.HierarchicalClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SubspaceClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.*;
import de.lmu.ifi.dbs.elki.database.ids.*;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Null;
import java.util.*;

/**
 * Created by PU Yang on 5/22/16.
 */

public class AGNES <M extends Model>
{

    //private ArrayList<Clustering<Model>> Clusterings;

    private Clustering<Model> Clustering;

    private int IterateNum;

    public AGNES (Clustering<Model> clustering, int threshold) {
        this.Clustering = clustering;
        this.IterateNum= threshold;
    }

    //Cluster Pair in Distance Map
    private class Pair {
        int Index_1;
        int Index_2;

        public Pair(int var1, int var2) {
            Index_2 = var2;
            Index_1 = var1;
        }

        public void setIndex(int var1, int var2) {
            Index_1 = var1;
            Index_2 = var2;
        }

        public boolean contains(int var) {
            if(Index_1 == var)
                return true;
            else if(Index_2 == var)
                return true;
            else
                return false;
        }

        public boolean equals(Pair obj) {
            if(Index_1 == obj.Index_1 && Index_2 == obj.Index_2)
                return true;
            else if(Index_1 == obj.Index_2 && Index_2 == obj.Index_1)
                return true;
            else
                return false;
        }

        public String toStirng() {
            return Index_1+","+Index_2;
        }

    }

    private class DistanceMatrix {
        private int MaxRow;

        private Vector<Vector<Double>> Matrix;

        public DistanceMatrix() {
            Matrix = new Vector<>();
        }

        public int getMaxRow() {
            return MaxRow;
        }

        public int getItemNum() {
            return Matrix.size();
        }

        public Pair findMin(){
            double min = 100;
            int index1 = 0, index2 = 0;
            for(int i = 0; i < MaxRow; i++) {
                int length = Matrix.get(i).size();
                for(int j = 0; j < length; j++) {
                    if(min > Matrix.get(i).get(j)) {
                        min = Matrix.get(i).get(j);
                        System.out.println("Min Value: " + min);
                        index1 = i;
                        index2 = j;
                    }
                }
            }
            System.out.println("Min Value: " + min);
            return new Pair(index1, index2);
        }

        public void removeOneItem(int index) {
            Iterator<Vector <Double>> it= Matrix.iterator();
            Vector<Double> row = null;
            while(it.hasNext()) {
                row = it.next();
                if(row.size() > index)
                    row.remove(index);
            }
            Matrix.remove(index);
            MaxRow = MaxRow - 1;
        }

        public void addOneItem(Vector<Double> vector) {
            Matrix.add(vector);
            MaxRow = MaxRow + 1;
        }

    }

    public Clustering<Model> run() {

        Clustering<Model> result = new Clustering<>("GEOSUBCLU+AGNES clustering", "geosubclu");

        ArrayList<Cluster<Model>> clusterList = new ArrayList<>(); //Keep the clusters for new clustering
        for(Cluster<Model> c:Clustering.getAllClusters()) {
            clusterList.add(c);
        }

        DistanceMatrix Matrix = new DistanceMatrix();
        System.out.println("Begin");
        System.out.println(Clustering.getAllClusters().size());
        //TODO 1. Build the distance Matrix for all the clusters

        int index1 = 0, numOfzero = 0;
        for (Cluster<?> cluster : Clustering.getAllClusters()) {
            MeanModel model_1 = (MeanModel)cluster.getModel();
            DoubleVector v1 = (DoubleVector) model_1.getMean();

            int index2 = 0;
            Vector<Double> row = new Vector<>();
            for (Cluster<?> cluster2 : Clustering.getAllClusters()) {
                MeanModel model_2 = (MeanModel) cluster2.getModel();
                DoubleVector v2 = (DoubleVector) model_2.getMean();
                    //Calculate the Euclidean Distance between two clusters
                    //may have problem
                BitSet bs = new BitSet();
                bs.set(0,2,true);
                double distance = new FirstSubspaceEuclideanDistanceFunction(bs).doubleDistance(v1, v2);

                if(index1 == index2 || cluster.getName().compareTo(cluster2.getName()) != 0) {
                    row.add(100.0);
                }
                else if (index1 > index2){
                    if (distance == 0)
                        numOfzero++;
                    row.add(distance);
                }
                else if(index1 < index2)
                    break;


                index2++;
            }
            Matrix.addOneItem(row);
            index1++;
        }

        System.out.println("Number of Zero: " + numOfzero);
        //TODO 2. Merge the two clusters who have the minimum distance over all pairs of clusters in current clustering
        //TODO 3. Update the Distance Matrix
        //TODO 4. If number of iteration reaches the threshold, then stop, otherwise go to step 2
        System.out.println("Main Part");
        for(int i = 0; i < this.IterateNum; i++) {
            //Find the minimum index and value in DistanceMatrix
            System.out.println("Round "+ i);

            Pair pair = Matrix.findMin();
            int index_1 = pair.Index_1, index_2 = pair.Index_2;
            System.out.println("Index_1, Index_2: " + index_1 +" " +index_2);

            //Merge clusters
            Cluster<Model> cluster_1= clusterList.get(index_1);
            Cluster<Model> cluster_2 = clusterList.get(index_2);
            if (!cluster_1.getName().equals(cluster_2.getName())) {
                System.out.println("***************Category is different***************");
//                continue;
            }
            MeanModel m_1 = (MeanModel) cluster_1.getModel();
            MeanModel m_2 = (MeanModel) cluster_2.getModel();

                //Merge DBIDs
            ArrayModifiableDBIDs IDs = DBIDUtil.newArray();
            IDs.addDBIDs(cluster_1.getIDs());
            IDs.addDBIDs(cluster_2.getIDs());
            int numOfCells_1 = cluster_1.size(), numOfCells_2 = cluster_2.size();
            double weight_1 = numOfCells_2/(numOfCells_1+numOfCells_2), weight_2 = numOfCells_2/(numOfCells_1+numOfCells_2);

                //Merge FeatureVector
            double [] v_1 = ((DoubleVector) m_1.getMean()).getValues();
            double [] v_2= ((DoubleVector) m_2.getMean()).getValues();
            double [] v_3 = new double [v_1.length];
            for(int j = 0; j < v_3.length; j++) {
                v_3[j] = v_1[j]*weight_1 + v_2[j]*weight_2;
            }

            DoubleVector v_new = new DoubleVector(v_3);
            MeanModel<DoubleVector> m_new = new MeanModel<>(v_new);

            String name = cluster_1.getName();
            Cluster<Model> cluster_new = new Cluster<Model>(name, IDs, false, m_new);
            System.out.println("Cluster's size: " + cluster_new.getIDs().size() + " " + cluster_new.size());
            //Update DistanceMatrix
                //Delete the merged cluster
            if(index_1 == index_2)
                System.out.println("WRONG!!!");

            clusterList.remove(index_1);
            clusterList.remove(index_2);

            Matrix.removeOneItem(index_1);
            Matrix.removeOneItem(index_2);

                //Add new cluster
            Vector<Double> item = new Vector<>();
            for(Cluster<?> cluster:clusterList) {
                MeanModel model_4 = (MeanModel) cluster.getModel();
                DoubleVector v_4 = (DoubleVector) model_4.getMean();
                BitSet bs = new BitSet();
                bs.set(0,2,true);
                double distance = new FirstSubspaceEuclideanDistanceFunction(bs).doubleDistance(v_new, v_4); //Compute the Distance
                item.add(distance);
                //DistanceMap.put(index_new+","+index_old, distance);

            }
            item.add(100.0);
            clusterList.add(cluster_new);
            Matrix.addOneItem(item);
        }
        System.out.println("Finish");
        for(Cluster<Model> cluster:clusterList) {
            result.addToplevelCluster(cluster);
        }
        System.out.println("Result Size: "+result.getAllClusters().size());
        return result;
    }

}
