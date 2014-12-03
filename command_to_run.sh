#!/bin/bash  

#echo "MECBA-Clu-HH - Combined 2 objectives"
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_NSGAII_2obj > resultado/time_Experiment_NSGAII_2obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_IBEA_2obj > resultado/time_Experiment_IBEA_2obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_SPEA2_2obj > resultado/time_Experiment_SPEA2_2obj.txt &

echo "MECBA-Clu-HH - Combined 4 objectives"
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_NSGAII_4obj > resultado/time_Experiment_NSGAII_4obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_IBEA_4obj > resultado/time_Experiment_IBEA_4obj.txt &
java -Xms2048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_SPEA2_4obj > resultado/time_Experiment_SPEA2_4obj.txt &
