package jmetal.hyperheuristics;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.util.MetricsUtil;

/**
 *
 * @author giovaniguizzo
 */
public class HypervolumeHandler extends MetricHandler {

    private final Hypervolume hypervolume;
    private final MetricsUtil metricUtil;

    public HypervolumeHandler() {

        this.hypervolume = new Hypervolume();
        this.metricUtil=new MetricsUtil();
    }

    public double calculate(String frontPath, int numberOfObjectives) {
        return calculate(metricUtil.readNonDominatedSolutionSet(frontPath), numberOfObjectives);
    }

    public double calculate(int numberOfObjectives) {
        return this.calculate(population, numberOfObjectives);
    }

   
    
    @Override
    public double calculate(SolutionSet front, int numberOfObjectives) {
        if (population.size() != 0) {
            /*
            double[][] referencePoint = getReferencePoint(numberOfObjectives);
            double[][] objectives = front.writeObjectivesToMatrix();
            referencePoint=front.writeObjectivesToMatrix();
            objectives=this.population.writeObjectivesToMatrix();
            return hypervolume.hypervolume(objectives, referencePoint, numberOfObjectives);
                    */
            this.population=this.removeDominadas(population);
            front=this.removeDominadas(front.union(new SolutionSet()));
            double[][] referencePoint = getReferencePoint(numberOfObjectives);
            double[][] objectives = front.writeObjectivesToMatrix();
            double[] maximumValues = metricUtil.getMaximumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
            double[] minimumValues = metricUtil.getMinimumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
            normalizeObjecties(objectives, minimumValues, maximumValues);
            return 1-hypervolume.calculateHypervolume(objectives, objectives.length, numberOfObjectives);
        }
        return 0D;
    }

    public void WFGHypervolume(SolutionSet newfront, SolutionSet oldfront){
        newfront.printObjectivesToFile("resultado/newfront.txt");
        oldfront.printObjectivesToFile("resultado/oldfront.txt");
    }
}
