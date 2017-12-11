#!/bin/bash

set -e

# This is now mastered in ssh://git@stash.btfin.com/os/build-tools.git/build-tools/sonar-publish-version.sh

curl -##L http://consul.cloud.btfin-dev.com:8500/v1/kv/application/devops/latest/sonar-publish-version.sh?raw > sonar-publish-version.sh
chmod +x sonar-publish-version.sh
ls -al
./sonar-publish-version.sh $GO_PIPELINE_LABEL

