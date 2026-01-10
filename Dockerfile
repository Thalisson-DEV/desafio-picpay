# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Note: Using -DskipTests to speed up container build. Run tests separately in CI.
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]