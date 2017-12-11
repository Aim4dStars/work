#!/bin/bash
###########################################################################################
#Written by David Mercieca
#Date : 14/07/2017
#this script is called by go server on MTE tools hosts to build nextgen
###########################################################################################
echo MAVEN_HOME=$MAVEN_HOME
echo JAVA_HOME=$JAVA_HOME

echo "==Starting checkstyle=="


echo "listing checkstyle file"
cat etc/checkstyle.report.xml

if [ "$?" -ne "0" ]; then
    exit 1
fi

echo "listing etc directory"
ls -al etc

if [ "$?" -ne "0" ]; then
    exit 1
fi

echo "executing checkstyle scan"
$JAVA_HOME/bin/java -cp $MAVEN_HOME/lib/checkstyle-6.19-all.jar com.puppycrawl.tools.checkstyle.Main -c etc/checkstyle.report.xml  src/main/java -f xml -o checkstyle-result.xml

if [ "$?" -ne "0" ]; then
    exit 1
fi

echo "listing results"
ls -al 

cat checkstyle-result.xml

if [ "$?" -ne "0" ]; then
    exit 1
fi

echo "Completed successfully"

echo "Checkstyle run. Done."
