#!/bin/sh
# Compiles and runs the JUnit 5 tests from the command line.
set -e
cd "$(dirname "$0")"
./build.sh
JUNIT=lib/junit-platform-console-standalone-1.9.1.jar
mkdir -p out/test-classes
find src/test/java -name '*.java' > out/test-sources.txt
javac --release 11 -encoding UTF-8 -cp out/classes:$JUNIT -d out/test-classes @out/test-sources.txt
java -jar $JUNIT --class-path out/classes:out/test-classes --scan-class-path \
  --details=tree --reports-dir=out/test-reports
