/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public class RNI extends MetricHandler {

    @Override
    public double calculate(SolutionSet front, int numberOfObjectives) {
        double toReturn=0;
        SolutionSet workset=new SolutionSet(front.size());
        workset=workset.union(front);
        workset=this.removeDominadas(workset);
        workset=this.removeRepetidas(workset);
        
        if(front.size() > 0)
            toReturn= ((double)workset.size())/((double)front.size());
        return toReturn;
    }

    
}
