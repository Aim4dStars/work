#!/bin/sh
export MAVEN_OPTS="-Djavax.net.ssl.keyStore=src/test/resources/keystore/dwgps0022.btfin.com.jks -Djavax.net.ssl.trustStore=src/test/resources/keystore/dwgps0022.btfin.com.jks -Djavax.net.ssl.trustStorePassword=YTMvhQq7YvwwawDh -Djavax.net.ssl.keyStorePassword=YTMvhQq7YvwwawDh"

mvn -Denvironment=CI -Dtimeout=60 -Pci -Dserver.port=$server_port -Ddev.tools.home=$dev_tools clean verify
