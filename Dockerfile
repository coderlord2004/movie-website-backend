# Build phase
FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime phase
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/MovieWebsiteProject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
