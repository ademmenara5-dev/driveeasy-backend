@echo off
title 1. EUREKA SERVER (Port 8761)
color 0A
echo ==================================================
echo   LANCEMENT DU SERVICE REGISTRY (EUREKA - 8761)
echo ==================================================
cd /d "%~dp0\service-registry"
java -jar target\service-registry-0.0.1-SNAPSHOT.jar
pause
