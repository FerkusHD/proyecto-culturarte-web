#!/bin/bash

echo "Iniciando componentes del Servidor Central..."

# Verificar si el JAR existe
SOAP_JAR=$(find soap-service/target -name "soap-service-*.jar" | head -n 1)
GUI_JAR=$(find desktop-gui/target -name "desktop-gui-*.jar" | head -n 1)

if [ -z "$SOAP_JAR" ]; then
    echo "Error: No se encontró el JAR de soap-service. Ejecuta ./compilar.sh primero."
    exit 1
fi

if [ -z "$GUI_JAR" ]; then
    echo "Error: No se encontró el JAR de desktop-gui. Ejecuta ./compilar.sh primero."
    exit 1
fi

# Ejecutar SOAP Service
echo "Iniciando SOAP Service ($SOAP_JAR)..."
java -jar "$SOAP_JAR" &
SOAP_PID=$!
echo "SOAP Service iniciado con PID $SOAP_PID"

# Esperar a que el servicio levante (ajustar tiempo si es necesario)
echo "Esperando 15 segundos para que el servicio SOAP esté listo..."
sleep 15

# Ejecutar Desktop GUI
echo "Iniciando Desktop GUI ($GUI_JAR)..."
java -jar "$GUI_JAR" &
GUI_PID=$!
echo "Desktop GUI iniciada con PID $GUI_PID"

# Manejar cierre
trap "kill $SOAP_PID $GUI_PID" SIGINT SIGTERM
wait
