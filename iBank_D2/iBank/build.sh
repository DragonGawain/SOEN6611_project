#!/bin/sh
# Compiles iBank with the JDK only. No IDE or build tool is needed. Requires JDK 11 or later.
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir -p out/classes
find src/main/java -name '*.java' > out/sources.txt
javac --release 11 -encoding UTF-8 -Xlint:unchecked -Xlint:deprecation -d out/classes @out/sources.txt
cp -R src/main/resources/. out/classes/
echo "Built into out/classes. Run with ./run.sh"
