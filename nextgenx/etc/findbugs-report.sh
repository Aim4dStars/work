#!/bin/bash

set -e

# This is now mastered in ssh://git@stash.btfin.com/os/build-tools.git/build-tools/findbugs-report.sh

curl -##L http://consul.cloud.btfin-dev.com:8500/v1/kv/application/devops/latest/findbugs-report.sh?raw > findbugs-report.sh
chmod +x findbugs-report.sh
ls -al
./findbugs-report.sh $GO_PIPELINE_LABEL

