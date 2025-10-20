# Etapa 1: construir el JAR
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
RUN apk add --no-cache maven
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: solo para guardar el JAR construido
FROM scratch AS final
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
# No abrimos GUI dentro del contenedor
