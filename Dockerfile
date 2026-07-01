# Stage 1: Build (Gradle 9.5.1 + JDK 25)
FROM gradle:9.5.1-jdk25 AS build
WORKDIR /app

# Copy Gradle config files first for Docker layer caching
COPY build.gradle settings.gradle ./

# Download dependencies before copying source
RUN gradle dependencies --no-daemon

# Copy source and package the application
COPY src src
RUN gradle bootJar --no-daemon

# Stage 2: Runtime (minimal JRE -distroless- for Java 25)
FROM gcr.io/distroless/java25-debian13
WORKDIR /app
EXPOSE 8080

# Copy the fat JAR built by Gradle
COPY --from=build /app/build/libs/*.jar app.jar

# Exec form required — no shell available in this image
ENTRYPOINT ["java", "-jar", "app.jar"]
