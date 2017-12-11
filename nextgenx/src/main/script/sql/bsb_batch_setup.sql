rem connect as sysdba or equivalent
rem Refer to "CHANGE HERE" tag to locate statements in this script to change schema reference 
rem for the target environment

DECLARE
v_count INTEGER := 0;
v_statement VARCHAR2 (500);
BEGIN
	SELECT COUNT (1) INTO v_count FROM dba_users WHERE username = UPPER ('bsb_batch');

	IF v_count != 0
	THEN
		EXECUTE IMMEDIATE ('DROP USER bsb_batch CASCADE');
	END IF;
 
	v_count := 0;
 
EXCEPTION
	WHEN OTHERS
	THEN
		DBMS_OUTPUT.put_line (SQLERRM);
		DBMS_OUTPUT.put_line ('   ');
END;
/
create user bsb_batch identified by bsb_batch_pwd;
grant connect, resource, create any view, create any directory, drop any directory, select any dictionary to bsb_batch;


rem connect as bsb_batch
rem connect bsb_batch/bsb_batch_pwd;

DECLARE
v_count INTEGER := 0;
v_statement VARCHAR2 (500);
BEGIN
	SELECT COUNT (1) INTO v_count FROM all_tables 
	WHERE OWNER = UPPER ('bsb_batch') and table_name = 'BSB_MST_000001';

	IF v_count != 0
	THEN
		EXECUTE IMMEDIATE ('DROP TABLE BSB_BATCH.BSB_MST_000001 CASCADE');
	END IF;
 
	v_count := 0;
 
EXCEPTION
	WHEN OTHERS
	THEN
		DBMS_OUTPUT.put_line (SQLERRM);
		DBMS_OUTPUT.put_line ('   ');
END;
/
rem BSB setup
CREATE TABLE  BSB_BATCH.BSB_MST_000001 (
bsb_code char(6) primary key,
bank_name char(256));

rem CHANGE HERE for new environment to edit the per_owner to the correct user
rem in the next 2 lines
grant all on bsb_batch.bsb_mst_000001 to per_owner with grant option;
create or replace view per_owner.bsb as select * from bsb_batch.bsb_mst_000001;

rem bpay_biller setup
DECLARE
v_count INTEGER := 0;
v_statement VARCHAR2 (500);
BEGIN
	SELECT COUNT (1) INTO v_count FROM all_tables 
	WHERE OWNER = UPPER ('bsb_batch') and table_name = 'BPAY_BILLER_MST_000001';

	IF v_count != 0
	THEN
		EXECUTE IMMEDIATE ('DROP TABLE BSB_BATCH.BPAY_BILLER_MST_000001 CASCADE');
	END IF;
 
	v_count := 0;
 
EXCEPTION
	WHEN OTHERS
	THEN
		DBMS_OUTPUT.put_line (SQLERRM);
		DBMS_OUTPUT.put_line ('   ');
END;
/
CREATE TABLE BSB_BATCH.BPAY_BILLER_MST_000001 (         
BILLER_CODE CHAR(10) primary key, 
BILLER_NAME CHAR(20), 
CRN_CHECK_DIGIT_ROUTINE CHAR(10), 
CRN_VALID_LENGTHS CHAR(20), 
CRN_FIXED_DIGITS_MASK CHAR(20)
);

rem CHANGE HERE for new environment to edit the per_owner to the correct user
rem in the next 2 lines
grant all on bsb_batch.bpay_biller_mst_000001 to per_owner with grant option;
create or replace view per_owner.bpay_biller as select * from bsb_batch.bpay_biller_mst_000001;

rem CHANGE HERE for new environment to edit the per_owner to the correct user
rem and the Avoka user name.
grant select on per_owner.bsb to txmanager_sit;
