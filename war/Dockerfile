# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
# Copy only the war module to leverage Docker layer caching
COPY war/pom.xml /build/
# Pre-fetch dependencies
RUN mvn -q -f /build/pom.xml -DskipTests dependency:go-offline
# Copy sources
COPY war/src /build/src
# Build artifact
RUN mvn -q -f /build/pom.xml -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
# Install curl for healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built WAR
COPY --from=builder /build/target/*.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.war"]
