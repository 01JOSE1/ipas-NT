# Usar una imagen base con Java 17
FROM openjdk:21-jdk-slim

# Información del mantenedor
LABEL maintainer="tu-email@example.com"

# Crear directorio para la aplicación
WORKDIR /app

# Copiar el archivo JAR al contenedor
COPY target/*.jar app.jar

# Puerto que usa tu aplicación (cámbialo si es diferente)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
