#!/bin/sh

######################  Global Variables  #################################################

export JAR_FILE=/tmp/simjav.jar
export SIMPLX=/Users/ecemis/plectix-20080930/osx/i386/simplx
export DATA_DIR=/Users/ecemis/eclipse-workspace/simulator/data/
export ITERATIONS=50


############################################################################################
#
# functions:
#
function checkVariable () {
    if [ $1 == 'x' ] ; then
       echo ' '
       echo Variable $2 is not defined. Aborting...
       echo ' '
       exit 1;
    fi 
}



############################################################################################


checkVariable x$JAR_FILE JAR_FILE
checkVariable x$SIMPLX SIMPLX
checkVariable x$DATA_DIR DATA_DIR
checkVariable x$ITERATIONS ITERATIONS

checkVariable x$KAPPA_FILE KAPPA_FILE
checkVariable x$TIME TIME
checkVariable x$RUN_JAVA_ITERATIONS RUN_JAVA_ITERATIONS

KAPPA_FILE=${DATA_DIR}/${KAPPA_FILE}


############################################################################################


COMMAND="time java -Xmx1G -classpath $JAR_FILE com.plectix.simulator.SimulationMain --sim $KAPPA_FILE --seed 1 --time $TIME --iterations $ITERATIONS"
if [ $RUN_JAVA_ITERATIONS == true ] ; then
   echo Running $COMMAND
   $COMMAND
   echo =========================== done with RUN_JAVA_ITERATIONS ========================
fi

# Reset variables:
I=1
ITERATIONS=`expr $ITERATIONS + $I`;

while expr $I != $ITERATIONS ; do 
    XML_FILE=simplx-ocaml-`printf %03d $I`.xml
    COMMAND="$SIMPLX --sim $KAPPA_FILE --xml-session-name $XML_FILE --seed $I --time $TIME"
	echo Running $COMMAND
	$COMMAND
	grep , $XML_FILE | grep -v "<" | sed s/,/" "/g > simplx-ocaml-`printf %03d $I`-curves
    echo =========================== done with simplx =============================

    if [ $RUN_JAVA_ITERATIONS != true ] ; then
        XML_FILE=simplx-java-`printf %03d $I`.xml
        COMMAND="time java -Xmx1G -classpath $JAR_FILE com.plectix.simulator.SimulationMain --sim $KAPPA_FILE --seed $I --time $TIME"
	    echo Running $COMMAND
	    $COMMAND
	    mv simplx.xml $XML_FILE
	    grep , $XML_FILE | grep -v "<" | sed s/,/" "/g > simplx-java-`printf %03d $I`-curves
        echo =========================== done with java =============================
    fi

	echo =========================== done with $I =================================
	I=`expr $I + 1`;
done

echo ===================================================================================
echo "                          done with $0 "
echo ===================================================================================


############################################################################################


