# Old Dockerfile
# FROM eclipse-temurin
# COPY target/tfip-miniproject-0.0.1-SNAPSHOT.jar tfip-miniproject-0.0.1-SNAPSHOT.jar
# ENTRYPOINT [ "java", "-jar", "/tfip-miniproject-0.0.1-SNAPSHOT.jar" ] 
# EXPOSE 8080

# Use Maven for build stage
FROM maven:3.9.2-eclipse-temurin-20-alpine AS build

# Set up working directory in the container
WORKDIR /app

# First, copy the pom.xml file where you list your dependencies
# From root directory to /app
# The source path is relative to the build context
# The destination path is relative to the working directory inside the container
COPY pom.xml ./

# Download all required dependencies; this layer gets cached
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application. Since dependencies are downloaded,
# this will basically just compile the code and package it
RUN mvn clean package

# Use OpenJDK for run stage
FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

# Copy the JAR file from the build stage
# Notice /app/target: This JAR file comes from the build container, not the host machine
COPY --from=build /app/target/tfip-miniproject-0.0.1-SNAPSHOT.jar ./tfip-miniproject-0.0.1-SNAPSHOT.jar

# Specify environment variables required at build time
ARG SPRING_PROFILES_ACTIVE
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG JWT_SIGNINGKEY

# Make these variables available at runtime
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
ENV JWT_SIGNINGKEY=$JWT_SIGNINGKEY

# Specify the command to run your application\
ENTRYPOINT [ "java", "-jar", "./tfip-miniproject-0.0.1-SNAPSHOT.jar" ]

# Expose the application port
EXPOSE 8080