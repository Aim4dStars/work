echo "Clean area..."
del /F /S /Q *.*

set eps-dev1=10.18.10.70
set eps-dev2=10.18.10.77
set eps-dev3=10.18.10.78

if %version% NEQ LATEST GOTO :passVersion 
for /F "tokens=1* delims==" %%%i in ('FINDSTR /B app.version D:\tomcat-ci1\webapps\nextgen\WEB-INF\classes\version-app.properties') do set theVersion=%%%j
set theVersion=%%theVersion: =%%
echo "Latest version discovered: %%theVersion%%"
GOTO :startRelease

:passVersion
set theVersion=%version%
GOTO :startRelease

:startRelease 
set _environment=%environment.name%
set _envStart=%%_environment:~0,3%%
if %%_envStart%% EQU eps GOTO :epsDeployment

echo "Deploying: %component.name% Version: %%theVersion%%"

echo "Stop the server (%environment.name%) ..."
net stop "Apache Tomcat Tomcat-%environment.name%"
net stop "Apache Tomcat Tomcat-%environment.name%"

echo "Getting settings..."
copy /Y "%maven.repository.home%\com\btfin\%component.name%\%%theVersion%%\%component.name%-%%theVersion%%-settings.jar" .

if %component.name% NEQ nextgen GOTO :afterPropClean
echo "Clear properties directory..."

del /F /S /Q D:\tomcat-%environment.name%\properties\*.*
:afterPropClean

echo "Base settings..."
"%env.JAVA_HOME%\bin\jar.exe" xvf "%component.name%-%%theVersion%%-settings.jar"
xcopy /E /Y /F /I base\* D:\tomcat-%environment.name%\properties\
xcopy /E /Y /F /I cms D:\tomcat-%environment.name%\properties\cms

echo "Environment specific (%environment.name%) ..."
xcopy /Y /S /F %environment.name%\application\* D:\tomcat-%environment.name%\properties

echo "Copy the war..."
RMDIR /S /Q D:\tomcat-%environment.name%\webapps\%component.name%
copy /Y "%maven.repository.home%\com\btfin\%component.name%\%%theVersion%%\%component.name%-%%theVersion%%.war" D:\tomcat-%environment.name%\webapps\%component.name%.war

:startServer
echo "Start the server (%environment.name%) ..."
net start "Apache Tomcat Tomcat-%environment.name%"
GOTO :end

:epsDeployment
echo "Pushing to %environment.name%..."
echo "IP address " %%%environment.name%%%
set _target="m034259@%%%environment.name%%%"
echo "Getting secret..."
call %env.HOME%\secret.cmd
echo "Clearing old files..."
call \dev-tools\putty\plink.exe -batch -pw %%PSWD%% %%_target%% "rm /tmp/nextgen*barista.zip"
echo "Copying %%theVersion%%..."
call \dev-tools\putty\pscp.exe -batch -pw %%PSWD%% "%maven.repository.home%\com\btfin\%component.name%\%%theVersion%%\%component.name%-%%theVersion%%-barista.zip" %%_target%%:/tmp
echo "Cleaning old installations..."
call \dev-tools\putty\plink.exe -batch -pw %%PSWD%% %%_target%% "bash -l -c \"clean-sparrow\""
echo "Installing %%theVersion%%..."
call \dev-tools\putty\plink.exe -batch -pw %%PSWD%% %%_target%% "chmod 777 /tmp/%component.name%-%%theVersion%%-barista.zip; bash -l -c \"apply-package /tmp/%component.name%-%%theVersion%%-barista.zip\""
GOTO :end

:end