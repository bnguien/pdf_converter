#!/bin/bash

# Worker startup script for Linux/Mac

echo "=========================================="
echo "PDF Converter Worker"
echo "=========================================="
echo ""

# Set database configuration
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=pdf_converter
export DB_USER=root
export DB_PASSWORD=""

# Set worker port
export WORKER_PORT=8081

echo "Database Configuration:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""
echo "Worker Port: $WORKER_PORT"
echo ""

# Check if JAR exists
if [ ! -f "target/pdf-converter-worker-1.0-SNAPSHOT.jar" ]; then
    echo "ERROR: JAR file not found!"
    echo "Please build the project first: mvn clean package"
    exit 1
fi

echo "Starting Worker..."
echo ""

java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar

