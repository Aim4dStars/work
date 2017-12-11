#!/bin/bash
###########################################################################################
#Written by David Mercieca
#Date : 14/07/2017
#this script is called by go server on MTE tools hosts to build nextgen
###########################################################################################

git clean -f -d -x

if [ -z ${1+x} ]
then
  >&1 echo "first argument is the server port"
  exit 1
fi

if [ -z ${2+x} ]
then
  >&2 echo "second argument is the mvn cmd"
  exit 1
fi

export MAVEN_ORIG=$MAVEN_OPTS
export MAVEN_OPTS="$MAVEN_ORIG -Xmx5000m -XX:MaxPermSize=2024M -Denvironment=DEV -Djava.naming.provider.url=rmi://localhost:11099"
export MAVEN_HOME=$MAVEN_HOME
echo MAVEN_OPTS=$MAVEN_OPTS

#Remove h2 database dependency as this causes test case failures if there are multiple versions
ls /opt/panorama/tools/maven/.m2/repository/com/h2database/h2

rm -rf /opt/panorama/tools/maven/.m2/repository/com/h2database/h2/*
#error handling
if [ "$?" -ne "0" ]; then
    exit 1
fi
echo "H2 dependency has been removed. Listing the directory again wiht ls command"
ls -ltr /opt/panorama/tools/maven/.m2/repository/com/h2database/h2
#error handling
if [ "$?" -ne "0" ]; then
    exit 1
fi


$MAVEN_HOME/bin/mvn -Pci -Dserver.port=$1 -Denvironment=DEV  $2

#error handling
if [ "$?" -ne "0" ]; then
    exit 1
fi




