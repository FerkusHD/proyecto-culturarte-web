FROM eclipse-temurin:21-jre

WORKDIR /app

COPY soap-service/target/soap-service-0.0.1-SNAPSHOT.jar app.jar
COPY mysql/application-mysql.properties application-mysql.properties

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]