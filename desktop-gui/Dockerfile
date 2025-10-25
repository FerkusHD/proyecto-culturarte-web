# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY jar/pom.xml /build/
RUN mvn -q -f /build/pom.xml -DskipTests dependency:go-offline
COPY jar/src /build/src
RUN mvn -q -f /build/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"
COPY --from=builder /build/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
