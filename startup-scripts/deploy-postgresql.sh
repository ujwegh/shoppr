#!/bin/bash

set -e

DEPLOYMENT_NAME="db"
NAMESPACE=${1}
DEPLOYMENT_SUFFIX=${NAMESPACE:0:3}

display_usage() {
    echo -e "\n This script deploys postgresql database pod to specified k8s namespace."
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

echo Deleting PVC: data-${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX}-postgresql-0
kubectl delete pvc data-${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX}-postgresql-0 || true

helm install \
    ${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX} --create-namespace --namespace $NAMESPACE \
    --set "name=${DEPLOYMENT_NAME}" \
    -f values-postgresql.yaml \
    bitnami/postgresql
sleep 500

echo Wait ${DEPLOYMENT_NAME}-${DEPLOYMENT_SUFFIX}-postgresql-0
