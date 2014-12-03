/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Operator;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.problems.CITO_CAITO;
import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public class HeuristicBuilder {

    protected CITO_CAITO problem;
    protected double crossoverProbability;
    protected double mutationProbability;
    protected int populationSize;
    protected int maxEvaluations;
    protected int archiveSize;

    protected ArrayList<AlgorithmHH> algs;

    public HeuristicBuilder(CITO_CAITO problem, double crossoverProbability, double mutationProbability, int populationSize, int maxEvaluations, int archiveSize) {
        this.problem = problem;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.archiveSize = archiveSize;
        this.algs = new ArrayList<>();
    }

    private AlgorithmHH createAlg(AlgorithmHH algorithm)throws JMException {

        Operator crossover;
        Operator mutation;
        Operator selection;
        // Crossover
        crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover");
        crossover.setParameter("probability", crossoverProbability);
        // Mutation
        mutation = MutationFactory.getMutationOperator("SwapMutation");
        mutation.setParameter("probability", mutationProbability);
        // Selection
        selection = SelectionFactory.getSelectionOperator("BinaryTournament");

        // Algorithm params
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);
        algorithm.setInputParameter("archiveSize", archiveSize);//just to keep standard
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
        algorithm.initPopulation();
        return (AlgorithmHH) algorithm;
    }

    protected AlgorithmHH createIBEA() throws JMException {
        AlgorithmHH algorithm = new IBEA(problem);
        return this.createAlg(algorithm);
    }

    protected AlgorithmHH createNSGAII() throws JMException {
        AlgorithmHH algorithm = new NSGAII(problem);
        return this.createAlg(algorithm);
    }

    protected AlgorithmHH createSPEA2() throws JMException {
        AlgorithmHH algorithm = new SPEA2(problem);
        return this.createAlg(algorithm);
    }

    public void initAlgs() {
        this.algs = new ArrayList<>();
        try {
            this.algs.add(this.createNSGAII());
            this.algs.add(this.createIBEA());
            this.algs.add(this.createSPEA2());
        } catch (JMException ex) {
            Logger.getLogger(HeuristicBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setSamePopulation();
    }

    private void setSamePopulation() {
        if (this.algs.size() > 0) {
            Random gerador = new Random();
            int pos = gerador.nextInt(this.algs.size());
            AlgorithmHH chosen = this.algs.get(pos);
            for (AlgorithmHH alg : this.algs) {
                alg.setFullPopulation(chosen.getFullPopulation());
            }
        }
    }

    public ArrayList<AlgorithmHH> getAlgs() {
        return algs;
    }

}
