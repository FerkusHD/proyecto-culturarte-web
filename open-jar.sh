#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SOAP_URL="http://localhost:8081/soap/ws/categorias.wsdl"
WEB_URL="http://localhost:8080/"
TIMEOUT=120
SLEEP=3

# Detectar docker compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker compose"
else
    echo "Error: No se encontró docker-compose ni docker compose."
    exit 1
fi

wait_for_service() {
  local url=$1
  local service_name=$2
  echo "[open-jar] Waiting for ${service_name} at ${url} (timeout ${TIMEOUT}s)..."
  local elapsed=0
  while true; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "[open-jar] ${service_name} is up."
      return 0
    fi
    sleep "$SLEEP"
    elapsed=$((elapsed + SLEEP))
    echo "[open-jar] Still waiting for ${service_name}... (${elapsed}s)"
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
      echo "[open-jar] ⚠️  Timeout waiting for ${service_name}."
      return 1
    fi
  done
}

# Función de limpieza
cleanup() {
    echo ""
    echo "[open-jar] 🛑 Deteniendo procesos..."
    kill $(jobs -p) 2>/dev/null || true
    echo "[open-jar] 🛑 Deteniendo base de datos..."
    (cd "$ROOT_DIR" && $DOCKER_COMPOSE_CMD stop db)
    echo "[open-jar] ✅ Todo detenido."
}
trap cleanup EXIT

echo "[open-jar] 🔨 Compilando proyecto..."
./compilar.sh

echo "[open-jar] 🗄️  Iniciando Base de Datos (Docker)..."
(cd "$ROOT_DIR" && $DOCKER_COMPOSE_CMD up -d db)

echo "[open-jar] ⏳ Esperando a que la DB esté lista..."
until (cd "$ROOT_DIR" && $DOCKER_COMPOSE_CMD exec -T db mysqladmin ping -h "localhost" --silent); do
    printf '.'
    sleep 1
done
echo ""
echo "[open-jar] ✅ DB lista."

echo "[open-jar] 🚀 Iniciando SOAP Service (Central)..."
# Buscamos el JAR con classifier exec (fat jar)
SOAP_JAR=$(find soap-service/target -name "soap-service-*-exec.jar" | head -n 1)
if [ -z "$SOAP_JAR" ]; then
    echo "❌ No se encontró el JAR ejecutable del servicio SOAP (soap-service-*-exec.jar)."
    exit 1
fi
java -jar "$SOAP_JAR" > soap.log 2>&1 &
SOAP_PID=$!
echo "[open-jar] SOAP Service corriendo (PID $SOAP_PID). Logs en soap.log"

wait_for_service "$SOAP_URL" "SOAP"

echo "[open-jar] 🌐 Iniciando Web App..."
WEB_WAR=$(find web-app/target -name "proyecto-culturarte-web-*.war" | head -n 1)
if [ -z "$WEB_WAR" ]; then
    echo "❌ No se encontró el WAR de la Web App."
    exit 1
fi
java -jar "$WEB_WAR" > web.log 2>&1 &
WEB_PID=$!
echo "[open-jar] Web App corriendo (PID $WEB_PID). Logs en web.log"

wait_for_service "$WEB_URL" "Web"

echo "[open-jar] 🖥️  Iniciando Desktop GUI..."
DESKTOP_JAR=$(find desktop-gui/target -name "app-*.jar" | head -n 1)
if [ -z "$DESKTOP_JAR" ]; then
    echo "❌ No se encontró el JAR de Desktop GUI."
    exit 1
fi

UPLOADS_DIR="$ROOT_DIR/uploads"
mkdir -p "$UPLOADS_DIR"
export APP_UPLOADS_DIR="$UPLOADS_DIR"

echo "[open-jar] Launching Swing app (JAR) locally... (uploads: $UPLOADS_DIR)"

# Build desktop-gui specifically
mvn -q -f "$ROOT_DIR/pom.xml" -pl desktop-gui -am install -DskipTests -Djacoco.skip=true

mvn -q -f "$ROOT_DIR/desktop-gui/pom.xml" spring-boot:run -Dspring-boot.run.mainClass=com.culturarte.DesktopGuiApplication -Dspring-boot.run.jvmArguments="-Djava.awt.headless=false -Dapp.uploads.dir=$UPLOADS_DIR"