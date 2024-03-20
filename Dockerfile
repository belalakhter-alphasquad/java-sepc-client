FROM openjdk:21-jdk-slim

ARG BUILD_VERSION=$(date +%s)
ENV APP_VERSION=$BUILD_VERSION


WORKDIR /app


COPY gradlew gradlew.bat gradle.properties settings.gradle.kts /app/
COPY gradle /app/gradle
COPY app/build.gradle.kts /app/
COPY app/libs /app/libs
COPY app/src /app/src
COPY app/.env /app/
COPY app/logs /app/



RUN ./gradlew clean build

CMD ["./gradlew", "run", "--version", "${APP_VERSION}"]
