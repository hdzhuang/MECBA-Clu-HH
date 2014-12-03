/**
 * SPEA2.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.metaheuristics.spea2;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.*;
import jmetal.hyperheuristics.AlgorithmHH;
import jmetal.problems.CITO_CAITO;
import jmetal.util.*;

/**
 * This class representing the SPEA2 algorithm
 */
public class SPEA2 extends AlgorithmHH {

    /**
     * Defines the number of tournaments for creating the mating pool
     */
    public static final int TOURNAMENTS_ROUNDS = 1;

    /**
     * Stores the problem to solve
     */
    private CITO_CAITO problem_;

    /**
     * Constructor. Create a new SPEA2 instance
     *
     * @param problem Problem to solve
     */
    public SPEA2(CITO_CAITO problem) {
        this.problem_ = problem;
        this.methodName = "Spea2";
    } // Spea2

    /**
     * Runs of the Spea2 algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     */
    @Override
    public SolutionSet execute() throws JMException {
        this.initPopulation();
        while (evaluations < maxEvaluations) {
            this.executeMethod();
        } // while
        Ranking ranking = new Ranking(archive);
        return ranking.getSubfront(0);
    } // execute    

    @Override
    public void executeMethod() throws JMException {
        SolutionSet offSpringSolutionSet;
        SolutionSet union = ((SolutionSet) population).union(archive);
        Spea2Fitness spea = new Spea2Fitness(union);
        spea.fitnessAssign();
        archive = spea.environmentalSelection(archiveSize);
        // Create a new offspringPopulation
        offSpringSolutionSet = new SolutionSet(populationSize);
        Solution[] parents = new Solution[2];
        while (offSpringSolutionSet.size() < populationSize) {
            int j = 0;
            do {
                j++;
                parents[0] = (Solution) selectionOperator.execute(archive, null);
            } while (j < SPEA2.TOURNAMENTS_ROUNDS); // do-while                    
            int k = 0;
            do {
                k++;
                parents[1] = (Solution) selectionOperator.execute(archive, null);
            } while (k < SPEA2.TOURNAMENTS_ROUNDS); // do-while

            //make the crossover
            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents, problem_);

            for (int l = 0; l < offSpring.length; l++) {
                mutationOperator.execute(offSpring[l], problem_);

                problem_.evaluate(offSpring[l]);
                problem_.evaluateConstraints(offSpring[l]);

                offSpringSolutionSet.add(offSpring[l]);
            }

            evaluations += offSpring.length;

//        mutationOperator.execute(offSpring[0], problem_);
//        problem_.evaluate(offSpring[0]);
//        problem_.evaluateConstraints(offSpring[0]);
//        offSpringSolutionSet.add(offSpring[0]);
//        evaluations++;
        } // while
        // End Create a offSpring population
        population = offSpringSolutionSet;
    }

    @Override
    public void initPopulation() {
        //Read the params
        populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

        //Read the operators
        crossoverOperator = operators_.get("crossover");
        mutationOperator = operators_.get("mutation");
        selectionOperator = operators_.get("selection");

        //Initialize the variables
        population = new SolutionSet(populationSize);
        archive = new SolutionSet(archiveSize);
        evaluations = 0;

        //-> Create the initial population
        Solution newSolution;
        for (int i = 0; i < populationSize; i++) {
            try {
                newSolution = new Solution(problem_);
                problem_.evaluate(newSolution);
                problem_.evaluateConstraints(newSolution);
                evaluations++;
                population.add(newSolution);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
} // Spea2
