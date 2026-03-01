# Use Java 21
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE ${PORT}

ENTRYPOINT ["sh","-c","java -jar /app/app.jar --server.port=${PORT}"]
