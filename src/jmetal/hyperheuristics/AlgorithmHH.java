/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public abstract class AlgorithmHH extends Algorithm {

    protected String methodName;
    protected SolutionSet population;
    protected SolutionSet archive;
    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;
    protected int archiveSize;
    protected int populationSize;
    protected int maxEvaluations;
    protected int evaluations;
    protected int requiredEvaluations;
    protected Distance distance;
    protected QualityIndicator indicators;

    protected long estimatedTime;

    /**
     * Configure and init population.
     *
     * @throws jmetal.util.JMException
     * @throws java.lang.ClassNotFoundException
     */
    public abstract void executeMethod() throws JMException, ClassNotFoundException;

    /**
     * Configure and init population.
     */
    public abstract void initPopulation();

    public AlgorithmHH() {
        this.estimatedTime = 0;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    protected SolutionSet clonePopulation(SolutionSet pop) {
        return pop.union(new SolutionSet());
    }

    public SolutionSet getPopulation() {
        return this.clonePopulation(population);
    }

    public void setPopulation(SolutionSet population) {
        this.population = this.clonePopulation(population);
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public SolutionSet getArchive() {
        return this.clonePopulation(archive);
    }

    public void setArchive(SolutionSet archive) {
        this.archive = this.clonePopulation(archive);
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public void setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public int getRequiredEvaluations() {
        return requiredEvaluations;
    }

    public void setRequiredEvaluations(int requiredEvaluations) {
        this.requiredEvaluations = requiredEvaluations;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void incrementEstimatedTime() {
        this.estimatedTime++;
    }

    public void restartEstimatedTime() {
        if (this.estimatedTime > 0) {
            this.estimatedTime = 0;
        }
    }

    public SolutionSet getFullPopulation() {
        return this.getPopulation().union(this.getArchive());
    }

    public void setFullPopulation(SolutionSet fullpopulation) {
        SolutionSet pop = new SolutionSet(this.populationSize);
        SolutionSet arch = new SolutionSet(this.archiveSize);
        for (int i = 0; i < this.populationSize; i++) {
            pop.add(fullpopulation.get(i));
        }
        for (int i = this.populationSize; i < fullpopulation.size(); i++) {
            arch.add(fullpopulation.get(i));
        }

        this.setPopulation(pop);
        this.setArchive(arch);
    }
}
