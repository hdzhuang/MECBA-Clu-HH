/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.hyperheuristics;

import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.Epsilon;

/**
 *
 * @author vinicius
 */
public class EpsilonHandler extends MetricHandler {

    private final Epsilon epsilon;

    public EpsilonHandler() {
        this.epsilon = new Epsilon();
    }

   
    @Override
    public double calculate(SolutionSet front, int numberOfObjectives) {
        if (population.size() != 0) {
            double[][] referencePoint = getReferencePoint(numberOfObjectives);
            double[][] objectives = front.writeObjectivesToMatrix();
            double[] maximumValues = metricUtil.getMaximumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
            double[] minimumValues = metricUtil.getMinimumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
            normalizeObjecties(objectives, minimumValues, maximumValues);
            return epsilon.epsilon(objectives, referencePoint, numberOfObjectives);
        }
        return 0D;
    }
}
