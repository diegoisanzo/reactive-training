# Etapa 1: Construcción (Compilación con Gradle 9.4.1 y JDK 25)
FROM gradle:9.4.1-jdk25 AS build
WORKDIR /app

# Copiar archivos de configuración de Gradle para la caché de capas de Docker
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Descargar dependencias básicas antes de copiar el código fuente
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y empaquetar la aplicación
COPY src src
RUN ./gradlew bootJar --no-daemon

# Etapa 2: Producción (Entorno de ejecución mínimo con Chisel para Java 25)
FROM ubuntu/jre:25-26.04_edge 
WORKDIR /app

# Copiar el Fat JAR compilado por Gradle
EXPOSE 8080

COPY --from=build /app/build/libs/*.jar app.jar

# Ejecución directa (sintaxis de array estricta por la falta de shell)
ENTRYPOINT ["java", "-jar", "app.jar"]
