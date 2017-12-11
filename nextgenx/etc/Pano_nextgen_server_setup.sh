#!/bin/sh
#
# Script to set up nextgen and mock-avaloq-icc-gateway properties for running nextgen against Avaloq instance
#
# Note: set your own ui-kernel path (separated by '/') using environment variable UI_KERNEL_DIR (default is 'C:/Development/ui-nw')
#

FILE_NEXTGEN_ENV=/cygdrive/c/Development/nextgen/src/main/resources/env.properties
FILE_MOCK_AVALOQ_ENV=/cygdrive/c/Development/mock-avaloq-icc-gateway/src/main/resources/nextgen-interfaces.properties
FILE_TMP=${FILE_NEXTGEN_ENV}.tmp

### FILE_NEXTGEN_ENV
FILE=$FILE_NEXTGEN_ENV
dos2unix < $FILE |\
    sed -e '/^avaloq.webservice.filestub=/s/=true/=false/' \
        -e '/^avaloqStatic.webservice.filestub=/s/=true/=false/' \
        \
        -e '/^investmentTrustDetail.webservice.filestub=/s/=true/=false/' \
        \
        -e '/^cms.filename=/s/^/#/' \
        -e '/^cms.basedir.resource=/s/^/#/' \
        -e '/^#cms.filename=file:\/\/\/c:\/Development/s/^#//' \
        -e '/^#cms.basedir.resource=file:\/\/\/c:\/Development/s/^#//' \
        \
        -e '/^webclient.resource.location=/s#=.*#=file:///'"${UI_KERNEL_DIR-C:/Development/ui-nw}"'/ui-kernel/dist/#' \
        | unix2dos \
        > $FILE_TMP

mv $FILE_TMP $FILE


### FILE_MOCK_AVALOQ_ENV
FILE=$FILE_MOCK_AVALOQ_ENV
dos2unix < $FILE |\
    sed -e '/^responseLog.enable=/s/=false/=true/' \
        -e '/^responseLog.maxFiles=/s/=50/=100/' \
        | unix2dos \
        > $FILE_TMP

mv $FILE_TMP $FILE


exit 0