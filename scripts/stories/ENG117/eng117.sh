#! /bin/bash
#java -jar jsim.jar --short-console-output --storify eng117.ka --event 10000 
#--use-strong-compression --operation-mode 1 --iteration 10 --no-maps

#Java --xml-session-name ~session21762.tmp --clock-precision 50 
#--short-console-output --storify ~kappa21763.tmp --event 10000 
#--no-compress-stories --no-use-strong-compression --operation-mode 1 
#--iteration 5 --no-maps

LIMIT_ITERATION=60	# count of iteration for stories
LIMIT_COUNT=5		# count of run JSim/Simplx

for((iteration=5; iteration <= LIMIT_ITERATION ; iteration+=5))
do
#==================NONE=========================
  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    java -jar jsim.jar --short-console-output --storify eng117.ka --event 10000 --no-compress-stories --no-use-strong-compression --operation-mode 1 --iteration $iteration --no-maps
  done
  } > "JSim/JSimOutput_NONE_$iteration.txt" 2>&1

  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    ./simplx --storify eng117.ka --event 10000 --no-compress-stories --no-use-strong-compression --iteration $iteration --no-maps
  done
  } > "SimplxComplx/SimplxComplx_NONE_$iteration.txt"
#=================WEAK==========================
  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    java -jar jsim.jar --short-console-output --storify eng117.ka --event 10000 --compress-stories --no-use-strong-compression --operation-mode 1 --iteration $iteration --no-maps
  done
  } > "JSim/JSimOutput_WEAK_$iteration.txt" 2>&1

  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    ./simplx --storify eng117.ka --event 10000 --compress-stories --no-use-strong-compression --iteration $iteration --no-maps
  done
  } > "SimplxComplx/SimplxComplx_WEAK_$iteration.txt"
#=================STRONG==========================
  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    java -jar jsim.jar --short-console-output --storify eng117.ka --event 10000 --use-strong-compression --operation-mode 1 --iteration $iteration --no-maps
  done
  } > "JSim/JSimOutput_STRONG_$iteration.txt" 2>&1

  {
  for((count=1; count <= LIMIT_COUNT ; count++))
  do
    ./simplx --storify eng117.ka --event 10000 --use-strong-compression --iteration $iteration --no-maps
  done
  } > "SimplxComplx/SimplxComplx_STRONG_$iteration.txt"
done
