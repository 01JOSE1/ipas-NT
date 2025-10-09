# Imagen base con Java 17
FROM openjdk:17-jdk-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR generado por Maven
COPY target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]