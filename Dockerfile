FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el JAR generado por Maven
COPY target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno (serán sobrescritas en runtime)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ipas_prod
ENV SPRING_DATASOURCE_USERNAME=ipas_prod_user
ENV SPRING_DATASOURCE_PASSWORD=prod_password

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
