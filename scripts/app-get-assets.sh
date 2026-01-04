#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
ROOT=${SCRIPT_DIR}/..

if [[ -z $ROOT/app/public/fontawesome-free-7.1.0-web ]]; then
    wget -O $ROOT/fa.tgz https://use.fontawesome.com/releases/v7.1.0/fontawesome-free-7.1.0-web.zip
    unzip -o -d $ROOT/app/public $ROOT/fa.tgz
    rm $ROOT/fa.tgz
else
    echo "Font awesome is already downloaded"
fi

if [[ -z $ROOT/app/public/bootstrap-5.3.8-dist ]]; then
    wget -O $ROOT/bootstrap.tgz https://github.com/twbs/bootstrap/releases/download/v5.3.8/bootstrap-5.3.8-dist.zip
    unzip -o -d $ROOT/app/public $ROOT/bootstrap.tgz
    rm $ROOT/bootstrap.tgz 
else
    echo "Bootstrap is already downloaded"
fi
