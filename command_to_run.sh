#!/bin/bash  

#echo "MECBA-Clu - Combined 2 objectives"
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_NSGAII_2obj > resultado/time_Experiment_NSGAII_2obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_PAES_2obj > resultado/time_Experiment_PAES_2obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_SPEA2_2obj > resultado/time_Experiment_SPEA2_2obj.txt &

echo "MECBA-Clu - Combined 4 objectives"
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_NSGAII_4obj > resultado/time_Experiment_NSGAII_4obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_PAES_4obj > resultado/time_Experiment_PAES_4obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu.jar jmetal.experiments.Experiment_SPEA2_4obj > resultado/time_Experiment_SPEA2_4obj.txt &
