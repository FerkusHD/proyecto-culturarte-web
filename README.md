# Culturarte - Plataforma de Crowdfunding Cultural

Culturarte es una plataforma web de crowdfunding para proyectos culturales desarrollada en Java con Spring Boot. Permite a los usuarios crear propuestas culturales, colaborar con ellas y gestionar el financiamiento de proyectos artísticos.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [Endpoints Principales](#endpoints-principales)
- [Docker](#docker)
- [Desarrollo](#desarrollo)

## ✨ Características

- **Gestión de Usuarios**: Registro y autenticación de usuarios (Proponentes y Colaboradores)
- **Propuestas Culturales**: Creación, búsqueda y gestión de propuestas culturales
- **Sistema de Colaboraciones**: Los colaboradores pueden financiar propuestas
- **Categorías**: Sistema de categorización jerárquico para organizar propuestas
- **Comentarios**: Sistema de comentarios en propuestas
- **Favoritos**: Los usuarios pueden marcar propuestas como favoritas
- **Seguimiento de Usuarios**: Sistema de seguimiento entre usuarios
- **Ranking**: Ranking de usuarios por cantidad de seguidores
- **Financiación**: Seguimiento del progreso de financiación de propuestas
- **Extensión de Financiación**: Los proponentes pueden extender el plazo de financiación
- **Constancias PDF**: Generación de constancias de pago en PDF

## 🏗️ Arquitectura

El proyecto sigue una arquitectura de microservicios con los siguientes componentes:

```
┌─────────────┐
│   Web App   │ (Spring MVC + JSP)
│   (8080)    │
└──────┬──────┘
       │ SOAP
       ▼
┌─────────────┐
│SOAP Service │ (Spring WS)
│   (8081)    │
└──────┬──────┘
       │ JPA
       ▼
┌─────────────┐
│   MySQL     │
│   (3306)    │
└─────────────┘
```

### Módulos

1. **core-business**: Lógica de negocio y entidades del dominio
2. **web-app**: Aplicación web Spring MVC con JSP
3. **soap-service**: Servicio SOAP para comunicación entre componentes
4. **desktop-gui**: Aplicación de escritorio (opcional)

## 🛠️ Tecnologías

### Backend
- **Java 21**
- **Spring Boot 3.x**
- **Spring MVC**
- **Spring WS** (SOAP)
- **JPA/Hibernate**
- **MySQL 8.4**
- **Maven**

### Frontend
- **JSP (JavaServer Pages)**
- **Bootstrap 5.3**
- **JavaScript (ES6+)**
- **AJAX/Fetch API**

### Herramientas
- **Docker & Docker Compose**
- **Maven**
- **JUnit** (Testing)

## 📁 Estructura del Proyecto

```
proyecto-culturarte-web/
├── core-business/          # Lógica de negocio
│   ├── src/main/java/
│   │   └── com/culturarte/
│   │       ├── logica/      # Controladores de lógica
│   │       ├── clases/      # Entidades del dominio
│   │       ├── datatypes/   # DTOs
│   │       └── enums/       # Enumeraciones
│   └── pom.xml
│
├── web-app/                 # Aplicación web
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/culturarte/web/
│   │   │       ├── controller/  # Controladores MVC
│   │   │       ├── soap/         # Clientes SOAP
│   │   │       └── service/     # Servicios
│   │   ├── resources/
│   │   │   ├── static/          # CSS, JS
│   │   │   └── application.properties
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── jsp/         # Vistas JSP
│   └── pom.xml
│
├── soap-service/            # Servicio SOAP
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/culturarte/soap/
│   │   │       └── endpoint/    # Endpoints SOAP
│   │   ├── resources/
│   │   │   └── ws/              # XSDs
│   │   └── xsd/                 # Definiciones XSD
│   └── pom.xml
│
├── desktop-gui/             # GUI de escritorio (opcional)
├── uploads/                 # Archivos subidos (imágenes)
├── docker-compose.yml       # Configuración Docker
└── pom.xml                  # POM padre
```

## 📦 Requisitos

- **Java 21** o superior
- **Maven 3.8+**
- **Docker & Docker Compose** (recomendado)
- **MySQL 8.4** (si no usas Docker)

## 🚀 Instalación

### Opción 1: Usando Docker (Recomendado)

1. **Clonar el repositorio**:
```bash
git clone <url-del-repositorio>
cd proyecto-culturarte-web
```

2. **Construir y ejecutar con Docker Compose**:
```bash
docker-compose up --build
```

Esto iniciará:
- MySQL en el puerto 3306
- SOAP Service en el puerto 8081
- Web App en el puerto 8080

3. **Acceder a la aplicación**:
- Web App: http://localhost:8080
- SOAP Service WSDL: http://localhost:8081/soap/ws/categorias.wsdl

### Opción 2: Instalación Manual

1. **Configurar MySQL**:
```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE culturarte;
```

2. **Configurar propiedades**:
Editar `web-app/src/main/resources/application.properties` y `soap-service/src/main/resources/application.properties` con tus credenciales de MySQL.

3. **Compilar el proyecto**:
```bash
mvn clean install
```

4. **Ejecutar SOAP Service**:
```bash
cd soap-service
mvn spring-boot:run
```

5. **Ejecutar Web App** (en otra terminal):
```bash
cd web-app
mvn spring-boot:run
```

## ⚙️ Configuración

### Variables de Entorno

#### Web App (`web-app/src/main/resources/application.properties`)
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/culturarte
spring.datasource.username=root
spring.datasource.password=root

# SOAP Service
soap.service.host=localhost
soap.service.port=8081
soap.service.context-path=/soap/ws
```

#### SOAP Service (`soap-service/src/main/resources/application.properties`)
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/culturarte
spring.datasource.username=root
spring.datasource.password=root

# Puerto del servicio
server.port=8081
```

## 📖 Uso

### Roles de Usuario

1. **Visitante**: Puede ver propuestas y buscar, pero no puede colaborar
2. **Proponente**: Puede crear propuestas culturales
3. **Colaborador**: Puede colaborar con propuestas y comentar

### Flujo Principal

1. **Registro**: Crear cuenta como Proponente o Colaborador
2. **Login**: Iniciar sesión en la plataforma
3. **Crear Propuesta** (Proponente): 
   - Ir a "Tengo una propuesta"
   - Completar formulario con título, descripción, categoría, etc.
4. **Colaborar** (Colaborador):
   - Buscar propuestas
   - Seleccionar una propuesta
   - Hacer una colaboración con monto y tipo de retorno
5. **Gestionar**: Ver perfil, propuestas favoritas, colaboraciones, etc.

## 🔌 Endpoints Principales

### Web App (Puerto 8080)

#### Usuarios
- `GET /usuarios/alta` - Formulario de registro
- `POST /usuarios/alta` - Registrar usuario
- `GET /usuarios/{nickname}` - Ver perfil de usuario
- `GET /usuarios/buscar` - Buscar usuarios
- `GET /usuarios/ranking` - Ranking de usuarios
- `POST /usuarios/seguir` - Seguir usuario
- `POST /usuarios/dejarDeSeguir` - Dejar de seguir

#### Propuestas
- `GET /propuestas/alta` - Formulario de alta
- `POST /propuestas/alta` - Crear propuesta
- `GET /propuestas/{titulo}` - Ver propuesta
- `GET /propuestas/buscar` - Buscar propuestas
- `GET /propuestas/listar` - Listar todas (JSON)
- `POST /propuestas/agregarFavorita` - Agregar a favoritos
- `POST /propuestas/quitarFavorita` - Quitar de favoritos
- `POST /propuestas/agregarComentario` - Agregar comentario
- `POST /propuestas/cancelar/{titulo}` - Cancelar propuesta
- `POST /propuestas/extender/{titulo}` - Extender financiación

#### Colaboraciones
- `GET /propuestas/registroColaboracion` - Formulario de colaboración
- `POST /propuestas/altaColaboracion` - Registrar colaboración
- `GET /colaboraciones/constancia-pago` - Descargar PDF

#### Categorías
- `GET /categorias/lista` - Lista de categorías (JSON)
- `GET /categorias/tree` - Árbol de categorías (JSON)

### SOAP Service (Puerto 8081)

Los endpoints SOAP están disponibles en:
- Categorías: `http://localhost:8081/soap/ws/categorias`
- Propuestas: `http://localhost:8081/soap/ws/propuestas`
- Usuarios: `http://localhost:8081/soap/ws/usuarios`
- Colaboraciones: `http://localhost:8081/soap/ws/colaboraciones`

## 🐳 Docker

### Comandos Útiles

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Reconstruir imágenes
docker-compose up --build

# Limpiar volúmenes
docker-compose down -v
```

### Servicios Docker

- **db**: Base de datos MySQL
- **soap**: Servicio SOAP
- **web**: Aplicación web

## 💻 Desarrollo

### Compilar

```bash
# Compilar todo el proyecto
mvn clean install

# Compilar solo un módulo
cd web-app
mvn clean install
```

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests de un módulo específico
cd core-business
mvn test
```

### Estructura de Código

- **Controladores**: Manejan las peticiones HTTP y renderizan vistas
- **Servicios SOAP**: Clientes para comunicarse con el servicio SOAP
- **DTOs**: Objetos de transferencia de datos
- **Entidades**: Modelos del dominio
- **JSPs**: Vistas del frontend

### Convenciones

- Los controladores están en `com.culturarte.web.controller`
- Los servicios SOAP están en `com.culturarte.web.soap.client`
- Las vistas JSP están en `WEB-INF/jsp/`
- Los recursos estáticos (CSS/JS) están en `resources/static/`

