#!/bin/sh
# Starts the iBank ABM simulator.
cd "$(dirname "$0")"
[ -d out/classes ] || ./build.sh
java -cp out/classes ibank.App
