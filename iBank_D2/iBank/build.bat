@echo off
rem Compiles iBank with the JDK only. Requires JDK 11 or later.
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out\classes
dir /s /b src\main\java\*.java > out\sources.txt
javac --release 11 -encoding UTF-8 -Xlint:unchecked -Xlint:deprecation -d out\classes @out\sources.txt || exit /b 1
xcopy /e /i /q src\main\resources out\classes > nul
echo Built into out\classes. Run with run.bat
