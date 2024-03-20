FROM openjdk:21-jdk-slim

ARG IMAGE_NAME=oddmatrix-client
ARG BUILD_VERSION=$(date +%s)
ARG IMAGE_TAG=${BUILD_VERSION}
ENV APP_VERSION=$BUILD_VERSION
ENV IMAGE_NAME=$IMAGE_NAME
ENV IMAGE_TAG=$IMAGE_TAG

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
