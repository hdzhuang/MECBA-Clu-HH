/**
 *
 * @author giovaniguizzo
 */
package jmetal.hyperheuristics;

import java.util.Arrays;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.util.MetricsUtil;

public abstract class MetricHandler {

    protected SolutionSet population;
    protected final MetricsUtil metricUtil;

    public MetricHandler() {
        this.population = new SolutionSet();
        this.metricUtil = new MetricsUtil();
    }

    public void addParetoFront(SolutionSet front) {
        population = population.union(front);
    }

    public void addParetoFront(String path) {
        addParetoFront(metricUtil.readNonDominatedSolutionSet(path));
    }

    public void clear() {
        this.population = new SolutionSet();
    }

    protected double[][] getReferencePoint(int numberOfObjectives) {
        double[][] referencePoint = new double[numberOfObjectives][numberOfObjectives];
        for (int i = 0; i < referencePoint.length; i++) {
            double[] objective = referencePoint[i];
            objective[i] = 1.01;
            for (int j = 0; j < objective.length; j++) {
                if (i != j) {
                    objective[j] = 0;
                }
            }
        }
        return referencePoint;
    }

    
    
    protected void normalizeObjecties(double[][] solutionSet, double[] minimumValues, double[] maximumValues) {
        for (int solutionIndex = 0; solutionIndex < solutionSet.length; solutionIndex++) {
            double[] solution = solutionSet[solutionIndex];
            for (int objectiveIndex = 0; objectiveIndex < solution.length; objectiveIndex++) {
                double max = maximumValues[objectiveIndex];
                double min = minimumValues[objectiveIndex];

                if (min != max) {
                    solution[objectiveIndex] = (solution[objectiveIndex] - min) / (max - min);
                } else {
                    solution[objectiveIndex] = 1.0;
                }
            }
        }
         System.out.print("MAX: ");
        System.out.println(Arrays.toString(maximumValues));
        System.out.print("MIN: ");
        System.out.println(Arrays.toString(minimumValues));
    }

    public abstract double calculate(SolutionSet front, int numberOfObjectives);
    
    protected SolutionSet removeDominadas(SolutionSet result) {
        boolean dominador, dominado;
        double valor1 = 0;
        double valor2 = 0;

        for (int i = 0; i < (result.size() - 1); i++) {
            for (int j = (i + 1); j < result.size(); j++) {
                dominador = true;
                dominado = true;

                for (int k = 0; k < result.get(i).numberOfObjectives(); k++) {
                    valor1 = result.get(i).getObjective(k);
                    valor2 = result.get(j).getObjective(k);

                    if (valor1 > valor2 || dominador == false) {
                        dominador = false;
                    } else if (valor1 <= valor2) {
                        dominador = true;
                    }

                    if (valor2 > valor1 || dominado == false) {
                        dominado = false;
                    } else if (valor2 < valor1) {
                        dominado = true;
                    }
                }

                if (dominador) {
                    result.remove(j);
                    j = j - 1;
                } else if (dominado) {
                    result.remove(i);
                    j = i;
                }
            }
        }

        return result;
    }

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    protected SolutionSet removeRepetidas(SolutionSet result) {
        String solucao;

        for (int i = 0; i < result.size() - 1; i++) {
            solucao = result.get(i).getDecisionVariables()[0].toString();
            for (int j = i + 1; j < result.size(); j++) {
                if (solucao.equals(result.get(j).getDecisionVariables()[0].toString())) {
                    result.remove(j);
                }
            }
        }
        return result;
    }
}
