FROM openjdk:21-jdk-slim

WORKDIR /app


COPY gradlew gradlew.bat gradle.properties settings.gradle.kts /app/
COPY gradle /app/gradle
COPY app/build.gradle.kts /app/
COPY app/libs /app/libs
COPY app/src /app/src
COPY app/.env /app/
COPY app/logs /app/



RUN ./gradlew clean build

CMD ["./gradlew", "run"]
