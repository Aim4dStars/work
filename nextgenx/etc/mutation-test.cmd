REM Will run mutation testing
call %dev.tools%\maven\bin\mvn -Pci test-compile org.pitest:pitest-maven:mutationCoverage
if %errorlevel% neq 0 exit /b %errorlevel%
