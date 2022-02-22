#!/bin/bash

set -e

display_usage() {
  echo -e "\n This script build image and push it to registry."
  echo -e "\nUSAGE:\n\n ${0} <tag> \n"
}

if [ $# -lt 1 ]; then
  display_usage
  exit 1
fi

TAG_NAME=${1}
SERVICE_NAME="investments"

WORK_DIR=$(
  cd "$(dirname "$0")"
  cd ..
  pwd
)

cd "$WORK_DIR" || exit
echo "$WORK_DIR"

./gradlew clean buildDependents
./gradlew bootBuildImage --imageName=${SERVICE_NAME}/${TAG_NAME} || true
echo "IMAGE CREATED"
