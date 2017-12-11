#!/bin/bash

set -e

mvn -Pci verify && /sonar/bin/sonar-runner
