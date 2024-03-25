# Use the official Maven image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.4-openjdk-17 AS build

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Stage 2: A smaller image with the runtime environment
FROM openjdk:17-jdk-alpine

# Copy the packaged jar file to our container
COPY --from=build /app/target/Order-0.0.1-SNAPSHOT.jar /app/Order-0.0.1-SNAPSHOT.jar

# Run the Spring Boot application when the container starts
CMD ["java", "-jar", "/app/Order-0.0.1-SNAPSHOT.jar"]
