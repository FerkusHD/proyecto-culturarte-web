# Etapa 1: construir el JAR
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
# Instalamos Maven dentro del contenedor
RUN apk add --no-cache maven
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: imagen final
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
