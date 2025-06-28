FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ARG JAR_FILE
COPY build/libs/$JAR_FILE app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]