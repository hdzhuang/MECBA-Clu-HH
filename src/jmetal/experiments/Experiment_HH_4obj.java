package jmetal.experiments;

import jmetal.hyperheuristics.AlgorithmHH;
import jmetal.hyperheuristics.HyperHeuristicSelector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import jmetal.base.*;
import jmetal.hyperheuristics.ChoiceFunction;
import jmetal.hyperheuristics.HeuristicBuilder;
import jmetal.problems.*;
import jmetal.util.JMException;
import jmetal.util.Ranking;

public class Experiment_HH_4obj {

    public static void printData(String algName, String filename, String context, int numberOfElements) {
        System.out.println("\n================" + algName + "================");
        System.out.println("Software: " + filename);
        System.out.println("Context: " + context);
        System.out.println("Number of elements: " + numberOfElements);
        long heapSize = Runtime.getRuntime().totalMemory();
        heapSize = (heapSize / 1024) / 1024;
        System.out.println("Heap Size: " + heapSize + "Mb\n");
    }

    public static void verifyLocation(String algName, String filename, String context) {
        File directory = new File("resultado/" + algName + "/" + filename + context);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                //System.err.println("Impossivel criar diretorio");
                //System.exit(0);
            }
        }
    }

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        //Softwares
        String[] softwares;
        if (args.length == 1) {
            softwares = new String[1];
            softwares[0] = args[0];
        } else {
            softwares = new String[4];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OA_AJHotDraw";
            softwares[3] = "OO_BCEL";
            /*
            softwares[4] = "OA_HealthWatcher";
            softwares[5] = "OA_TollSystems";
            softwares[6] = "OO_JBoss";
            softwares[7] = "OO_JHotDraw";
                    */
        }
        int runsNumber = 10;
        int populationSize = 300;
        int archiveSize = 300;
        int totalEvaluations = 60000; //ou seja o que for
        int runHH = 200;
        int maxEvaluations = totalEvaluations/runHH;//slideSize como no artigo
        double scalingFactor = runHH*0.05;
        runHH--;//remover a inicializacao da conta
        double crossoverProbability = 0.95;
        double mutationProbability = 0.02; //0.2;
        int numObj=4;

        for (String filename : softwares) {
            long initTime = System.currentTimeMillis();
            SolutionSet todasRuns = new SolutionSet();
            SolutionSet partial;
            Combined4Objectives problem = new Combined4Objectives("problemas/" + filename + ".txt");
            String context = "_Comb_4obj";

             System.out.println("==========================Executando o problema " + filename + "==========================");
            System.out.println("Context: " + context);
            System.out.println("HH: Choice Function");
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArchiveSize -> " + archiveSize);
            System.out.println("\tMaxEva -> " + totalEvaluations);
            System.out.println("\tSlide Window -> " + maxEvaluations);
            System.out.println("\tCross -> " + crossoverProbability);
            System.out.println("\tMuta -> " + mutationProbability);
            System.out.println("Number of elements: " + problem.numberOfElements_);

            HeuristicBuilder builder = new HeuristicBuilder(problem, crossoverProbability, mutationProbability, populationSize, maxEvaluations, archiveSize);

            //escolhe algoritimo
            for (int runs = 0; runs < runsNumber; runs++) {
                long initRunTime = System.currentTimeMillis();
                SolutionSet resultFront = new SolutionSet();
                builder.initAlgs();
                HyperHeuristicSelector selector = new ChoiceFunction(scalingFactor, builder.getAlgs(), numObj);
                AlgorithmHH algorithm;
                for (AlgorithmHH alg : builder.getAlgs()) {
                    System.out.println("Inicializando " + alg.getMethodName());
                    int i = 0;
                    while (i < maxEvaluations) {
                        alg.executeMethod();
                        i = alg.getEvaluations();
                    }
                }
                for (int eval = 0; eval < runHH; eval++) {
                    algorithm = selector.chooseAlg();
                    int i = 0;
                    while (i < maxEvaluations) {
                        algorithm.executeMethod();
                        i = algorithm.getEvaluations();
                    }
                    partial = algorithm.getFullPopulation();
                    Ranking ranking = new Ranking(partial);
                    resultFront = ranking.getSubfront(0);
                    resultFront = problem.removeDominadas(resultFront);
                    resultFront = problem.removeRepetidas(resultFront);
                    //escolhe algoritimo
                    selector.incrementTimebutNotThis(algorithm);
                }
                resultFront.printObjectivesToFile("resultado/all/" + filename + context + "/FUN_all" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/all/" + filename + context + "/VAR_all" + "-" + filename + "-" + runs + ".NaoDominadas");
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
                long estimatedTime = System.currentTimeMillis() - initRunTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
            }

            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/all/" + filename + context + "/All_FUN_all" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/all/" + filename + context + "/All_VAR_all" + "-" + filename);

            long estimatedTotalTime = System.currentTimeMillis() - initTime;
            System.out.println("\n===============REPORT===================");
            System.out.println("Software: " + filename);

            System.out.println("Estimated Total Time: " + estimatedTotalTime);
            System.out.println("\n=======================================");
            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_ibea");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
