#!/bin/bash

echo "Iniciando Web App..."

# Verificar si el WAR existe
WEB_WAR=$(find web-app/target -name "web-app-*.war" | head -n 1)

if [ -z "$WEB_WAR" ]; then
    echo "Error: No se encontró el WAR de web-app. Ejecuta ./compilar.sh primero."
    exit 1
fi

echo "Iniciando Web App ($WEB_WAR)..."
java -jar "$WEB_WAR"
