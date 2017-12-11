#!/usr/bin/ksh

#Check usage: Expected parameter is bsb or bpay_biller for this script
if  [ "${1}" != "bsb"  -a "${1}" != "bpay_biller" -o $# != 2 ];
then
	echo "Usage: ngperdbldr.ksh <target> <datafile>"
	echo "<target> value is bsb or bpay_biller"
	echo "<datafile> is the target file to process"
	exit 1
fi

BASEDIR=`dirname $0`
export BASEDIR

#Run the Envs setup
if [ ! -x ${BASEDIR}/util/envVars -o ! -f ${BASEDIR}/util/envVars ]
then
	echo "Load error: Env script ${BASEDIR}/util/envVars file not found!"
	exit 1
fi
. ${BASEDIR}/util/envVars

#Schema where the data load happens
BATCH_SCHEMA=BSB_BATCH
export BATCH_SCHEMA

#Define the Target for this sript - bsb or bpay_biller processing
#TARGET=${1:-bsb}
TARGET=$1
export TARGET

#Preprocessing setup: define the prefix to use for this load for files and db table
baseName="EXT_TAB_${TARGET}"
uniqTime=`date +%s`

dataFolder=`${BASEDIR}/util/getDir ${TARGET}_TAB`
export dataFolder

#Based on the target define the primary key field 
primaryKey=bsb_code

if [ "${TARGET}" = "bpay_biller" ];
then
	primaryKey=biller_code
	dataFolder=`${BASEDIR}/util/getDir ${TARGET}_TAB`
fi

#pick up the data file to process
dataFile=$2

#echo "Processing: ${dataFolder}/${dataFile}"
chmod a+r $dataFolder/$dataFile
if [ $? != 0 ];
then
        echo "Unable to set permissions for ${dataFolder}/${dataFile}"
        exit 1
fi

#Execute the core db script to process the ETL process
$ORACLE_HOME/bin/sqlplus -s / << EOF
set head off
set echo off
set feed off
set verify off
whenever sqlerror exit sql.sqlcode;
@${BASEDIR}/sql/${TARGET}_loader.sql $baseName $uniqTime $dataFile $ENV_NAME
@${BASEDIR}/sql/${TARGET}_data_insert.sql $baseName $uniqTime $TARGET $primaryKey $ENV_NAME
exit;
EOF

#Check if the db scripts encountered any error
saveCode=$?
if [ "${saveCode}" != "0" ]
then
	echo "Load Error: Target ${TARGET} File ${dataFile} on `date` - SQL Script error!"
	#check if master table created for postOp handling
	#SQL error 133 is primary key violation 
	if [ "${saveCode}" != "133" ];
	then
		noMasterTable=1
	fi
	${BASEDIR}/util/postOp FAIL $baseName $uniqTime $TARGET $dataFile $noMasterTable
	exit $saveCode
fi

#Check discard file for abnormal error
disfiledir=`${BASEDIR}/util/getDir ${TARGET}_DIS`
#echo $disfiledir
disfile="${disfiledir}/${baseName}_${uniqTime}.dis"

#test for discard file exists and readable. 
if [ ! -r $disfile ]
then
	echo "Load Error: Target ${TARGET} File ${dataFile} on `date` - Discard file not accessible!"
	${BASEDIR}/util/postOp FAIL $baseName $uniqTime $TARGET $dataFile
	exit 1
fi

disRows=`cat $disfile| wc -l| tr -d " "`
disRows=${disRows:?"-1"}
#Expected discard rows total is 2 (one head and one tail record)
if [ "${disRows}" != "2" ] 
then
	echo "Load Error: Target ${TARGET} File ${dataFile} on `date` - unexpected discards found!"
	${BASEDIR}/util/postOp FAIL $baseName $uniqTime $TARGET $dataFile
	exit 2
fi

#Get the file location of the BAD file 
badfiledir=`${BASEDIR}/util/getDir ${TARGET}_BAD`

#Check if there is a bad file exists from this ETL run
badfile="${badfiledir}/${baseName}_${uniqTime}.bad"
if [ -f $badfile ]
then
	echo "Load Error: Target ${TARGET} File ${dataFile} on `date` - Bad file found!"
	${BASEDIR}/util/postOp FAIL $baseName $uniqTime $TARGET $dataFile
	exit 1
fi

#Reset the view to the latest loaded master 
${BASEDIR}/util/postOp SUCCESS $baseName $uniqTime $TARGET $dataFile
#Check if all went well including view reset operation
if [ "${?}" != "0" ];
then
	echo "Load Error: Target ${TARGET} File ${dataFile} on `date` - Postop failure view may not have been created!"
	exit 2
fi
echo "Load Success: Target ${TARGET} File ${dataFile} processed with no errors on `date`"
exit 0
