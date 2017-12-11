#!/bin/bash
###########################################################################################
#Written by David Mercieca
#Date : 14/07/2017
# this script is called by go server on MTE tools hosts to test the oracle 
# personalisation database for server builds
###########################################################################################
git clean -f -d -x

export MAVEN_ORIG=$MAVEN_OPTS
export MAVEN_OPTS="$MAVEN_ORIG -Xmx2548m -XX:MaxPermSize=512M -Denvironment=CI -Dtimeout=8 -Djava.naming.provider.url=rmi://localhost:11099"
export MAVEN_HOME=$MAVEN_HOME
echo MAVEN_HOME=$MAVEN_HOME

$MAVEN_HOME/bin/mvn -P databaseOracle test

if [ -z ${1+x} ]
then
  >&1 echo "first argument is the server port"
  exit 1
fi

