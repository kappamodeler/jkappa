#!/bin/sh

################################################################################
#                                                                              #
#                    Sript to build open source releases                       #
#                    Created by Ihsan Ecemis 2010-03-05                        #
#                                                                              #
#                                                                              #
################################################################################

#
# set the parameters to below to configure:
#

PRODUCTNAME=JSIM
TOPRELEASEDIR=releases
RELEASEDIRPREFIX=release-

#################################################################################
#
# don't edit below if you don't know what you are doing
# instead set the parameters above
#

#
# setting other variables... 
#

# change the current directory to where the script is:
SCRIPTDIR=`dirname $0`
cd $SCRIPTDIR
SCRIPTDIR=`pwd`

# the full path of the top directory 
TOPDIR=${SCRIPTDIR}/../


cd $SCRIPTDIR
TIMESTAMP=`date +%Y-%m-%d_%H-%M-%S`
RELEASEDIR=${SCRIPTDIR}/${TOPRELEASEDIR}/${RELEASEDIRPREFIX}${TIMESTAMP}
rm -fr $RELEASEDIR
mkdir -p $RELEASEDIR

#################################################################################

function copy_folder_old () {
    # first let's create the directories:
    cd $TOPDIR
    DIRS=`find $1 -type d | grep -v "\/.svn"`
    cd $RELEASEDIR
    for dir in $DIRS ; do
       mkdir -p $dir
    done
    # now let's copy the files:
    cd $TOPDIR
    FILES=`find $1 -type f | grep -v "\/.svn"`
    for file in $FILES ; do
        cp "$file" ${RELEASEDIR}/"$file" 
    done
}

#################################################################################

function copy_folder () {
    # first let's create the directories:
    cd $TOPDIR
    tar --exclude='*.svn*' -cf /tmp/tar.tar $1
    cd $RELEASEDIR
    tar xf /tmp/tar.tar 
    rm -rf /tmp/tar.tar 
}

#################################################################################


# copy folders & files
copy_folder run
copy_folder src/main
copy_folder src/test

copy_folder lib/commons-cli
copy_folder lib/commons-logging
copy_folder lib/junit
copy_folder lib/log4j
copy_folder lib/xstream

copy_folder test.data

mkdir -p ${RELEASEDIR}/config
cp ${TOPDIR}/config/log4j.properties ${RELEASEDIR}/config

mkdir -p ${RELEASEDIR}/data
cp ${TOPDIR}/data/Example.ka ${RELEASEDIR}/data

cp ${SCRIPTDIR}/open-source-build.xml ${RELEASEDIR}/build.xml

# remove unnecessary stuff:
rm -rf ${RELEASEDIR}/run/LiveDataGUI.*
rm -rf ${RELEASEDIR}/src/main/com/plectix/simulator/gui
rm -rf ${RELEASEDIR}/lib/commons-cli/commons-cli-1.1-src.zip
rm -rf ${RELEASEDIR}/lib/commons-logging/commons-logging-1.1.1-javadoc.jar
rm -rf ${RELEASEDIR}/lib/commons-logging/commons-logging-1.1.1-src.jar
rm -rf ${RELEASEDIR}/lib/log4j/log4j-1.2.15-src.jar
rm -rf ${RELEASEDIR}/lib/xstream/xstream-1.3-src.jar

mv -f  ${RELEASEDIR}/src/main/com/plectix/simulator/BuildConstants.tpl  ${RELEASEDIR}/src/main/com/plectix/simulator/BuildConstants.java
