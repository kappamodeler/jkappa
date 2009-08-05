#!/bin/sh

JAVA=`which java`

if [ x$JAVA != 'x' ] ; then
   ${JAVA} -version > tempfile 2>&1
   VERSION=`grep "^java version" tempfile | awk -F'"' '{print $2}'`
   VERSION_MAX=`echo $VERSION | awk -F'.' '{print $1}'`
   if [ $VERSION_MAX == '0' ] ; then
       JAVA=
   else
       VERSION_MIN=`echo $VERSION | awk -F'.' '{print $2}'`

       if [ x`expr $VERSION_MIN \< 6` == 'x1' ] ; then
           JAVA=
       else
           # Installed version is fine:
           JREDIR=`dirname $JAVA`
           JREDIR=`dirname $JREDIR`
           JAVA=bin/java
       fi
   fi
   rm -f tempfile
fi

# if [ x$JAVA == 'x' ] ; then
   # # Let's try the local version:
   # JAVA=bin/java
   # JREDIR=./jre1.6.0_05
# fi

if [ ! -x ${JREDIR}/${JAVA} ] ; then
  echo ' '
  echo 'Sorry required Java installation not found!'
  echo ' '
  exit;
fi

${JREDIR}/${JAVA} -Xmx1G -classpath "${export.classpath.unix}" com.plectix.simulator.SimulationMain $@

