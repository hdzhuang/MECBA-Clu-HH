#!/bin/bash  
echo "MECBA - Combined 4 objectives"

problem=$1

java -Xms2048m -Xmx5048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Combined_NSGAII_4obj $problem > resultado/time_Experiment_NSGAII_4obj-$problem.txt &
java -Xms2048m -Xmx5048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Combined_IBEA_4obj $problem > resultado/time_Experiment_IBEA_4obj-$problem.txt &
java -Xms2048m -Xmx5048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Combined_SPEA2_4obj $problem > resultado/time_Experiment_SPEA2_4obj-$problem.txt &
#java -Xms2048m -Xmx5048m -classpath dist/MECBA-Clu-HH.jar jmetal.experiments.Experiment_HH_4obj $problem > resultado/time_Experiment_HH_4obj-$problem.txt &
