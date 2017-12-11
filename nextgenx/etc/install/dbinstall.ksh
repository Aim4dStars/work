#!/usr/bin/ksh
#Oracle environment settings - expect to be defined as env variables
ORACLE_HOME=${ORACLE_HOME:=/oradb/11.2/perhome1}

dev1env(){
	NG_ONLINE_SCHEMA=PER_OWNER_DEV1
	TXMANAGER_SCHEMA=TXMANAGER_DEV1
	ADOBE_SCHEMA=ADOBE_DEV1
	BSB_BATCH_SCHEMA=BSB_BATCH_DEV1
	DBNAME=PERD1
	DBSERVICE=PER_DEV1.BTFG
	ORACLE_HOST=ngwdw-vip.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1410
}
dev2env(){
	NG_ONLINE_SCHEMA=PER_OWNER_DEV2
	TXMANAGER_SCHEMA=TXMANAGER_DEV2
	ADOBE_SCHEMA=ADOBE_DEV2
	BSB_BATCH_SCHEMA=BSB_BATCH_DEV2
	DBNAME=PERD1
	DBSERVICE=PER_DEV2.BTFG
	ORACLE_HOST=ngwdw-vip.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1410
}
dev3env(){
	NG_ONLINE_SCHEMA=PER_OWNER_DEV3
	TXMANAGER_SCHEMA=TXMANAGER_DEV3
	ADOBE_SCHEMA=ADOBE_DEV3
	BSB_BATCH_SCHEMA=BSB_BATCH_DEV3
	DBNAME=PERD1
	DBSERVICE=PER_DEV3.BTFG
	ORACLE_HOST=ngwdw-vip.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1410
}
sit1env(){
	NG_ONLINE_SCHEMA=PER_OWNER_SIT1
	TXMANAGER_SCHEMA=TXMANAGER_SIT
	ADOBE_SCHEMA=ADOBE_SIT
	BSB_BATCH_SCHEMA=BSB_BATCH
	DBNAME=PERU1
	DBSERVICE=PER_SIT1.BTFG
	ORACLE_HOST=aixau2106sd0101.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1310
}
sit2env(){
	NG_ONLINE_SCHEMA=PER_OWNER_SIT2
	TXMANAGER_SCHEMA=TXMANAGER_SIT2
	ADOBE_SCHEMA=ADOBE_SIT2
	BSB_BATCH_SCHEMA=BSB_BATCH_SIT2
	DBNAME=PERU1
	DBSERVICE=PER_SIT2.BTFG
	ORACLE_HOST=aixau2106sd0101.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1310
}
uat1env(){
	NG_ONLINE_SCHEMA=PER_OWNER_U1
	TXMANAGER_SCHEMA=TXMANAGER_UAT1
	ADOBE_SCHEMA=ADOBE_UAT1
	BSB_BATCH_SCHEMA=BSB_BATCH_U1
	DBNAME=PERU1
	DBSERVICE=PER_UAT1.BTFG
	ORACLE_HOST=aixau2106sd0101.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1310
}
uat2env(){
	NG_ONLINE_SCHEMA=PER_OWNER_UAT2
	TXMANAGER_SCHEMA=TXMANAGER_UAT2
	ADOBE_SCHEMA=ADOBE_UAT2
	BSB_BATCH_SCHEMA=BSB_BATCH_UAT2
	DBNAME=PERU1
	DBSERVICE=PER_UAT2.BTFG
	ORACLE_HOST=aixau2106sd0101.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1310
}
trn1env(){
	NG_ONLINE_SCHEMA=PER_OWNER_TRAIN                   
	TXMANAGER_SCHEMA=TXMANAGER_TRAIN                   
	ADOBE_SCHEMA=ADOBE_TRAIN                           
	BSB_BATCH_SCHEMA=BSB_BATCH_TRAIN                   
	DBNAME=PERU1                                       
	DBSERVICE=PER_TRAIN.BTFG                           
	ORACLE_HOST=aixau2106sd0101.wsdc.nsw.westpac.com.au
	ORACLE_PORT=1310 
}
svpenv(){
	NG_ONLINE_SCHEMA=PER_OWNER
	TXMANAGER_SCHEMA=TXMANAGER_SVP
	ADOBE_SCHEMA=adobe_au2106st0132
	ADOBE_SCHEMA2=adobe_au2004st0111
	BSB_BATCH_SCHEMA=BSB_BATCH
	DBNAME=PERV1
	DBSERVICE=PERV1_PRIM.BTFG
	ORACLE_HOST=ngwvr-vip.rcc.nsw.westpac.com.au
	ORACLE_PORT=1210
}
prodenv(){
	NG_ONLINE_SCHEMA=PER_OWNER
	TXMANAGER_SCHEMA=TXMANAGER
	ADOBE_SCHEMA=adobe_servername
	ADOBE_SCHEMA2=adobe_servername2
	BSB_BATCH_SCHEMA=BSB_BATCH
	DBNAME=PERP1
	DBSERVICE=PERP1_PRIM.BTFG
	ORACLE_HOST=ngwpr-vip.rcc.nsw.westpac.com.au
	ORACLE_PORT=1110
}

ENV=$1
if [ "${ENV}" = "" -o $# != 1 ];
then
	echo "Usage: dbinstall.ksh <Environment Target>"
	echo "Environment Target values - dev1, dev2, dev3, sit1, sit2, uat1, uat2, trn1, svp, prod"
	exit 1
elif [ "${ENV}" != "dev1" -a "${ENV}" != "dev2" -a "${ENV}" != "dev3" -a "${ENV}" != "sit1" -a "${ENV}" != "sit2" -a "${ENV}" != "uat1" -a "${ENV}" != "uat2" -a "${ENV}" != "trn1" -a "${ENV}" != "svp" -a "${ENV}" != "prod" ];
then
	echo "Invalid Environment Target value:  ${ENV}"
	echo "Valid values - dev1, dev2, dev3, sit1, sit2, uat1, uat2, svp, prod"
	exit 1
fi

#Get the env values for the target
${ENV}env

uniqTime=`date +%s`

echo "Enter password for ${NG_ONLINE_SCHEMA} : "
read NG_ONLINE_USER_PASSWORD
echo "Enter password for ${BSB_BATCH_SCHEMA} : "
read BSB_BATCH_USER_PASSWORD

#$ORACLE_HOME/bin/sqlplus -s /@\"${ORACLE_HOST}:${ORACLE_PORT}/${DBNAME}\" AS SYSDBA << EOF
$ORACLE_HOME/bin/sqlplus -s / << EOF
set head off
set echo off
set feed on
set verify off
whenever sqlerror exit sql.sqlcode
spool ./${ENV}_${uniqTime}.log
@./1.bsb_batch_setup.sql $NG_ONLINE_SCHEMA $TXMANAGER_SCHEMA $BSB_BATCH_SCHEMA $BSB_BATCH_USER_PASSWORD
@./2.bsb_insert.sql $BSB_BATCH_SCHEMA
@./3.bpay_biller_insert.sql $BSB_BATCH_SCHEMA
connect $NG_ONLINE_SCHEMA/$NG_ONLINE_USER_PASSWORD@\"${ORACLE_HOST}:${ORACLE_PORT}/${DBNAME}\" 
@./4.personalisation_schemas.sql
@./5.personalisation_data.sql
spool off
exit
EOF

#Check execution status
if [ $? != 0 ];
then
	echo "DB Setup for ${ENV} failed. Check logs!"
	exit 1
else
	echo "DB Setup for ${ENV} successful."
	exit 0
fi