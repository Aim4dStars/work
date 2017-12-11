create table &1._&2.
(
    BSB_Code CHAR(6),
    BANK_NAME CHAR(255)
)
ORGANIZATION EXTERNAL
(
 TYPE ORACLE_LOADER
 DEFAULT DIRECTORY BSB_TAB_DIRECTORY_&4.
 ACCESS PARAMETERS
 (
  records delimited by newline CHARACTERSET US7ASCII
  LOAD WHEN 
  (
    (REC_ID != 'H') and (REC_ID != 'T') and (BSB_Code != BLANKS)
  )
  BADFILE BSB_BAD_DIRECTORY_&4.:'&1._&2..bad'
  LOGFILE BSB_LOG_DIRECTORY_&4.:'&1._&2..log'
  DISCARDFILE BSB_DIS_DIRECTORY_&4.:'&1._&2..dis'
  READSIZE 1048576
  FIELDS LDRTRIM
  MISSING FIELD VALUES ARE NULL
  REJECT ROWS WITH ALL NULL FIELDS
  (
     Rec_ID (1:1) CHAR(1),
     BSB_Code (3:8) CHAR(6),
     BANK_NAME (12:53) CHAR(41)
  )
 )
 LOCATION (BSB_TAB_DIRECTORY_&4.:'&3.')
)
REJECT LIMIT 0;