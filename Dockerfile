# Stage 1: Build (Gradle 9.4.1 + JDK 25)
FROM gradle:9.4.1-jdk25 AS build
WORKDIR /app

# Copy Gradle config files first for Docker layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies before copying source
RUN ./gradlew dependencies --no-daemon

# Copy source and package the application
COPY src src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime (minimal JRE via Chisel for Java 25)
FROM ubuntu/jre:25-26.04_edge
WORKDIR /app
EXPOSE 8080

# Copy the fat JAR built by Gradle
COPY --from=build /app/build/libs/*.jar app.jar

# Exec form required — no shell available in this image
ENTRYPOINT ["java", "-jar", "app.jar"]
