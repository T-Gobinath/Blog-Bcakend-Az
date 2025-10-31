# Use a standard Java runtime as a base
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
# Make sure the .jar file name matches your project's output
COPY target/Blog-Bcakend-Az-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]