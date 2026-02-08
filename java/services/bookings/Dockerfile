FROM maven:3-openjdk-17 AS maven
WORKDIR /app
COPY ./ ./
RUN mvn -Dmaven.test.skip clean package
FROM eclipse-temurin:17-jdk
ARG JAR_FILE=/app/target/*.jar
COPY --from=maven ${JAR_FILE} app.jar
ENV PORT 8080
EXPOSE $PORT
ENTRYPOINT ["java","-jar", "/app.jar"]