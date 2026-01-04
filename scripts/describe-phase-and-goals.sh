#!/usr/bin/env bash


echo "===================================="
echo "PHASES"
echo "===================================="
mvn -B help:describe -Dcmd=compile \
      | grep -v "^\["

echo "===================================="
echo "exec-maven-plugin GOALS"
echo "===================================="
mvn -B help:describe -DgroupId=org.codehaus.mojo \
      -DartifactId=exec-maven-plugin \
      -Ddetail=false \
      | grep -v "^\["
