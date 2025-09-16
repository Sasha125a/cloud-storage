FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle .
COPY src ./src
RUN gradle build -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/cloud-storage.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
