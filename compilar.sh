#!/bin/bash

# Compilar el proyecto
echo "Compilando el proyecto..."
mvn clean install -DskipTests

# Crear directorio de configuración
CONFIG_DIR="$HOME/.Culturarte"
if [ ! -d "$CONFIG_DIR" ]; then
    echo "Creando directorio de configuración en $CONFIG_DIR"
    mkdir -p "$CONFIG_DIR"
fi

# Crear archivo de configuración general
if [ ! -f "$CONFIG_DIR/config.properties" ]; then
    echo "Creando config.properties..."
    cat <<EOF > "$CONFIG_DIR/config.properties"
# Configuración General
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/culturarte?zeroDateTimeBehavior=CONVERT_TO_NULL
DB_USERNAME=root
DB_PASSWORD=root

# SOAP Service Host (IP de la máquina donde corre el servidor central)
SOAP_SERVICE_HOST=localhost
SOAP_SERVICE_PORT=8081
SOAP_SERVICE_CONTEXT_PATH=/soap/ws
EOF
fi

# Crear archivos específicos si no existen
if [ ! -f "$CONFIG_DIR/web-app.properties" ]; then
    touch "$CONFIG_DIR/web-app.properties"
fi
if [ ! -f "$CONFIG_DIR/soap-service.properties" ]; then
    touch "$CONFIG_DIR/soap-service.properties"
fi
if [ ! -f "$CONFIG_DIR/desktop-gui.properties" ]; then
    touch "$CONFIG_DIR/desktop-gui.properties"
fi

echo "Compilación completada y configuración inicializada."
