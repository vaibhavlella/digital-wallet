#!/usr/bin/env bash

#Assigning the present directory variable
myPath=$(pwd)

#Creating different folder variables for input and output
batch_input=$myPath"/paymo_input/batch_payment.txt"
stream_input=$myPath"/paymo_input/stream_payment.txt"
output1=$myPath"/paymo_output/output1.txt"
output2=$myPath"/paymo_output/output2.txt"
output3=$myPath"/paymo_output/output3.txt"
output4=$myPath"/paymo_output/output4.txt"

# Getting inside the ./src folder
cd src

# Permissions granted to the files.
chmod 777 *.java

javac *.java
java antifraud $batch_input $stream_input $output1 $output2 $output3 $output4

