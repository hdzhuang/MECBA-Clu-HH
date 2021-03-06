package jmetal.base;

import java.util.ArrayList;
import jmetal.base.variable.Permutation;

public class Cluster {

    public int id;
    public ArrayList<Integer> modules;

    //--------------------------------------------------------------------------
    public Cluster() {
        modules = new ArrayList<Integer>();
    }

    //--------------------------------------------------------------------------
    public static Cluster getCluster(Permutation solution, int clusterId){
        for (int i = 0; i < solution.clusters_.size(); i++) {
            if (solution.clusters_.get(i).id == clusterId) {
                return solution.clusters_.get(i);
            }
        }

        return null;
    }

    //--------------------------------------------------------------------------
    public static int getClusterId(ArrayList<Cluster> clusters_, int clusterId){
        for (int i = 0; i < clusters_.size(); i++) {
            if(clusters_.get(i).modules.indexOf(clusterId) > -1){
                return clusters_.get(i).id;
            }
        }

        return -1;
    }


    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        String string;
        string = "(id="+id+") ";

        for (int j = 0; j < modules.size(); j++) {
            string += modules.get(j) + " ";
        }

        return string;
    }

    //--------------------------------------------------------------------------
}
