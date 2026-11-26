@echo off
title LANCEUR GLOBAL DRIVEEASY BACKEND
color 0E
echo ==========================================================
echo        DEMARRAGE AUTOMATIQUE DE LA PLATEFORME DRIVEEASY
echo ==========================================================
echo.

echo [1/6] Demarrage d'Eureka (service-registry - 8761)...
start "1. Eureka (8761)" cmd /k "cd /d ""%~dp0\service-registry"" && java -jar target\service-registry-0.0.1-SNAPSHOT.jar"
timeout /t 10 /nobreak >nul

echo [2/6] Demarrage du Config Server (8888)...
start "2. Config Server (8888)" cmd /k "cd /d ""%~dp0\config-server"" && java -jar target\config-server-0.0.1-SNAPSHOT.jar"
timeout /t 8 /nobreak >nul

echo [3/6] Demarrage du Vehicule Service (8081)...
start "3. Vehicule Service (8081)" cmd /k "cd /d ""%~dp0\vehicule-service"" && java -jar target\vehicule-service-0.0.1-SNAPSHOT.jar"

echo [4/6] Demarrage du Client Service (8082)...
start "4. Client Service (8082)" cmd /k "cd /d ""%~dp0\client-service"" && java -jar target\client-service-0.0.1-SNAPSHOT.jar"

echo [5/6] Demarrage du Location Service (8083)...
start "5. Location Service (8083)" cmd /k "cd /d ""%~dp0\location-service"" && java -jar target\location-service-0.0.1-SNAPSHOT.jar"
timeout /t 5 /nobreak >nul

echo [6/6] Demarrage de l'API Gateway (8080)...
start "6. API Gateway (8080)" cmd /k "cd /d ""%~dp0\api-gateway"" && java -jar target\api-gateway-0.0.1-SNAPSHOT.jar"

echo.
echo ==========================================================
echo   TOUS LES MICROSERVICES SONT EN COURS DE LANCEMENT !
echo   - Eureka Dashboard : http://localhost:8761
echo   - API Gateway      : http://localhost:8080
echo ==========================================================
pause
