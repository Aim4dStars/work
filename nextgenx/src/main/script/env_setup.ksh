#!/usr/bin/ksh
#Check parameters expected 
if  [ "${1}" != "bsb"  -a "${1}" != "bpay_biller" -o $# != 3 ];
then
	clear
	echo "Usage: env_setup.ksh <target> <incoming folder> <output folder>"
	echo "<target> value is either bsb or bpay_biller"
	echo "<incoming folder> value is the incoming data file folder location"
	echo "<output folder> value is the log, discard and bad folder output locations"
	exit 1
fi

#Set the target for which the environment is being defined
TARGET=$1
export TARGET

BASEDIR=`dirname $0`
export BASEDIR

#Run the Envs setup
if [ ! -x ${BASEDIR}/util/envVars -o ! -f ${BASEDIR}/util/envVars ]
then
        echo "Env script ${BASEDIR}/util/envVars file not found!"
        exit 1
fi
. ${BASEDIR}/util/envVars

#Check for directory folder exists
if [ ! -d "${2}" -o ! -d "${3}" ];
then
	echo "Folder does not exist!"
	exit 1
fi

#Folder where log, discard and bad files to be generated
DEFAULT_OUTPUT_DIR=${3:-/tmp}

#Set to the given folder in param for incoming file location
TAB_DIRECTORY=${2:-/tmp}
export TAB_DIRECTORY

BAD_DIRECTORY=${DEFAULT_OUTPUT_DIR}/bad
LOG_DIRECTORY=${DEFAULT_OUTPUT_DIR}/log
DIS_DIRECTORY=${DEFAULT_OUTPUT_DIR}/discard
export BAD_DIRECTORY LOG_DIRECTORY DIS_DIRECTORY

#Subroutine to create an OS directory
createDir() {
if [ ! -d $1 ]
then
	mkdir $1 

	if [ ! "${?}" = "0" ]
	then
		echo "Env setup script error!"
		exit 1
# Commented out the chmod below since 2755 directory perms are assumed
# for all Oracle directories
#	else
#		chmod 777 $1
	fi
fi
}

#Create the OS directories
#At least read priv is needed in TAB_DIRECTORY
createDir $TAB_DIRECTORY
#At least read and write priv is needed in rest directories
createDir $BAD_DIRECTORY
createDir $LOG_DIRECTORY
createDir $DIS_DIRECTORY

#Setup the Oracle directories
$ORACLE_HOME/bin/sqlplus -s / << EOF
set head off
set verify off
set feed off
set echo off
whenever sqlerror exit sql.sqlcode;
@${BASEDIR}/sql/scr_setup.sql $TAB_DIRECTORY $BAD_DIRECTORY $LOG_DIRECTORY $DIS_DIRECTORY $TARGET $ENV_NAME
exit;
EOF
