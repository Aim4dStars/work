create table &1._&2.
(
	Biller_Code   CHAR(10),
	Biller_Name   CHAR(20),
	Payment_Method  CHAR(3),
	Active_date DATE,
	Deactive_date DATE,
	Crn_Check_Digit_Routine CHAR(10),
	Crn_Valid_Lengths CHAR(20),
	Crn_Fixed_Digits_Mask CHAR(20)
)
ORGANIZATION EXTERNAL
(
 TYPE ORACLE_LOADER
 DEFAULT DIRECTORY BPAY_BILLER_TAB_DIRECTORY_&4.
 ACCESS PARAMETERS
 (
  records delimited by newline CHARACTERSET US7ASCII
  LOAD WHEN 
  (
	(Biller_Code != '0000000000') and (Biller_Code != '9999999999') and (Biller_Code != BLANKS)
  )
  BADFILE BPAY_BILLER_BAD_DIRECTORY_&4.:'&1._&2..bad'
  LOGFILE BPAY_BILLER_LOG_DIRECTORY_&4.:'&1._&2..log'
  DISCARDFILE BPAY_BILLER_DIS_DIRECTORY_&4.:'&1._&2..dis'
  READSIZE 1048576
  FIELDS LDRTRIM
  MISSING FIELD VALUES ARE NULL
  REJECT ROWS WITH ALL NULL FIELDS
  (
	Biller_Code (2:11) CHAR(10),
	Biller_Name (57:76) CHAR(20),
	Payment_Method  (19:21) CHAR(3),
	Active_date (22:29) DATE mask "YYYYMMDD",
	Deactive_date (30:37) DATE mask "YYYYMMDD",
	Crn_Valid_Lengths (931:950) CHAR(20), 
	Crn_Fixed_Digits_Mask (951:970) CHAR(20),
	Crn_Check_Digit_Routine (971:980) CHAR(10)
  )
 )
 LOCATION (BPAY_BILLER_TAB_DIRECTORY_&4.:'&3.')
)
REJECT LIMIT 0;
