set tomcat.service.name=%1
set component.name=%2
set nextgen-environment=%3
set build.branch=%4

IF NOT %build.branch% == ip GOTO :afterChangeName
set tomcat.service.name="Apache Tomcat Tomcat-CI2"
set nextgen-environment=ci2

:afterChangeName

echo "Stop the server..."
net stop %tomcat.service.name%
net stop %tomcat.service.name%

if %component.name% NEQ nextgen GOTO :afterPropClean
echo "Clear properties directory..."
del /F /S /Q d:\tomcat-%nextgen-environment%\properties\*.*

:afterPropClean

cd target
mkdir settings
xcopy /Y *settings.jar settings
cd settings
"%JAVA_HOME%\bin\jar.exe" xvf *settings.jar

echo "Base settings..."
xcopy /E /Y /F /I base\* d:\tomcat-%nextgen-environment%\properties
xcopy /E /Y /F /I cms d:\tomcat-%nextgen-environment%\properties\cms

echo "Environment specific (%nextgen-environment%) ..."
xcopy /Y /S /F %nextgen-environment%\application\* d:\tomcat-%nextgen-environment%\properties
RMDIR /S /Q d:\tomcat-%nextgen-environment%\webapps\%component.name%
del /F /S /Q d:\tomcat-%nextgen-environment%\webapps\%component.name%.war

echo "Start the server..."
net start %tomcat.service.name%

cd ..
copy /Y %component.name%.war d:\tomcat-%nextgen-environment%\webapps\%component.name%.war


:end
