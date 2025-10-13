FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar el JAR generado por Maven
COPY target/*.jar app.jar

EXPOSE 8080

ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ipas_prod
ENV SPRING_DATASOURCE_USERNAME=ipas_prod_user
ENV SPRING_DATASOURCE_PASSWORD=prod_password

ENTRYPOINT ["java", "-jar", "app.jar"]

