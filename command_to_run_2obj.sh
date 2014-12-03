#!/bin/bash  

echo "MECBA - Combined 2 objectives"

problem=$1

java -Xms2048m -Xmx5048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_NSGAII_2obj $problem > resultado/time_Experiment_NSGAII_2obj-$problem.txt &
java -Xms2048m -Xmx5048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_IBEA_2obj $problem > resultado/time_Experiment_IBEA_2obj-$problem.txt &
java -Xms2048m -Xmx5048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_SPEA2_2obj $problem > resultado/time_Experiment_SPEA2_2obj-$problem.txt &
#java -Xms2048m -Xmx5048m -classpath dist/MECBA-HH.jar jmetal.experiments.Experiment_HH_2obj $problem > resultado/time_Experiment_HH_2obj-$problem.txt &

