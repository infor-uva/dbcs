FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /app
COPY ./ ./
RUN mvn -Dmaven.test.skip=true clean package
FROM eclipse-temurin:25-jdk
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENV PORT 8080
EXPOSE $PORT
ENTRYPOINT ["java","-jar", "/app.jar"]