# Etapa 1: Compilar Spring Boot
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


# Etapa 2: Ejecutar aplicación
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Duser.timezone=America/Lima","-XX:MaxRAMPercentage=60.0","-XX:MinRAMPercentage=25.0","-XX:+UseG1GC","-jar","app.jar"]