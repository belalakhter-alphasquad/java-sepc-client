FROM openjdk:21-jdk-slim

WORKDIR /app

COPY gradlew gradlew.bat gradle gradle.properties settings.gradle.kts build.gradle.kts /app/
COPY gradle /app/gradle

COPY src /app/src
COPY app/libs /app/libs
COPY app/.env /app/.env


RUN ./gradlew clean build

CMD ["./gradlew", "run"]
