@echo off
title 2. CONFIG SERVER (Port 8888)
color 0B
echo ==================================================
echo   LANCEMENT DU CONFIG SERVER (Port 8888)
echo ==================================================
cd /d "%~dp0\config-server"
java -jar target\config-server-0.0.1-SNAPSHOT.jar
pause
