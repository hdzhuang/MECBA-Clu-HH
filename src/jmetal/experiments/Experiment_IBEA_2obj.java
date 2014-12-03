package jmetal.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.*;
import jmetal.base.operator.crossover.*;
import jmetal.base.operator.mutation.*;
import jmetal.base.operator.selection.*;
import jmetal.problems.*;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.util.JMException;

public class Experiment_IBEA_2obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        //Softwares
        String[] softwares = {
            "OA_AJHotDraw",
            "OA_AJHsqldb",
            "OO_MyBatis",
            "OO_BCEL",
            "OA_TollSystems",
            "OO_JBoss",
            "OO_JHotDraw",
            "OA_HealthWatcher"
        };

        for (String filename : softwares) {

            int runsNumber = 30;
            int populationSize = 300;
            int maxEvaluations = 60000;
            int archiveSize = 300;
            double crossoverProbability = 0.95;
            double mutationProbability = 0.02;
            String context = "_Comb_2obj";

            File directory = new File("resultado/ibea/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.exit(0);
                }
            }

            Combined2Objectives problem = new Combined2Objectives("problemas/" + filename + ".txt");
            Algorithm algorithm = new IBEA(problem);
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
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", maxEvaluations);
            algorithm.setInputParameter("archiveSize", archiveSize);
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            System.out.println("\n================ IBEA ================");
            System.out.println("Software: " + filename);
            System.out.println("Context: " + context);
            System.out.println("Number of elements: " + problem.numberOfElements_);

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

                resultFront.printObjectivesToFile("resultado/ibea/" + filename + context + "/FUN_ibea" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/ibea/" + filename + context + "/VAR_ibea" + "-" + filename + "-" + runs + ".NaoDominadas");

                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
            }

            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/ibea/" + filename + context + "/All_FUN_ibea" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/ibea/" + filename + context + "/All_VAR_ibea" + "-" + filename);

            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_ibea");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
