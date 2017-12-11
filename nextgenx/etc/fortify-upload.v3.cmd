set PATH=%PATH%;%M2_HOME%\bin;"d:\Program Files\HP_Fortify\HP_Fortify_SCA_and_Apps_4.31\bin"

REM fortifyclient uploadFPR -file target\server-scan.fpr NG-001_BT-Panorama_%1 -url http://10.55.5.165/ssc -authtoken 277f266f-7b5d-4f7e-b8d8-2745b969d983 -debug
fortifyclient uploadFPR -file %3 -project NG-001_BT-Panorama_%1 -version %2 -url https://fortifyssc.stgeorge.com.au/ssc -authtoken 0f320996-4c13-4f81-8d48-8c4ceae8b87b
