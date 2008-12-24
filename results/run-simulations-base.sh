#!/bin/sh

######################  Global Variables  #################################################

export JAR_FILE=/tmp/simjav.jar
export SIMPLX=/Users/ecemis/plectix-20081125/osx/simplx
export DATA_DIR=/Users/ecemis/eclipse-workspace/simulator/data/
export ITERATIONS=50

# old version of simplx (buggy):
# export SIMPLX=/Users/ecemis/plectix-20080930/osx/i386/simplx
# export SIMPLX=/Users/ecemis/plectix-20081107/osx/simplx
# export SIMPLX=/Users/ecemis/plectix-20081117/osx/simplx

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

function runCommand () {
    echo Running $1
	$1
	result=$?
    if [ $result != 0 ]; then
       echo "Command failed. Return: " $result
	   echo "Command: " $1
       echo "Aborting..."
       exit 1
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

if [ x$OCAML_XML_FILE_PREFIX == 'x' ] ; then
    export OCAML_XML_FILE_PREFIX="simplx-ocaml"
fi

if [ x$JAVA_XML_FILE_PREFIX == 'x' ] ; then
    export JAVA_XML_FILE_PREFIX="simplx-java"
fi

############################################################################################

JAVA_COMMAND_PREFIX="time java -Xmx1G -classpath $JAR_FILE com.plectix.simulator.SimulationMain --ocaml_style_obs_name --sim $KAPPA_FILE --time $TIME "
SIMPLX_COMMAND_PREFIX="$SIMPLX --sim $KAPPA_FILE --time $TIME "
echo JAVA_COMMAND_PREFIX: $JAVA_COMMAND_PREFIX
echo SIMPLX_COMMAND_PREFIX: $SIMPLX_COMMAND_PREFIX

if [ $RUN_JAVA_ITERATIONS == true ] ; then
   COMMAND=${JAVA_COMMAND_PREFIX}"--seed 1 --iterations $ITERATIONS"
   runCommand "$COMMAND"
   echo =========================== done with RUN_JAVA_ITERATIONS ========================
fi

# Reset variables:
I=1
ITERATIONS=`expr $ITERATIONS + $I`;

while expr $I != $ITERATIONS ; do 
    XML_FILE=${OCAML_XML_FILE_PREFIX}-`printf %03d $I`.xml
    COMMAND=${SIMPLX_COMMAND_PREFIX}"--xml-session-name $XML_FILE --seed $I"
    runCommand "$COMMAND"

	grep , $XML_FILE | grep -v "<" | sed s/,/" "/g > ${OCAML_XML_FILE_PREFIX}-`printf %03d $I`-curves
    echo =========================== done with simplx =============================

    if [ $RUN_JAVA_ITERATIONS != true ] ; then
		COMMAND=${JAVA_COMMAND_PREFIX}"--seed $I"
        runCommand "$COMMAND"

        XML_FILE=${JAVA_XML_FILE_PREFIX}-`printf %03d $I`.xml
	    mv simplx.xml $XML_FILE
	    grep , $XML_FILE | grep -v "<" | sed s/,/" "/g > ${JAVA_XML_FILE_PREFIX}-`printf %03d $I`-curves
        echo =========================== done with java =============================
    fi

	echo =========================== done with $I =================================
	I=`expr $I + 1`;
done

echo ===================================================================================
echo "                          done with $0 "
echo ===================================================================================


############################################################################################


