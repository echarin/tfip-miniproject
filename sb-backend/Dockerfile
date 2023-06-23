# Use Maven for build stage
FROM maven:3.9.2-eclipse-temurin-20-alpine AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Use OpenJDK for run stage
FROM eclipse-temurin:20-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/tfip-miniproject-0.0.1-SNAPSHOT.jar ./tfip-miniproject-0.0.1-SNAPSHOT.jar

# Specify environment variables required at build time
ARG SPRING_PROFILES_ACTIVE
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG JWT_SIGNINGKEY
ARG MONGO_USER
ARG MONGO_PASSWORD
ARG MONGO_HOST
ARG MONGO_PORT
ARG MONGO_DB

# Make these variables available at runtime
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
ENV JWT_SIGNINGKEY=$JWT_SIGNINGKEY
ENV MONGO_USER=$MONGO_USER
ENV MONGO_PASSWORD=$MONGO_PASSWORD
ENV MONGO_HOST=$MONGO_HOST
ENV MONGO_PORT=$MONGO_PORT
ENV MONGO_DB=$MONGO_DB

# Specify the command to run your application
ENTRYPOINT [ "java", "-jar", "./tfip-miniproject-0.0.1-SNAPSHOT.jar" ]

# Expose the application port
EXPOSE 8080