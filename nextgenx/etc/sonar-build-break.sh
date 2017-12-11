#!/bin/bash

set -e

# This is now mastered in ssh://git@stash.btfin.com/os/build-tools.git/build-tools/sonar-build-breaker-toggle.sh

curl -##L http://consul.cloud.btfin-dev.com:8500/v1/kv/application/devops/latest/sonar-build-breaker-toggle.sh?raw > sonar-build-breaker-toggle.sh
chmod +x sonar-build-breaker-toggle.sh
ls -al
./sonar-build-breaker-toggle.sh $GO_PIPELINE_LABEL

