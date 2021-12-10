#!/bin/bash

set -e

display_usage() {
    echo -e "\n This script deploys a service to the k8s namespace."
    echo -e "\nUSAGE:\n\n ${0} <namespace> [tag] \n"
    echo -e "tag - custom name image tag, if empty use namespace by default "
}

if [ $# -lt 1 ]; then
    display_usage
    exit 1
fi

TAG_NAME=${1}

if ! test -z "${2}"
then
  TAG_NAME=${2}
fi

NAMESPACE=${1}
SERVICE_NAME="notifications"

SCRIPT_PATH="$(
    cd "$(dirname "$0")"
    pwd -P
)"

echo "${SCRIPT_PATH}"
cd "${SCRIPT_PATH}"
../build-image.sh $TAG_NAME

kubectl config set-context $NAMESPACE --namespace=$NAMESPACE --cluster=docker-desktop --user=docker-desktop || true
kubectl config use-context $NAMESPACE

helm delete ${SERVICE_NAME} || true
helm dependency update ${SCRIPT_PATH}/chart

helm install ${SERVICE_NAME} --create-namespace --namespace $NAMESPACE \
    --set "image=${SERVICE_NAME}" \
    --set "namespace.full=${NAMESPACE}" \
    --set "namespace.short=${NAMESPACE:0:3}" \
    --set "imageTag=${TAG_NAME}" \
    --set "name=${SERVICE_NAME}" \
    --set "spring.profiles.active=${NAMESPACE}" \
    --set "spring.cloud.config.label=${NAMESPACE}" \
    ${SCRIPT_PATH}/chart || true
cd $SCRIPT_PATH