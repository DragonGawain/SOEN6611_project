@echo off
rem Starts the iBank ABM simulator.
cd /d "%~dp0"
if not exist out\classes call build.bat
java -cp out\classes ibank.App
