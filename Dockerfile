# Use lightweight Java runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# copy jar produced by maven
COPY target/*.jar app.jar

# expose port (change if your app uses different port)
EXPOSE 8080

# JVM memory limits for lower resource usage
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","/app/app.jar"]
