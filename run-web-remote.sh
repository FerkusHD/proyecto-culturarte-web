#!/bin/bash

# IP del Servidor Central (CAMBIAR ESTA IP)
CENTRAL_IP="192.168.1.100"

echo "Iniciando Web App conectada al servidor central en $CENTRAL_IP..."

# Configurar variables de entorno
export SOAP_SERVICE_HOST=$CENTRAL_IP
export DB_URL="jdbc:mysql://$CENTRAL_IP:3306/culturarte?zeroDateTimeBehavior=CONVERT_TO_NULL"
export DB_USERNAME=root
export DB_PASSWORD=root

# Verificar si el WAR existe
WEB_WAR=$(find web-app/target -name "proyecto-culturarte-web-*.war" | head -n 1)

if [ -z "$WEB_WAR" ]; then
    echo "Error: No se encontró el WAR de web-app. Ejecuta ./compilar.sh primero."
    exit 1
fi

echo "Iniciando Web App ($WEB_WAR)..."
echo "Conectando a SOAP Service en: http://$CENTRAL_IP:8081/soap/ws"
echo "Conectando a Base de Datos en: $CENTRAL_IP:3306"
java -jar "$WEB_WAR"
