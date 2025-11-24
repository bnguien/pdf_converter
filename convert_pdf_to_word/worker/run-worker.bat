@echo off
REM Worker startup script for Windows

echo ==========================================
echo PDF Converter Worker
echo ==========================================
echo.

REM Set database configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=pdf_converter
set DB_USER=root
set DB_PASSWORD=

REM Set worker port
set WORKER_PORT=8081

echo Database Configuration:
echo   Host: %DB_HOST%
echo   Port: %DB_PORT%
echo   Database: %DB_NAME%
echo   User: %DB_USER%
echo.
echo Worker Port: %WORKER_PORT%
echo.

REM Check if JAR exists
if not exist "target\pdf-converter-worker-1.0-SNAPSHOT.jar" (
    echo ERROR: JAR file not found!
    echo Please build the project first: mvn clean package
    pause
    exit /b 1
)

echo Starting Worker...
echo.

java -jar target\pdf-converter-worker-1.0-SNAPSHOT.jar

pause

