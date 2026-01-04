#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
ROOT=${SCRIPT_DIR}/..

    # -it \
    # --entrypoint  /bin/bash \

docker run \
    -e KEYCLOAK_URL="http://172.17.0.1:9000/" \
    -e KEYCLOAK_USER="admin" \
    -e KEYCLOAK_PASSWORD="admin" \
    -e KEYCLOAK_AVAILABILITYCHECK_ENABLED=true \
    -e KEYCLOAK_AVAILABILITYCHECK_TIMEOUT=120s \
    -e IMPORT_FILES_LOCATIONS='/config/*' \
    -v $ROOT/keycloak-config:/config \
    adorsys/keycloak-config-cli:latest
