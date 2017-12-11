#!/bin/bash

set -e

# This is now mastered in ssh://git@stash.btfin.com/os/build-tools.git/build-tools/sonar-util.sh

curl -##L http://consul.cloud.btfin-dev.com:8500/v1/kv/application/devops/latest/sonar-util.sh?raw | sh -s -- $GO_PIPELINE_LABEL

