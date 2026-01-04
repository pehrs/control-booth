#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
ROOT=${SCRIPT_DIR}/..

LOGO_PNG=$(echo -n "data:image/png;base64,$(cat ${ROOT}/app/public/logo.png | base64 | tr -d '\r\n')" )
ARCH_PNG=$(echo -n "data:image/png;base64,$(cat ${ROOT}/docs/architecture.png | base64 | tr -d '\r\n')" )
CATALOG_PNG=$(echo -n "data:image/png;base64,$(cat ${ROOT}/docs/catalog-entities.png | base64 | tr -d '\r\n')" )


mkdir -p ${ROOT}/target/classes
src=${ROOT}/src/main/resources/about.md
target=${ROOT}/target/classes/about-rendered.md
cp $src $target

LOGO_ED_PNG=$(echo "$LOGO_PNG" | sed 's/\//\\\//g')
ARCH_ED_PNG=$(echo "$ARCH_PNG" | sed 's/\//\\\//g')
CATALOG_ED_PNG=$(echo "$CATALOG_PNG" | sed 's/\//\\\//g')

ed_cmd=$(mktemp)

# We use ED here as we need support for really long strings (which sed does not support)
touch $ed_cmd
echo "%s/src=\"[^\"]*\" alt=\"logo\"/src=\"$LOGO_ED_PNG\" alt=\"logo\"/g" >> $ed_cmd
echo "%s/src=\"[^\"]*\" alt=\"architecture\"/src=\"$ARCH_ED_PNG\" alt=\"architecture\"/g" >> $ed_cmd
echo "%s/src=\"[^\"]*\" alt=\"catalog-entities\"/src=\"$CATALOG_ED_PNG\" alt=\"catalog-entities\"/g" >> $ed_cmd
echo "w" >> $ed_cmd
echo "q" >> $ed_cmd
# Do the edits
< $ed_cmd ed $target

rm $ed_cmd
    
