/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author vinicius
 */
public class ChoiceFunction extends HyperHeuristicSelector {

    protected double scalingFactor;
    
    public ChoiceFunction(double scalingFactor, ArrayList<AlgorithmHH> algs, int numObj) {
        super(algs,numObj);
        this.scalingFactor=scalingFactor;
    }
    
   
    

    @Override
    public AlgorithmHH chooseAlg() {
        //selecao aleatoria
        this.calcRanking();
        int[] frequencies =this.calcAllFrequency();
        double valueChosen=0;
        System.out.println("\n=================Choice Function=================");
        ArrayList<Integer> chosen=new ArrayList<>();
        for(int i=0; i < algs.size(); i++){
            AlgorithmHH alg=this.algs.get(i);
            AlgorithmRanking hypRanking = this.getMetric(alg, this.hypervolume); //RNI
            frequencies[i] = hypRanking.getRanking();//RNI
            double cFvalue=this.cFunction(alg, frequencies[i]);
            if(cFvalue > valueChosen){
                valueChosen=cFvalue;
                chosen.clear();
                chosen.add(i);
            }
            else if(cFvalue == valueChosen) {
                chosen.add(i);
            }
            System.out.print("Algoritimo " + alg.getMethodName());
            System.out.print("\tEstimated Time: "+alg.getEstimatedTime());
            System.out.print("\tHypervolume Last Value: " + this.getImprovementObj(alg, hypervolume).getLastValue());
            //System.out.print("\tHypervolume Average Value: " + this.getImprovementObj(alg, hypervolume).getAverage());
            System.out.println("\tRNI Last Value: " + this.getImprovementObj(alg, rni).getLastValue());
            //System.out.print("\tRNI Average Value: " + this.getImprovementObj(alg, rni).getAverage());
            //System.out.print("\tEpsilon Value: " + this.getMetricValue(alg, epsilon));
            System.out.print("\tChoice Value: " + cFvalue);
            System.out.println("\t\t\tHypervolume Rank: " + this.getMetric(alg, hypervolume).getRanking());
            System.out.println("\t\t\tRNI Rank: " + this.getMetric(alg, rni).getRanking());
            //System.out.print("\t\t\tEpsilon Rank: " + this.getMetricRank(alg, epsilon));
            //System.out.println("\t\t\tFrequency Rank: " + (frequencies[i]-this.getMetricRank(alg, rni)));
        }
        int pos;
        if(chosen.size()==1){
            pos=chosen.get(0);
        }
        else{
            Random gerador = new Random();
            pos=gerador.nextInt(chosen.size());
        }
        AlgorithmHH alg=this.algs.get(pos);
        System.out.print("Rodando " + alg.getMethodName());
        System.out.print("\tChoice Value: "+valueChosen);
        System.out.println("\n=================================================");
        return this.selectAlg(pos);
    }

    
    
    
    
    private double cFunction(AlgorithmHH alg, int frequency) {
        return this.f1(this.algs.size(), frequency)*this.scalingFactor + alg.getEstimatedTime(); //this.scalingFactor
    }

    private double f1(int N, int frequency) {
        return 2 * (N + 1) - frequency;
    }
}
