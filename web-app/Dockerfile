# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml /build/
COPY core-business/pom.xml /build/core-business/
COPY soap-service/pom.xml /build/soap-service/
COPY web-app/pom.xml /build/web-app/
COPY desktop-gui/pom.xml /build/desktop-gui/

COPY core-business /build/core-business
COPY soap-service /build/soap-service
COPY web-app /build/web-app
COPY desktop-gui /build/desktop-gui

RUN mvn -q -f /build/pom.xml clean install -DskipTests -pl web-app -am

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/web-app/target/*.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.war"]
