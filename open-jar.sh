#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
WEB_URL="http://localhost:8080/"
SOAP_URL="http://localhost:8081/ws"
TIMEOUT=120
SLEEP=3

have() { command -v "$1" >/dev/null 2>&1; }

wait_for_url() {
  local url=$1
  local name=$2
  echo "[open-jar] Waiting for $name at ${url} (timeout ${TIMEOUT}s)..."
  local elapsed=0
  while true; do
    if have curl && curl -fsS "$url" >/dev/null 2>&1; then
      echo "[open-jar] $name is up."
      return 0
    fi
    sleep "$SLEEP"
    elapsed=$((elapsed + SLEEP))
    echo "[open-jar] Still waiting for $name... (${elapsed}s)"
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
      echo "[open-jar] Timeout waiting for $name. Check logs or run it manually."
      return 1
    fi
  done
}

if have docker && docker compose version >/dev/null 2>&1; then
  echo "[open-jar] Starting DB with Docker Compose..."
  (cd "$ROOT_DIR" && docker compose up -d db)
  sleep 5  # Give DB time to initialize
else
  echo "[open-jar] Docker Compose not found. Skipping containerized DB startup."
  echo "[open-jar] Make sure your DB is running locally before continuing."
fi

# Start SOAP service
echo "[open-jar] Starting SOAP service..."
mvn -q -f "$ROOT_DIR/soap-service/pom.xml" spring-boot:run -Dserver.port=8081 &
SOAP_PID=$!
wait_for_url "$SOAP_URL" "SOAP service" || true

# Start Web app
echo "[open-jar] Starting Web application..."
mvn -q -f "$ROOT_DIR/web-app/pom.xml" spring-boot:run -Dserver.port=8080 &
WEB_PID=$!
wait_for_url "$WEB_URL" "Web app" || true

# Setup for desktop app
UPLOADS_DIR="$ROOT_DIR/uploads"
mkdir -p "$UPLOADS_DIR"
export APP_UPLOADS_DIR="$UPLOADS_DIR"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djava.awt.headless=false"

echo "[open-jar] Building and launching Swing app (JAR)..."
mvn -q -f "$ROOT_DIR/pom.xml" -pl desktop-gui -am install -DskipTests -Djacoco.skip=true

# Trap to kill background processes on script exit
trap 'kill $SOAP_PID $WEB_PID 2>/dev/null || true' EXIT

# Run desktop app
exec mvn -q -f "$ROOT_DIR/desktop-gui/pom.xml" spring-boot:run \
    -Dspring-boot.run.mainClass=com.culturarte.DesktopGuiApplication \
    -Dspring-boot.run.jvmArguments="-Djava.awt.headless=false -Dapp.uploads.dir=$UPLOADS_DIR"