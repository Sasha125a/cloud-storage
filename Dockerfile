FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем только нужные файлы
COPY pom.xml .
COPY src ./src

# Команда сборки - используем системный Maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
