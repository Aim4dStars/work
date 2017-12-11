FROM docker.cloud.btfin-dev.com/panorama/tomcat-base

MAINTAINER "Julian Gamble <julian.gamble@btfinancialgroup.com>"

COPY target/nextgen.war /usr/local/tomcat/webapps/
ADD src/main/resources/webservices /usr/local/tomcat/lib/webservices

ARG build_label
LABEL build.label=${build_label}
