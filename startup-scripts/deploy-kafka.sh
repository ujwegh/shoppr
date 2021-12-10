#!/bin/bash

set -e

DEPLOYMENT_NAME="kafka"
NAMESPACE=${1}
DEPLOYMENT_SUFFIX=${NAMESPACE:0:3}

display_usage() {
    echo -e "\n This script deploys kafka pod to specified k8s namespace."
    echo -e "\nUSAGE:\n\n ${0} <namespace>"
}

if [ $# -lt 1 ]; then
    display_usage
    exit 1
fi

if [ $(helm repo list | grep https://charts.bitnami.com/bitnami | wc -l) == '0' ]
then
        echo '----------------------------------------------------------------------------'
        echo 'adding missing helm repository with kafka chart'
        helm repo add bitnami https://charts.bitnami.com/bitnami
        helm repo update
        echo '----------------------------------------------------------------------------'
fi

kubectl config set-context $NAMESPACE --namespace=$NAMESPACE --cluster=docker-desktop --user=docker-desktop || true
kubectl config use-context $NAMESPACE

helm delete ${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX} || true
helm install ${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX} --create-namespace --namespace $NAMESPACE \
    bitnami/kafka

echo Wait ${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX}-0
