#!/bin/sh

JAR_FILE=/tmp/simjav.jar
SIMPLX=/Users/ecemis/plectix-20080930/osx/i386/simplx
DATA_DIR=/Users/ecemis/eclipse-workspace/simulator/data/
ITERATIONS=50


# KAPPA_FILE=${DATA_DIR}/Example.ka 
# TIME=50

# KAPPA_FILE=${DATA_DIR}/degradation-deg-all.ka
# TIME=10

KAPPA_FILE=${DATA_DIR}/brightberl.ka
TIME=1000

############################################################################################

time java -Xmx1G -classpath $JAR_FILE com.plectix.simulator.SimulationMain --sim $KAPPA_FILE --seed 1 --iterations $ITERATIONS --time $TIME

I=0
while expr $I != $ITERATIONS ; do 
    XML_FILE=simplx-`printf %03d $I`.xml
	# echo $XML_FILE
    $SIMPLX --sim $KAPPA_FILE --xml-session-name $XML_FILE --seed $I --time $TIME
	grep , $XML_FILE | grep -v "<" | sed s/,/" "/g > simplx-`printf %03d $I`-curves
	echo =========================== done with $I =================================
	I=`expr $I + 1`;
done


