# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY core-business /build/core-business
COPY soap-service /build/soap-service
COPY web-app /build/web-app

RUN mvn -q -f core-business/pom.xml clean install -DskipTests
RUN mvn -q -f soap-service/pom.xml clean install -DskipTests
RUN mvn -q -f web-app/pom.xml -DskipTests dependency:go-offline
RUN mvn -q -f web-app/pom.xml clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/web-app/target/*.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.war"]
