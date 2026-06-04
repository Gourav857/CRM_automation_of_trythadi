# Step 1: Build the application using Maven
FROM maven:3.8.8-jakartaee10 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using OpenJDK
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]