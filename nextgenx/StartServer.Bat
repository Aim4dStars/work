set M2_HOME=C:\apache-maven-3.0.5
set MAVEN_OPTS= -Xmx1024m -XX:MaxPermSize=128M -Denvironment=DEMO
 
set JAVA_HOME=C:\Java\jdk1.7.0_09
 
set GIT_HOME=c:\git
 
set PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%GIT_HOME%\bin;%PATH%
 
cd \Development\nextgen
mvn tomcat7:run
