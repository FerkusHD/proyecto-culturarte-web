#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SOAP_URL="http://localhost:8081/soap/ws/categorias.wsdl"
WEB_URL="http://localhost:8080/"
TIMEOUT=120
SLEEP=3

have() { command -v "$1" >/dev/null 2>&1; }

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
      echo "[open-jar] ⚠️  Timeout waiting for ${service_name}. Check 'docker compose logs -f ${service_name}' or run it manually."
      return 1
    fi
  done
}

if have docker && docker compose version >/dev/null 2>&1; then
  (cd "$ROOT_DIR" && docker compose up -d --build db soap web)
  echo "[open-jar] Starting DB + SOAP + Web with Docker Compose..."
  (cd "$ROOT_DIR" && docker compose up -d db soap web)
  
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

mvn -q -f "$ROOT_DIR/pom.xml" -pl desktop-gui -am install -DskipTests -Djacoco.skip=true

exec mvn -q -f "$ROOT_DIR/desktop-gui/pom.xml" spring-boot:run -Dspring-boot.run.mainClass=com.culturarte.DesktopGuiApplication -Dspring-boot.run.jvmArguments="-Djava.awt.headless=false -Dapp.uploads.dir=$UPLOADS_DIR"