# Render does not have Java, so the program is published with Docker.
# 1. Build the project with Maven
FROM maven:3.9-eclipse-temurin-17 AS compilar
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package

# 2. Run the program with Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=compilar /app/target/sif-0.0.1-SNAPSHOT.jar sif.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "sif.jar"]