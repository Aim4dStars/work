#!/bin/bash
set -e


DEFAULT_VOLUMES_FROM="--volumes-from $(hostname)"
VOLUMES_FROM=${DEFAULT_VOLUMES_FROM}

DOCKER_IMAGE=docker.cloud.btfin-dev.com/m035801/sourceanalyzer:v10
docker pull ${DOCKER_IMAGE}

PROJECT=$1
SOURCE_ANALYZER="/opt/HP_Fortify/HP_Fortify_SCA_and_Apps_16.11/bin/sourceanalyzer"
CLEAN_COMMAND="$SOURCE_ANALYZER -b $PROJECT -clean"
TRANSLATE_COMMAND="DEFAULT TRANSLATE COMMAND"
SCAN_COMMAND="DEFAULT SCAN_COMMAND"
MEMORY_SETTINGS="-Xmx10g -Xms2g -Xss4g"
LARGE_MEMORY_SETTINGS="-Xmx15g -Xms8g -Xss4g"
FPR_SCAN_FILENAME=${PROJECT}.fpr


echo "$PROJECT -- start of fortify processing"

# Nextgen monolith scan commands
if [ "$PROJECT" == "APIServer-Dev" -o "$PROJECT" == "APIServer" ]; then
    TRANSLATE_COMMAND="$SOURCE_ANALYZER -b $PROJECT -64 $MEMORY_SETTINGS -exclude 'src/test/**/*' -exclude 'src/main/resources/webservices/**/*' -verbose -debug-verbose -logfile translate.log -source \""1.7\"" -sql-language PL/SQL mvn clean package com.hpe.security.fortify.maven.plugin:sca-maven-plugin:16.10:translate -DskipTests=true"
    SCAN_COMMAND="$SOURCE_ANALYZER -verbose -b $PROJECT -64 $MEMORY_SETTINGS  -build-label nextgen -scan -f $FPR_SCAN_FILENAME -debug-verbose -logfile scan.log"
fi

# Mobile web client scan commands
if [ "$PROJECT" == "MobileClient-WebView-Dev" -o "$PROJECT" == "MobileClient-WebView" ]; then
    TRANSLATE_COMMAND="$SOURCE_ANALYZER -b $PROJECT . $MEMORY_SETTINGS -exclude 'app/mocks/**/*' -exclude 'test/**/*' -exclude 'platforms/**/*' -exclude 'plugins/**/*' -exclude 'patches/debug/**/*' -exclude 'mock/**/*' -exclude 'Gruntfile.js' -verbose -debug-verbose -logfile translate-web.log"
    SCAN_COMMAND="$SOURCE_ANALYZER $MEMORY_SETTINGS -b $PROJECT -scan -f $FPR_SCAN_FILENAME -verbose -debug-verbose -logfile scan-web.log"
    #ReportGenerator $MEMORY_SETTINGS -format pdf -f "artifacts/fortify/MobileClient-WebView-Dev.pdf" -template OWASP2013.xml -source $FPR_SCAN_FILENAME
fi

# Mobile android client scan commands
if [ "$PROJECT" == "MobileClient-Android-Dev" -o "$PROJECT" == "MobileClient-Android" ]; then
    TRANSLATE_COMMAND="$SOURCE_ANALYZER $MEMORY_SETTINGS -b $PROJECT -cp \""platforms/android/libs/**/*.jar\"" -extdirs \""platforms/android/build:extras/google/m2repository:extras/android/m2repository:platforms/android-22\"" -source \""1.7\"" \""platforms/android/src\"" -debug-verbose -logfile translate.log"
    SCAN_COMMAND="$SOURCE_ANALYZER $MEMORY_SETTINGS -b $PROJECT -scan -f $FPR_SCAN_FILENAME -debug-verbose -logfile scan.log"
    #ReportGenerator -Xmx8G -Xms8G -Xss8G -format pdf -f "artifacts/fortify/MobileClient-Android-Dev.pdf" -template OWASP2013.xml -source $FPR_SCAN_FILENAME
fi

# Web client scan commands
if [ "$PROJECT" == "WebClient-Dev" -o "$PROJECT" == "WebClient" ]; then
    TRANSLATE_COMMAND="$SOURCE_ANALYZER -b $PROJECT . -64 $LARGE_MEMORY_SETTINGS -exclude 'webapp/mock/**/*' -exclude 'webapp/test/**/*' -exclude 'webapp/libs/**/*' -source \""1.7\"" -exclude 'webapp/vendors/**/*' -exclude 'node_modules/**/*' -exclude 'grunt/**/*' -debug-verbose -verbose -logfile translate.log"
    SCAN_COMMAND="$SOURCE_ANALYZER -b $PROJECT -verbose -64 $LARGE_MEMORY_SETTINGS -scan -f $FPR_SCAN_FILENAME -debug-verbose -logfile scan.log"
fi

# NW scan commands
if [ "$PROJECT" == "WebClient-NW-Dev" -o "$PROJECT" == "WebClient-NW" ]; then
    TRANSLATE_COMMAND="$SOURCE_ANALYZER -b $PROJECT src -64 $LARGE_MEMORY_SETTINGS -exclude '**/*.spec.js' -source \""1.7\"" -debug-verbose -verbose -logfile translate.log"
    SCAN_COMMAND="$SOURCE_ANALYZER -b $PROJECT -verbose -64 $LARGE_MEMORY_SETTINGS -scan -f $FPR_SCAN_FILENAME -debug-verbose -logfile scan.log"
fi


echo "$CLEAN_COMMAND"
echo "$TRANSLATE_COMMAND"
echo "$SCAN_COMMAND"


echo "tweaks: volumes:>${VOLUMES_FROM}<"
echo "pwd: $(pwd)"
docker run -i -e APP_ID=$(cat /etc/app-id) -w $(pwd) -e WORK_DIR=$(pwd) -e LOCAL_REPOSITORY_BASE_DIR=.. -e GO_ENVIRONMENT_NAME -e GO_SERVER_URL -e GO_TRIGGER_USER -e GO_PIPELINE_NAME -e GO_PIPELINE_COUNTER -e GO_PIPELINE_LABEL -e GO_STAGE_NAME -e GO_STAGE_COUNTER -e GO_JOB_NAME -e GO_REVISION_NEXTGEN ${VOLUMES_FROM} ${DOCKER_IMAGE} bash -c "pwd; echo '-- start clean '; $CLEAN_COMMAND; echo '-- end clean '; echo '-- start translate '; $TRANSLATE_COMMAND; echo '-- end translate '; echo '-- start scan '; $SCAN_COMMAND; echo '-- end scan '"

echo "$PROJECT -- end of fortify processing"