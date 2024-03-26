
FROM openjdk:21-jdk-slim AS build
WORKDIR /workspace
RUN rm -rf app/build/*
RUN rm -rf app/app.jar
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts /workspace/
COPY gradle /workspace/gradle
COPY app/build.gradle.kts /workspace/
COPY app/libs /workspace/libs
COPY app/src /workspace/src
COPY app/.env /workspace/
COPY app/logs /workspace/

RUN ./gradlew clean build


FROM openjdk:21-jdk-slim
WORKDIR /application
COPY --from=build /workspace/.env .env
COPY --from=build /workspace/build/libs/*.jar app.jar
CMD ["java", "-XX:+UseContainerSupport", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", "-jar", "app.jar"]

