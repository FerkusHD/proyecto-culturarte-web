# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY web-app/pom.xml /build/
RUN mvn -q -f /build/pom.xml -DskipTests dependency:go-offline
COPY web-app/src /build/src
RUN mvn -q -f /build/pom.xml -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/*.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.war"]