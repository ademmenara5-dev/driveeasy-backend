@echo off
title LANCEUR GLOBAL DRIVEEASY (DEMO PROF)
color 0B
echo ==========================================================
echo        DEMARRAGE COMPLET DE LA PLATEFORME DRIVEEASY
echo              (Architecture Microservices & Angular)
echo ==========================================================
echo.

echo [1/7] Lancement de Eureka (service-registry - Port 8761)...
start "1. Eureka (8761)" cmd /k "cd /d ""%~dp0\service-registry"" && java -jar target\service-registry-0.0.1-SNAPSHOT.jar"
timeout /t 10 /nobreak >nul

echo [2/7] Lancement du Config Server (Port 8888)...
start "2. Config Server (8888)" cmd /k "cd /d ""%~dp0\config-server"" && java -jar target\config-server-0.0.1-SNAPSHOT.jar"
timeout /t 8 /nobreak >nul

echo [3/7] Lancement du Vehicule Service (Port 8081)...
start "3. Vehicule Service (8081)" cmd /k "cd /d ""%~dp0\vehicule-service"" && java -jar target\vehicule-service-0.0.1-SNAPSHOT.jar"

echo [4/7] Lancement du Client Service (Port 8082)...
start "4. Client Service (8082)" cmd /k "cd /d ""%~dp0\client-service"" && java -jar target\client-service-0.0.1-SNAPSHOT.jar"

echo [5/7] Lancement du Location Service (Port 8083)...
start "5. Location Service (8083)" cmd /k "cd /d ""%~dp0\location-service"" && java -jar target\location-service-0.0.1-SNAPSHOT.jar"
timeout /t 5 /nobreak >nul

echo [6/7] Lancement de l'API Gateway (Port 8080)...
start "6. API Gateway (8080)" cmd /k "cd /d ""%~dp0\api-gateway"" && java -jar target\api-gateway-0.0.1-SNAPSHOT.jar"
timeout /t 5 /nobreak >nul

echo [7/7] Lancement du Frontend Angular (Port 4200)...
start "7. Angular Frontend (4200)" cmd /k "cd /d ""D:\unity\driveeasy-frontend"" && npm start"

echo.
echo ==========================================================
echo   TOUT EST LANCE ! OUVERTURE DES ONGLETS DANS LE NAVIGATEUR...
echo ==========================================================
timeout /t 10 /nobreak >nul

start http://localhost:8761
start http://localhost:4200

echo.
echo C'est pret ! Bonne presentation !
pause
