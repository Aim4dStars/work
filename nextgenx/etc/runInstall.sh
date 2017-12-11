#!/bin/sh

#####
#
# This script installs the settings into the standard nextgen structure
#
# Sparrow/ <- = $SPARROW_HOME
# Sparrow/configuration/properties
# Sparrow/configuration/cms_base
# Sparrow/staging
# Sparrow/log

if [ -z $1 ]
then
  echo "Please specify env config to use"
  exit 1
fi

if [ -z $2 ]
then
  echo "Please specify settings file to use"
  exit 1
fi

if [ -z $SPARROW_HOME ]
then
  echo "Please set SPARROW_HOME to enable installation"
  exit 1
fi

echo "Sparrow Home: $SPARROW_HOME"
echo "Installing using env $1"

PROP_DIR=$SPARROW_HOME/configuration/properties
CMS_DIR=$SPARROW_HOME/configuration/cms_base
TEMP_DIR=/tmp/$$

mkdir -p $TEMP_DIR

echo "Clearing old settings..."
rm -rf $PROP_DIR/*

echo "Extracting new settings..."
cp $2 $TEMP_DIR
(cd $TEMP_DIR; jar xvf *settings.jar)
(cd $TEMP_DIR; rm `basename $2`)
(cd $TEMP_DIR; cp -vr base/* $PROP_DIR)
(cd $TEMP_DIR; cp -vr $1/application/* $PROP_DIR)

echo "Updating cms..."
rm -rf $CMS_DIR/cms
(cd $TEMP_DIR; cp -vr cms $CMS_DIR)

echo "Setting permissions..."
find $SPARROW_HOME -type d | xargs chmod o+xr
find $SPARROW_HOME -type f | xargs chmod o+r

echo "Cleaning temp dir"
(cd $TEMP_DIR; rm -rf *)
