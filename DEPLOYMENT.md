# Guía de Despliegue en Múltiples Máquinas

Esta guía explica cómo ejecutar el **Servidor Central** en una PC y la **Aplicación Web** en otra PC diferente.

## Arquitectura del Sistema

```
┌─────────────────────────────────────┐
│     PC 1: SERVIDOR CENTRAL          │
│  ┌──────────────────────────────┐   │
│  │  Base de Datos (MySQL)       │   │
│  │  Puerto: 3306                │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  SOAP Service                │   │
│  │  Puerto: 8081                │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  Desktop GUI                 │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
                 ↕
         (Red Local/Internet)
                 ↕
┌─────────────────────────────────────┐
│     PC 2: SERVIDOR WEB              │
│  ┌──────────────────────────────┐   │
│  │  Web Application             │   │
│  │  Puerto: 8080                │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

## Configuración

### PC 1: Servidor Central

#### 1. Configurar la Base de Datos para Acceso Remoto

Edita el archivo `docker-compose.yml` para permitir conexiones externas:

```yaml
services:
  db:
    image: mysql:8.0
    container_name: culturarte-db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: culturarte
    ports:
      - "3306:3306"  # Exponer el puerto para acceso externo
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - culturarte-network
```

#### 2. Configurar el SOAP Service

El SOAP Service ya está configurado para escuchar en todas las interfaces (`0.0.0.0`), por lo que aceptará conexiones externas.

Puedes verificar/modificar en `soap-service/src/main/resources/application.properties`:

```properties
soap.server.host=0.0.0.0
soap.server.port=8081
```

#### 3. Configurar el Firewall

Asegúrate de que los puertos estén abiertos en el firewall de la PC 1:
- **Puerto 3306**: Base de Datos MySQL
- **Puerto 8081**: SOAP Service

**En macOS:**
```bash
# Verificar firewall
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --getglobalstate

# Si está activo, permitir conexiones entrantes para Java
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /usr/bin/java
```

**En Linux (Ubuntu/Debian):**
```bash
sudo ufw allow 3306/tcp
sudo ufw allow 8081/tcp
sudo ufw reload
```

**En Windows:**
```powershell
# Abrir PowerShell como Administrador
New-NetFirewallRule -DisplayName "MySQL" -Direction Inbound -LocalPort 3306 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "SOAP Service" -Direction Inbound -LocalPort 8081 -Protocol TCP -Action Allow
```

#### 4. Obtener la IP de la PC 1

**En macOS/Linux:**
```bash
# IP local
ifconfig | grep "inet " | grep -v 127.0.0.1

# O más simple
hostname -I
```

**En Windows:**
```powershell
ipconfig
```

Anota la dirección IP (por ejemplo: `192.168.1.100`)

#### 5. Ejecutar el Servidor Central

```bash
./run-central.sh
```

### PC 2: Servidor Web

#### 1. Configurar Variables de Entorno

Crea un archivo de configuración en `~/.Culturarte/web-app.properties`:

```bash
mkdir -p ~/.Culturarte
nano ~/.Culturarte/web-app.properties
```

Agrega el siguiente contenido (reemplaza `192.168.1.100` con la IP real de la PC 1):

```properties
# Configuración del Servidor Central
SOAP_SERVICE_HOST=192.168.1.100
SOAP_SERVICE_PORT=8081
SOAP_SERVICE_CONTEXT_PATH=/soap/ws

# Configuración de Base de Datos
DB_URL=jdbc:mysql://192.168.1.100:3306/culturarte?zeroDateTimeBehavior=CONVERT_TO_NULL
DB_USERNAME=root
DB_PASSWORD=root

# URL base de la aplicación web (ajustar según tu dominio/IP)
APP_BASE_URL=http://192.168.1.101:8080
```

#### 2. Alternativa: Variables de Entorno del Sistema

En lugar de crear el archivo de configuración, puedes exportar variables de entorno:

**En macOS/Linux:**
```bash
export SOAP_SERVICE_HOST=192.168.1.100
export DB_URL=jdbc:mysql://192.168.1.100:3306/culturarte?zeroDateTimeBehavior=CONVERT_TO_NULL
export DB_USERNAME=root
export DB_PASSWORD=root
export APP_BASE_URL=http://192.168.1.101:8080
```

**En Windows (PowerShell):**
```powershell
$env:SOAP_SERVICE_HOST="192.168.1.100"
$env:DB_URL="jdbc:mysql://192.168.1.100:3306/culturarte?zeroDateTimeBehavior=CONVERT_TO_NULL"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:APP_BASE_URL="http://192.168.1.101:8080"
```

#### 3. Modificar run-web.sh para Variables de Entorno

Puedes crear un script personalizado `run-web-remote.sh`:

```bash
#!/bin/bash

# IP del Servidor Central
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
java -jar "$WEB_WAR"
```

Hazlo ejecutable:
```bash
chmod +x run-web-remote.sh
```

#### 4. Ejecutar la Aplicación Web

```bash
./run-web-remote.sh
```

O si usaste el archivo de configuración:
```bash
./run-web.sh
```

## Verificación

### En la PC 1 (Servidor Central)

Verifica que los servicios estén corriendo:

```bash
# Verificar SOAP Service
curl http://localhost:8081/soap/ws/categorias.wsdl

# Verificar Base de Datos
docker ps | grep culturarte-db
```

### En la PC 2 (Servidor Web)

Verifica la conexión al SOAP Service (reemplaza con la IP real):

```bash
curl http://192.168.1.100:8081/soap/ws/categorias.wsdl
```

### Desde un Navegador

Accede a la aplicación web desde cualquier dispositivo en la red:
```
http://192.168.1.101:8080
```

## Troubleshooting

### Error de Conexión al SOAP Service

1. **Verificar conectividad de red:**
   ```bash
   ping 192.168.1.100
   ```

2. **Verificar que el puerto esté abierto:**
   ```bash
   telnet 192.168.1.100 8081
   # O con nc (netcat)
   nc -zv 192.168.1.100 8081
   ```

3. **Revisar logs del SOAP Service:**
   ```bash
   tail -f soap.log
   ```

### Error de Conexión a la Base de Datos

1. **Verificar conectividad:**
   ```bash
   telnet 192.168.1.100 3306
   ```

2. **Verificar permisos de MySQL:**
   ```bash
   docker exec -it culturarte-db mysql -uroot -proot -e "SELECT user, host FROM mysql.user;"
   ```

3. **Crear usuario remoto si es necesario:**
   ```bash
   docker exec -it culturarte-db mysql -uroot -proot -e "CREATE USER 'root'@'%' IDENTIFIED BY 'root'; GRANT ALL PRIVILEGES ON culturarte.* TO 'root'@'%'; FLUSH PRIVILEGES;"
   ```

### La Web App no puede conectarse

1. **Verificar variables de entorno:**
   ```bash
   echo $SOAP_SERVICE_HOST
   echo $DB_URL
   ```

2. **Revisar logs de la aplicación web:**
   ```bash
   tail -f web.log
   ```

## Despliegue en Producción

Para un entorno de producción, considera:

1. **Usar HTTPS** en lugar de HTTP
2. **Configurar un proxy reverso** (Nginx, Apache)
3. **Usar contraseñas seguras** para la base de datos
4. **Configurar un firewall** adecuado
5. **Usar un servidor de base de datos dedicado** en lugar de Docker
6. **Implementar balanceo de carga** si es necesario
7. **Configurar backups automáticos** de la base de datos

## Scripts de Referencia

- **`run-central.sh`**: Ejecuta BD + SOAP + Desktop GUI
- **`run-web.sh`**: Ejecuta solo la Web App
- **`open-jar.sh`**: Ejecuta todo en una sola máquina
