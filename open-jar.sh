#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SOAP_URL="http://localhost:8081/soap/ws/categorias.wsdl"
WEB_URL="http://localhost:8080/"
TIMEOUT=120
SLEEP=3

have() { command -v "$1" >/dev/null 2>&1; }

# Detectar el comando de docker compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker compose"
else
    echo "Error: No se encontró docker-compose ni docker compose."
    DOCKER_COMPOSE_CMD=""
fi

wait_for_service() {
  local url=$1
  local service_name=$2
  echo "[open-jar] Waiting for ${service_name} at ${url} (timeout ${TIMEOUT}s)..."
  local elapsed=0
  while true; do
    if have curl && curl -fsS "$url" >/dev/null 2>&1; then
      echo "[open-jar] ${service_name} is up."
      return 0
    fi
    sleep "$SLEEP"
    elapsed=$((elapsed + SLEEP))
    echo "[open-jar] Still waiting for ${service_name}... (${elapsed}s)"
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
      echo "[open-jar] ⚠️  Timeout waiting for ${service_name}. Check '${DOCKER_COMPOSE_CMD} logs -f ${service_name}' or run it manually."
      return 1
    fi
  done
}

if [ -n "$DOCKER_COMPOSE_CMD" ]; then
  echo "[open-jar] Cleaning up old containers and images..."
  (cd "$ROOT_DIR" && $DOCKER_COMPOSE_CMD down --remove-orphans --rmi local)

  echo "[open-jar] Starting DB + SOAP + Web with Docker Compose (Force Build)..."
  (cd "$ROOT_DIR" && $DOCKER_COMPOSE_CMD up -d --build --force-recreate db soap web)
  
  echo "[open-jar] Waiting for services to be ready..."
  wait_for_service "$SOAP_URL" "SOAP" || echo "[open-jar] ⚠️  SOAP service not ready, but continuing..."
  wait_for_service "$WEB_URL" "Web" || echo "[open-jar] ⚠️  Web service not ready, but continuing..."
else
  echo "[open-jar] Docker Compose not found. Skipping containerized DB/SOAP/Web startup."
  echo "[open-jar] Make sure your DB, SOAP service, and Web are running locally before launching the app."
fi

export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djava.awt.headless=false"

UPLOADS_DIR="$ROOT_DIR/uploads"
mkdir -p "$UPLOADS_DIR"
export APP_UPLOADS_DIR="$UPLOADS_DIR"

echo "[open-jar] Launching Swing app (JAR) locally... (uploads: $UPLOADS_DIR)"

# Build desktop-gui specifically
mvn -q -f "$ROOT_DIR/pom.xml" -pl desktop-gui -am install -DskipTests -Djacoco.skip=true

exec mvn -q -f "$ROOT_DIR/desktop-gui/pom.xml" spring-boot:run -Dspring-boot.run.mainClass=com.culturarte.DesktopGuiApplication -Dspring-boot.run.jvmArguments="-Djava.awt.headless=false -Dapp.uploads.dir=$UPLOADS_DIR"