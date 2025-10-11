# Usar imagen base de Java
FROM openjdk:17-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR que genera Maven
COPY target/ipas-*.jar app.jar

# Puerto que expone (Spring Boot usa 8080)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
