@echo off
rem Compiles and runs the JUnit 5 tests from the command line.
cd /d "%~dp0"
call build.bat || exit /b 1
set JUNIT=lib\junit-platform-console-standalone-1.9.1.jar
mkdir out\test-classes
dir /s /b src\test\java\*.java > out\test-sources.txt
javac --release 11 -encoding UTF-8 -cp out\classes;%JUNIT% -d out\test-classes @out\test-sources.txt || exit /b 1
java -jar %JUNIT% --class-path out\classes;out\test-classes --scan-class-path --details=tree --reports-dir=out\test-reports
