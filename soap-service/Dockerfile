FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY core-business ./core-business
COPY soap-service ./soap-service

RUN mvn -f core-business/pom.xml clean install -DskipTests
RUN mvn -f soap-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/soap-service/target/soap-service-0.0.1-SNAPSHOT.jar app.jar
COPY mysql/application.properties application-mysql.properties

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
