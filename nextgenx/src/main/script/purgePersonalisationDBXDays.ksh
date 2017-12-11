#!/usr/bin/ksh

#Check usage: Expected parameter is <number of days ago> <personalisation schema>
if  [ $# != 2 ]
then
	echo "Usage: purgePersonalisationDBXDays.ksh <number of days ago> <personalisation schema>"
	echo "eg: purgePersonalisationDBXDays.ksh 181 PER_OWNER_DEV2"
	echo "<number of days ago> is typically 181 to match the ABS purge job - Discard dormant client onboarding - BU.BTFG"
	echo "<personalisation schema> typically PER_OWNER_DEV2 (or for prod PER_OWNER)"
	echo "see also http://dwgps0026/twiki/bin/view/NextGen/OnboardingApplication180DayPurge"
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

DAYS_AGO=${1}
export DAYS_AGO
echo running with DAYS_AGO = $DAYS_AGO
PERSONALISATION_SCHEMA=${2}
export PERSONALISATION_SCHEMA
echo running with PERSONALISATION_SCHEMA = $PERSONALISATION_SCHEMA



$ORACLE_HOME/bin/sqlplus -s / << EOF
set head off
set echo off
set feed off
set verify off
whenever sqlerror exit sql.sqlcode;
SET SERVEROUTPUT ON
execute ${PERSONALISATION_SCHEMA}.onboarding_application_clean(${DAYS_AGO});
exit;
EOF


#Check if the db scripts encountered any error
saveCode=$?
if [ "${saveCode}" != "0" ]
then
	echo "SQL Script error! - examine the log and don't panic, this is not critical yet should be fixed if it happens every day"
	exit $saveCode
fi

echo "Success on `date`"
exit 0
