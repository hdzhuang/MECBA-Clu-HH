package jmetal.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.*;
import jmetal.base.operator.crossover.*;
import jmetal.base.operator.mutation.*;
import jmetal.base.operator.selection.*;
import jmetal.problems.*;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.util.JMException;

public class Experiment_NSGAII_4obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

         //Softwares
        String[] softwares = {
            "OO_MyBatis",
            "OA_AJHsqldb",
            "OA_AJHotDraw",
            "OO_BCEL",
            //"OA_TollSystems",
            //"OO_JBoss",
            //"OO_JHotDraw",
            //"OA_HealthWatcher"
        };


        for (String filename : softwares) {

            int runsNumber = 20;
            int populationSize = 48;
            int maxEvaluations = 60000;
            double crossoverProbability = 0.95;
            double mutationProbability = 0.02;
            String context = "_Comb_4obj";

            File directory = new File("resultado/nsgaii/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.exit(0);
                }
            }

            Combined4Objectives problem = new Combined4Objectives("problemas/" + filename + ".txt");
            Algorithm algorithm = new NSGAII(problem);
            SolutionSet todasRuns = new SolutionSet();
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
            //algorithm.setInputParameter("crossoverProbability_", "1.0");
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", maxEvaluations);
            //Add the operators to the algorithm
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            System.out.println("\n================ NSGAII ================");
            System.out.println("Software: " + filename);
            System.out.println("Number of elements: " + problem.numberOfElements_);
            System.out.println("Context: " + context);

            long heapSize = Runtime.getRuntime().totalMemory();
            heapSize = (heapSize / 1024) / 1024;
            System.out.println("Heap Size: " + heapSize + "Mb\n");

            for (int runs = 0; runs < runsNumber; runs++) {
                // Execute the Algorithm
                long initTime = System.currentTimeMillis();
                SolutionSet resultFront = algorithm.execute();
                long estimatedTime = System.currentTimeMillis() - initTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);

                resultFront = problem.removeDominadas(resultFront);
                resultFront = problem.removeRepetidas(resultFront);

                resultFront.printObjectivesToFile("resultado/nsgaii/" + filename + context + "/FUN_nsgaii" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/nsgaii/" + filename + context + "/VAR_nsgaii" + "-" + filename + "-" + runs + ".NaoDominadas");

                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
            }

            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/nsgaii/" + filename + context + "/All_FUN_nsgaii" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/nsgaii/" + filename + context + "/All_VAR_nsgaii" + "-" + filename);

            //grava arquivo juntando funcoes e variaveis
            //problem.gravaCompleto(todasRuns, "TodasRuns-Completo_nsgaii");
        }
    }
    //  --  --  --  --  --  --  --  --  --  -                -  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
