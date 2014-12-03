/**
 * NSGAII.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.metaheuristics.nsgaII;

import jmetal.hyperheuristics.AlgorithmHH;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.*;
import jmetal.problems.CITO_CAITO;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.*;

/**
 * This class implements the NSGA-II algorithm.
 */
public class NSGAII extends AlgorithmHH {

    /**
     * stores the problem to solve
     */
    private CITO_CAITO problem_;

    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public NSGAII(CITO_CAITO problem) {
        this.problem_ = problem;
        this.methodName = "NSGAII";
    } // NSGAII

    private void processOffspring(SolutionSet offspringPopulation) {
        SolutionSet union;
        // Create the solutionSet union of solutionSet and offSpring
        union = ((SolutionSet) population).union(offspringPopulation);

        // Ranking the union
        Ranking ranking = new Ranking(union);

        int remain = populationSize;
        int index = 0;
        SolutionSet front = null;
        population.clear();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
            //Assign crowding distance to individuals
            distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            //Add the individuals of this front
            for (int k = 0; k < front.size(); k++) {
                population.add(front.get(k));
            } // for

            //Decrement remain
            remain = remain - front.size();

            //Obtain the next front
            index++;
            if (remain > 0) {
                front = ranking.getSubfront(index);
            } // if
        } // while

        // Remain is less than front(index).size, insert only the best one
        if (remain > 0) {  // front contains individuals to insert
            distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            front.sort(new jmetal.base.operator.comparator.CrowdingComparator());
            for (int k = 0; k < remain; k++) {
                population.add(front.get(k));
            } // for

            remain = 0;
        } // if

        // This piece of code shows how to use the indicator object into the code
        // of NSGA-II. In particular, it finds the number of evaluations required
        // by the algorithm to obtain a Pareto front with a hypervolume higher
        // than the hypervolume of the true Pareto front.
        if ((indicators != null)
                && (requiredEvaluations == 0)) {
            double HV = indicators.getHypervolume(population);
            if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
                requiredEvaluations = evaluations;
            } // if
        } // if
    }

    public void executeMethod() throws JMException, ClassNotFoundException {
        // Create the offSpring solutionSet
        SolutionSet offspringPopulation;

        offspringPopulation = new SolutionSet(populationSize);
        Solution[] parents = new Solution[2];

        for (int i = 0; i < (populationSize / 4); i++) {
            if (evaluations < maxEvaluations) {
                //obtain parents
                parents[0] = (Solution) selectionOperator.execute(population, null);
                parents[1] = (Solution) selectionOperator.execute(population, null);

                Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents, problem_);

//                    if((evaluations % 1000) == 0){
//                        System.out.println(" - evaluations: " + evaluations);
//                    }
                for (int j = 0; j < offSpring.length; j++) {
                    mutationOperator.execute(offSpring[j], problem_);

                    problem_.evaluate(offSpring[j]);
                    problem_.evaluateConstraints(offSpring[j]);

                    offspringPopulation.add(offSpring[j]);
                }

                evaluations += offSpring.length;

//                    mutationOperator.execute(offSpring[0], problem_);
//                    mutationOperator.execute(offSpring[1], problem_);
//
//                    problem_.evaluate(offSpring[0]);
//                    problem_.evaluateConstraints(offSpring[0]);
//                    problem_.evaluate(offSpring[1]);
//                    problem_.evaluateConstraints(offSpring[1]);
//                    offspringPopulation.add(offSpring[0]);
//                    offspringPopulation.add(offSpring[1]);
//                    evaluations += 2;
            } // if
        } // for
        this.processOffspring(offspringPopulation);
    }

    /**
     * Runs the NSGA-II algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {
        this.initPopulation();
        // Generations ...
        while (evaluations < maxEvaluations) {
            this.executeMethod();
        } // while
        // Return as output parameter the required evaluations
        setOutputParameter("evaluations", requiredEvaluations);
        // Return the first non-dominated front
        Ranking ranking = new Ranking(population);
        return ranking.getSubfront(0);
    } // execute

    @Override
    public void initPopulation() {
        //Read the parameters

        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
        indicators = (QualityIndicator) getInputParameter("indicators");
        distance = new Distance();
        //Initialize the variables
        populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        Object paramArchiveSize = getInputParameter("archiveSize");
        if (paramArchiveSize != null) {
            archiveSize = ((Integer) paramArchiveSize).intValue();
        } else {
            archiveSize = 0;
        }

        archive = new SolutionSet(archiveSize);
        evaluations = 0;
        requiredEvaluations = 0;

        //Read the operators
        mutationOperator = operators_.get("mutation");
        crossoverOperator = operators_.get("crossover");
        selectionOperator = operators_.get("selection");
        population = new SolutionSet(populationSize);

        // Create the initial solutionSet
        Solution newSolution;
        for (int i = 0; i < populationSize; i++) {
            try {
                newSolution = new Solution(problem_);
                problem_.evaluate(newSolution);
                try {
                    problem_.evaluateConstraints(newSolution);
                    evaluations++;
                    population.add(newSolution);
                } catch (JMException ex) {
                    Logger.getLogger(NSGAII.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(NSGAII.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    
    @Override
    public SolutionSet getArchive() {
        return this.clonePopulation(population);
    }

    @Override
    public void setArchive(SolutionSet archive) {
        this.processOffspring(archive);
    }
} // NSGA-II
