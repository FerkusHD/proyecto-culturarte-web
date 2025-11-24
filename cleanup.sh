#!/usr/bin/env bash
set -euo pipefail

echo "🧹 Limpiando procesos del proyecto CulturArte..."

# Detectar docker compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker compose"
else
    echo "⚠️  No se encontró docker-compose ni docker compose."
    DOCKER_COMPOSE_CMD=""
fi

# Matar procesos Java del proyecto
echo "🔪 Deteniendo procesos Java del proyecto..."
pkill -f "soap-service.*jar" || true
pkill -f "proyecto-culturarte-web.*war" || true
pkill -f "app-.*jar" || true
pkill -f "desktop-gui" || true

# Esperar un momento
sleep 2

# Detener y limpiar Docker
if [ -n "$DOCKER_COMPOSE_CMD" ]; then
    echo "🐳 Deteniendo contenedores Docker..."
    $DOCKER_COMPOSE_CMD down
else
    echo "⚠️  Saltando limpieza de Docker (comando no disponible)"
fi

# Limpiar logs antiguos (opcional)
if [ -f "soap.log" ]; then
    echo "📝 Truncando soap.log..."
    > soap.log
fi

if [ -f "web.log" ]; then
    echo "📝 Truncando web.log..."
    > web.log
fi

echo "✅ Limpieza completada. Ahora puedes ejecutar ./open-jar.sh"
