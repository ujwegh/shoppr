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

WORK_DIR=$(
  cd "$(dirname "$0")"
  cd ..
  pwd
)

REGISTRY_URL="127.0.0.1:5000"
TAG_NAME=${1}
SERVICE_NAME="auth-service"
echo "$WORK_DIR"

cd "$WORK_DIR" || exit
echo "$WORK_DIR"
./gradlew clean buildDependents
./gradlew jib -Pregistry_url=$REGISTRY_URL -Pimage_tag=$TAG_NAME -Djib.allowInsecureRegistries=true --stacktrace || true

echo "IMAGE: ${REGISTRY_URL}/${SERVICE_NAME}:${TAG_NAME}"
