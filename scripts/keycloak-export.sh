
#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
ROOT=${SCRIPT_DIR}/..

CONTAINER_NAME=control-booth-keycloak

# Export the 
docker exec -ti $CONTAINER_NAME  bash -c "mkdir -p /opt/keycloak/export && /opt/keycloak/bin/kc.sh export --dir /opt/keycloak/export/ --realm control-booth"
docker cp $CONTAINER_NAME:/opt/keycloak/export/control-booth-realm.json $ROOT/keycloak-export/.
docker cp $CONTAINER_NAME:/opt/keycloak/export/control-booth-users-0.json $ROOT/keycloak-export/.

