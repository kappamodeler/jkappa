#! /bin/bash
# eng117_part2_JSim.sh

FOUND=0                   # Cod of finish if Ok.
simulation=0
substringIndex=0
str=""
length=0

for filename in *         # Search all files in current directory.
do
  echo "$filename" | grep -q ".txt"         # Check filename extension
  if [ $? -eq $FOUND ]
  then
#    echo "$filename"
    while read line
    do
      simulation=`expr match "$line" '-Simulation\:'`
#      echo "$simulation"
      if [ $simulation -ne 0 ]
      then
        substringIndex=`expr index "$line" "[=]"`
        substringIndex=$(( substringIndex + 1 ))
#        echo "$substringIndex"
        length=${#line}
        str=`expr substr "$line" $substringIndex $length`

        substringIndex=`expr index "$str" "[=]"`
        substringIndex=$(( substringIndex + 1 )) 
        str=`expr substr "$str" $substringIndex $length`

        substringIndex=`expr index "$str" "[s]"`
        substringIndex=$(( substringIndex - 2 ))
        str=`expr substr "$str" 1 $substringIndex`
#        echo "$substringIndex"
#        echo "$line"
        echo "$str"
      fi
      simulation=0
    done <"$filename" > RESULTS_"${filename}"
  fi
done
