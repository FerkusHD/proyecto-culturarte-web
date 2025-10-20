# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Leverage Docker layer caching
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=default
WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 8080
# Use shell form to allow JAVA_OPTS
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.war"]
