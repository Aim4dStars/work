#!/bin/bash

set -e

# Use environment variables to substitute vaules

if [ ! "${SHARED_LOADER}" == "" ]
then
  echo "Updating shared loader to:  ${SHARED_LOADER}..."
  $(echo "sed -i -r s|shared.loader=.*|shared.loader=${SHARED_LOADER}|g") /usr/local/tomcat/conf/catalina.properties
fi

if [ "${WAIT_FOR_ABS}" == "true" ]
then
  echo "Waiting for ABS enabled..."
  until nc -z abs 1522
  do
    echo "Nothing on abs:1522..."
    sleep 60
  done
else
  echo "Not waiting for abs, current status is"
  nc -vz abs 1522 || /bin/true
fi

# Run origin cmd
exec catalina.sh run
