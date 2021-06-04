#!/bin/bash


echo "Starting tests for EvolutionaryFPGA..."
echo "Each test will be run nums times for determining goodness of genetic algorithm"
echo "Result are written in results.txt file"
echo "Parameters for genetic algorithm: populationSize=50, iterations=70000, mutationRate=0.05, nums=300"

pins=1
variables=2
wires=3
nums=300
programFile=$1
saveFile=$2

run_test() {
	echo "Rows: $1, Cols: $2, Pins: 1, Wires: 3, Variables: 2"
	java -jar $programFile --rows $1 --cols $2 --pins $pins --variables $variables --wires $wires --file $3 --nums $nums >> $saveFile
}


echo "Starting calculations for mapping one logical clb into 1x1 FPGA configuration..."
echo "One logical clb into 1x1 FPGA configuration" >> $saveFile
run_test 1 1 "./src/main/resources/1vs1.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping one logical clb into 2x1 FPGA configuration ..."
echo "One logical clb into 2x1 FPGA configuration" >> $saveFile
run_test 2 1 "./src/main/resources/1vs1.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping one logical clb into 1x2 FPGA configuration ..."
echo "One logical clb into 1x2 FPGA configuration" >> $saveFile
run_test 1 2 "./src/main/resources/1vs1.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping one logical clb into 2x2 FPGA configuration ..."
echo "One logical clb into 2x2 FPGA configuration" >> $saveFile
run_test 2 2 "./src/main/resources/1vs1.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping two logical clbs with only two variables into 2x1 FPGA configuration ..."
echo "Two logical clbs into 2x1 FPGA configuration" >> $saveFile
run_test 2 1 "./src/main/resources/2vs1-two-vars.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping two logical clbs into 1x2 FPGA configuration ..."
echo "Two logical clbs into 1x2 FPGA configuration" >> $saveFile
run_test 1 2 "./src/main/resources/2vs1-two-vars.txt"
echo $'\n' >> $saveFile


echo "Starting calculations for mapping two logical clbs into 2x2 FPGA configuration ..."
echo "Two logical clbs into 2x2 FPGA configuration" >> $saveFile
run_test 2 2 "./src/main/resources/2vs1-two-vars.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping two logical clbs with three variables into 2x1 FPGA configuration ..."
echo "Two logical clbs with three variables nto 2x1 FPGA configuration" >> $saveFile
run_test 2 1 "./src/main/resources/2vs1-three-vars.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping two logical clbs with three variables into 1x2 FPGA configuration ..."
echo "Two logical clbs with three variables into 1x2 FPGA configuration" >> $saveFile
run_test 1 2 "./src/main/resources/2vs1-three-vars.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping two logical clbs with three variables into 2x2 FPGA configuration ..."
echo "Rows: 2, Cols: 2, Pins: 1, Wires: 3, Variables: 2"
echo "Two logical clbs with three variables into 2x2 FPGA configuration" >> $saveFile
run_test 2 2 "./src/main/resources/2vs1-three-vars.txt"
echo $'\n' >> $saveFile

echo "Starting calculations for mapping three logical clbs with three variables into 2x2 FPGA configuration ..."
echo "Three logical clbs into 2x2 FPGA configuration" >> $saveFile
run_test 2 2 "./src/main/resources/3vs1.txt"
echo $'\n' >> $saveFile


echo "Starting calculations for mapping two logical clbs into 2x2 FPGA configuration with 5 wires..."
echo "Two logical clbs into 2x2 FPGA configuration, 5 wires" >> $saveFile
wires=5
run_test 2 2 "./src/main/resources/2vs1-two-vars.txt"
echo $'\n' >> $saveFile


















