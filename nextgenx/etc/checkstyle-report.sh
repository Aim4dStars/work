#!/bin/bash

set -e

# This is now mastered in ssh://git@stash.btfin.com/os/build-tools.git/build-tools/checkstyle-report.sh

curl -##L http://consul.cloud.btfin-dev.com:8500/v1/kv/application/devops/latest/checkstyle-report.sh?raw > checkstyle-report.sh
chmod +x checkstyle-report.sh
ls -al
./checkstyle-report.sh 

