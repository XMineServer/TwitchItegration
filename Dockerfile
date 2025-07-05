FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=build/libs/twitch-integration.jar
ARG APP_IMAGE=sidey383/twitch-integration
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]