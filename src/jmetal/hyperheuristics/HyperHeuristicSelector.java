/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public abstract class HyperHeuristicSelector {

    protected int numObj;
    protected ArrayList<AlgorithmHH> algs;
    protected AlgorithmHH current;

    protected HashMap<AlgorithmHH, ArrayList<AlgorithmRanking>> hash;
    protected HashMap<AlgorithmHH, SolutionSet> olderPopulation;

    protected HypervolumeHandler hypervolume;
    protected RNI rni;
    

    public HyperHeuristicSelector(ArrayList<AlgorithmHH> algs, int numObj) {
        this.numObj = numObj;
        this.algs = algs;
        this.current = null;
        this.hash = new HashMap<>();
        this.olderPopulation = new HashMap<>();
        this.hypervolume = new HypervolumeHandler();
        this.rni = new RNI();
        this.initHHSelector();
    }

    protected void trocaContexto(AlgorithmHH origem, AlgorithmHH destino) {
        if (origem != null) {
            this.olderPopulation.put(destino, destino.getFullPopulation());
            destino.setFullPopulation(origem.getFullPopulation());
            destino.setRequiredEvaluations(origem.getRequiredEvaluations());
        } else {
            for (AlgorithmHH alg : this.algs) {
                alg.setFullPopulation(destino.getFullPopulation());
                this.olderPopulation.put(alg, destino.getFullPopulation());
            }
        }
        destino.setEvaluations(0);
    }

    protected void hashInit() {
        for (AlgorithmHH alg : algs) {
            SolutionSet fullPop = alg.getFullPopulation();
            this.olderPopulation.put(alg, fullPop);
            Improvement imp = new Improvement(this.hypervolume);
            ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
            AlgorithmRanking algranking = new AlgorithmRanking(imp, alg, this.numObj);//Hypervolume
            ranking.add(algranking);//Hypervolume
            algranking = new AlgorithmRanking(new Improvement(rni), alg, this.numObj);//RNI
            ranking.add(algranking);//RNI
            //outras metricas
            this.hash.put(alg, ranking);
        }
    }

    private SolutionSet joinAllPopulation() {
        SolutionSet allpopulation = new SolutionSet();
        for (SolutionSet population : this.olderPopulation.values()) {
            allpopulation = allpopulation.union(population);
        }
        return allpopulation;
    }

    public void initHHSelector() {
        this.hashInit();
    }

    public AlgorithmHH selectAlg(int id) {
        if (id > this.algs.size()) {
            id = 0;
        }
        AlgorithmHH alg = this.algs.get(id);
        this.trocaContexto(this.current, alg);
        alg.restartEstimatedTime();
        this.current = alg;
        return alg;
    }

    public void updateRankingAlg(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        for (AlgorithmRanking algRanking : ranking) {
            algRanking.calcMetric();
        }
    }

    private ArrayList<AlgorithmRanking> getRankingByMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
        for (AlgorithmHH alg : algs) {
            ArrayList<AlgorithmRanking> allranking = this.hash.get(alg);
            AlgorithmRanking algranking = null;
            for (AlgorithmRanking aux : allranking) {
                if (aux.getImp().getMetric() == metric) {
                    algranking = aux;
                    algranking.calcMetric();
                }
            }
            ranking.add(algranking);
        }
        Collections.sort(ranking);
        return ranking;
    }

    private void calcRankingMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = this.getRankingByMetric(metric);
        int rank = 0;
        double previus = Double.MIN_VALUE;
        for (AlgorithmRanking algranking : ranking) {
            if (algranking.getValue() != previus) {
                rank++;
                previus = algranking.getValue();
            }
            algranking.setRanking(rank);
        }
    }

    public void updateImprovement(AlgorithmHH alg, SolutionSet allpopulation) {
        double improveHyp = this.calcImprovement(alg, this.hypervolume, allpopulation);//Hypervolume
        Improvement imp = this.getImprovementObj(alg, hypervolume);//Hypervolume
        imp.addImprovement(improveHyp);//Hypervolume
        double improveRNI = this.calcImprovement(alg, rni, allpopulation);//RNI
        imp = this.getImprovementObj(alg, rni);//RNI
        imp.addImprovement(improveRNI);//RNI
    }

    protected void calcRanking() {
        SolutionSet allPop = this.joinAllPopulation();
        if (this.current != null) {
            this.updateImprovement(current, allPop);
        } else {
            for (AlgorithmHH alg : this.algs) {
                this.updateImprovement(alg, allPop);
            }
        }
        this.calcRankingMetric(hypervolume);//Hypervolume
        this.calcRankingMetric(rni); //RNI
    }

    private int calcFrequencyFirst(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        int qtdfirst = 0;
        if (ranking != null) {
            for (AlgorithmRanking algrank : ranking) {
                if (algrank.getRanking() == 1) {
                    qtdfirst++;
                }
            }
        }
        return qtdfirst;
    }

    protected int[] calcAllFrequency() {
        Map<AlgorithmHH, Integer> frequencies = new HashMap<AlgorithmHH, Integer>();
        int biggerFrequency = Integer.MIN_VALUE;
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            int aux = this.calcFrequencyFirst(alg);
            frequencies.put(alg, aux);
            if (aux > biggerFrequency) {
                biggerFrequency = aux;
            }
        }
        frequencies = this.sortByComparator(frequencies);
        int[] frequenciesArray = new int[this.algs.size()];
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            frequenciesArray[i] = this.returnrRankPosition(alg, frequencies);
        }
        return frequenciesArray;
    }

    private int returnrRankPosition(AlgorithmHH alg, Map<AlgorithmHH, Integer> frequencies) {
        Iterator it = frequencies.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            counter++;
            if (pairs.getKey() == alg) {
                return counter;
            }
        }
        return counter;
    }

    private LinkedHashMap<AlgorithmHH, Integer> sortByComparator(Map<AlgorithmHH, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<AlgorithmHH, Integer>> list
                = new LinkedList<Map.Entry<AlgorithmHH, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<AlgorithmHH, Integer>>() {
            public int compare(Map.Entry<AlgorithmHH, Integer> o1,
                    Map.Entry<AlgorithmHH, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        LinkedHashMap<AlgorithmHH, Integer> sortedMap = new LinkedHashMap<AlgorithmHH, Integer>();
        for (Iterator<Map.Entry<AlgorithmHH, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<AlgorithmHH, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    protected Improvement getImprovementObj(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank.getImp();
            }
        }
        return null;
    }
    
    protected AlgorithmRanking getMetric(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank;
            }
        }
        return null;
    }

    public void incrementTimebutNotThis(AlgorithmHH algNoincrement) {
        for (AlgorithmHH alg : this.algs) {
            if (alg != algNoincrement) {
                alg.incrementEstimatedTime();
            }
        }
    }

    public void updateOldPopulation(AlgorithmHH alg, SolutionSet solution) {
        this.olderPopulation.put(alg, solution);
    }

    public double calcImprovement(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {

        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getFullPopulation());
        //calcula o hyp do novo e do velho
        double oldH = metric.calculate(this.olderPopulation.get(alg), numObj);
        double newH = metric.calculate(alg.getFullPopulation(), numObj);
        return newH;/// - oldH;
    }

    public AlgorithmHH chooseAlg() {
        return null;
    }

    public AlgorithmHH chooseAlg(int firstIteration, Double[] qOp, Double[] nOptrial) {
        return null;
    }
}
