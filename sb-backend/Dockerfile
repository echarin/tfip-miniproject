# For GitHub Actions CI/CD to Railway
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
ARG JWT_SIGNINGKEY

# Make these variables available at runtime
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV JWT_SIGNINGKEY=$JWT_SIGNINGKEY

# ARG MYSQL_HOST
# ARG MYSQL_PORT
# ARG MYSQL_USER
# ARG MYSQL_PASSWORD
# ARG MYSQL_DATABASE
# ENV MYSQL_HOST=$MYSQL_HOST
# ENV MYSQL_PORT=$MYSQL_PORT
# ENV MYSQL_USER=$MYSQL_USER
# ENV MYSQL_PASSWORD=$MYSQL_PASSWORD
# ENV MYSQL_DATABASE=$MYSQL_DATABASE

# Specify the command to run your application
ENTRYPOINT [ "java", "-jar", "./tfip-miniproject-0.0.1-SNAPSHOT.jar" ]

# Expose the application port
EXPOSE 8080