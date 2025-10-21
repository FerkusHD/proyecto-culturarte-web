#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
WEB_URL="http://localhost:8080/"
TIMEOUT=120
SLEEP=3

have() { command -v "$1" >/dev/null 2>&1; }

wait_for_web() {
  echo "[open-jar] Waiting for web at ${WEB_URL} (timeout ${TIMEOUT}s)..."
  local elapsed=0
  while true; do
    if have curl && curl -fsS "$WEB_URL" >/dev/null 2>&1; then
      echo "[open-jar] Web is up."
      return 0
    fi
    sleep "$SLEEP"
    elapsed=$((elapsed + SLEEP))
    echo "[open-jar] Still waiting... (${elapsed}s)"
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
      echo "[open-jar] Timeout waiting for web. Check 'docker compose logs -f web' or run the web manually."
      return 1
    fi
  done
}

if have docker && docker compose version >/dev/null 2>&1; then
  echo "[open-jar] Starting DB + Web with Docker Compose..."
  (cd "$ROOT_DIR" && docker compose up -d db web)
  wait_for_web || true
else
  echo "[open-jar] Docker Compose not found. Skipping containerized DB/Web startup."
  echo "[open-jar] Make sure your DB and Web are running locally before launching the app."
fi

export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djava.awt.headless=false"

# Use a single uploads folder at repo root for both JAR and WEB when running locally
UPLOADS_DIR="$ROOT_DIR/uploads"
mkdir -p "$UPLOADS_DIR"
export APP_UPLOADS_DIR="$UPLOADS_DIR"

echo "[open-jar] Launching Swing app (JAR) locally... (uploads: $UPLOADS_DIR)"
exec mvn -q -f "$ROOT_DIR/jar/pom.xml" spring-boot:run -Dspring-boot.run.jvmArguments="-Djava.awt.headless=false -Dapp.uploads.dir=$UPLOADS_DIR"