set PATH=%PATH%;%M2_HOME%\bin;"d:\Program Files\HP_Fortify\HP_Fortify_SCA_and_Apps_4.31\bin"

git clean -f -d -x

set SCAN_FILE=target\server-scan.fpr
set FORTIFY_PROJECT=BT-Panorama
set FORTIFY_VERSION=%1
set FORTIFY_URL=https://fortifyssc.stgeorge.com.au/ssc
set FORTIFY_MEMORY_OPTS=-Xmx10g -Xms2g -Xss4g
set FORTIFY_BUILD_ID=%FORTIFY_PROJECT%%FORTIFY_VERSION%

sourceanalyzer -b %FORTIFY_BUILD_ID% -clean

REM mvn -Pci clean sca:translate
sourceanalyzer -b %FORTIFY_BUILD_ID% -64 -Xmx10g -Xms2g -Xss4g  -sql-language PL/SQL mvn -DfailOnSCAError package com.fortify.ps.maven.plugin:sca-maven-plugin:4.30:translate -DskipTests=true

REM mvn -Dfortify.sca.Xmx=2048M -Pci sca:scan
sourceanalyzer -verbose -b %FORTIFY_BUILD_ID% -64 -Xmx10g -Xms2g -Xss4g  -build-label server-%2-%GO_DEPENDENCY_LABEL_SERVER_TESTS% -scan -f %SCAN_FILE%

call fortifyclient uploadFPR -file %SCAN_FILE% -project %FORTIFY_PROJECT% -version %FORTIFY_VERSION% -url %FORTIFY_URL% -authtoken 0f320996-4c13-4f81-8d48-8c4ceae8b87b
@echo on

call fortifyclient downloadFPR -file %SCAN_FILE% -project %FORTIFY_PROJECT% -version %FORTIFY_VERSION% -url %FORTIFY_URL% -authtoken eba77a6e-557b-4031-9e37-a0d978456fad
@echo on


REM Generate XML report
call ReportGenerator %FORTIFY_MEMORY_OPTS% -format xml -f target\result.xml -template OWASP2013.xml -source %SCAN_FILE%
@echo on

REM Generate PDF report
call ReportGenerator %FORTIFY_MEMORY_OPTS% -format pdf -f target\result.pdf -template OWASP2013.xml -source %SCAN_FILE%
@echo on

REM Generate PDF summary report
call ReportGenerator %FORTIFY_MEMORY_OPTS% -format pdf -f target\summary.pdf -template ScanReport.xml -source %SCAN_FILE%
@echo on
