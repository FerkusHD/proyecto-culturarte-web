FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .

COPY core-business ./core-business
COPY soap-service ./soap-service
COPY web-app ./web-app

RUN mvn -f core-business/pom.xml clean install -DskipTests
RUN mvn -f soap-service/pom.xml clean install -DskipTests
RUN mvn -f web-app/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app


COPY --from=builder /build/web-app/target/proyecto-culturarte-web-0.0.1-SNAPSHOT.war app.war
COPY mysql/application.properties application-mysql.properties

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
