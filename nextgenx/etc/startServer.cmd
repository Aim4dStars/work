@ECHO OFF
goto Init

:Jrebel
set MAVEN_OPTS=%MAVEN_OPTS% -Drebel.spring_plugin=false -Dmyproject.root=C:/Development/ -javaagent:C:\java\jrebel.jar
set PHASE=-Pjrebel jrebel:generate  %PHASE%
goto EndLoop 

:Debug
set MAVEN_OPTS=%MAVEN_OPTS% -agentlib:jdwp=transport=dt_socket,address=12100,server=y,suspend=n 
goto EndLoop 

:Cms
set MAVEN_OPTS=%MAVEN_OPTS% -Dcms.filename=file:///c:/Development/nextgen-settings/src/main/cms_base/cms/cms-index.xml,file:///c:/Development/nextgen-settings/src/main/cms_base/cms/cms-errors.xml,file:///c:/Development/nextgen-settings/src/main/cms_base/cms/cms-help.xml,file:///c:/Development/nextgen-settings/src/main/cms_base/cms/cms-info.xml,file:///c:/Development/nextgen-settings/src/main/cms_base/cms/cms-disclaimers.xml -Dcms.basedir.resource=file:///c:/Development/nextgen-settings/src/main/cms_base/cms
goto EndLoop

:Integrated
set MAVEN_OPTS=%MAVEN_OPTS% -Dstub.webservice.filestub=false -Davaloq.webservice.filestub=false -DavaloqStatic.webservice.filestub=false -Dehcache.transaction.timeout=12000 -Dehcache.transaction.timeout.long=90000 -Dfeature.businessSpine=true
goto EndLoop

:Usage
echo Usage message
goto Exit: 

:Init
set PHASE=tomcat7:run
set MAVEN_OPTS=%MAVEN_OPTS% -Xmx2048m -XX:MaxPermSize=1024M -XX:-UseGCOverheadLimit

:Loop
IF "%1"=="" GOTO Continue
   if "%1"=="-j" goto Jrebel: 
   if "%1"=="-d" goto Debug:
   if "%1"=="-c" goto Cms:
   if "%1"=="-i" goto Integrated:
   goto Usage
:EndLoop   
SHIFT
GOTO Loop
:Continue

mvn %phase%
:Exit
