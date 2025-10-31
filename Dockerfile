# --- Stage 1: Build the application ---
FROM maven:3.8.5-eclipse-temurin-17-focal AS build

# Set the working directory
WORKDIR /build

# Copy the entire project into the build container
COPY . .

# Run the Maven build to create the .jar file
RUN mvn clean package -DskipTests

# --- Stage 2: Create the final, small runtime image ---
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built .jar file from the 'build' stage (from above)
# THIS IS THE CORRECTED LINE
COPY --from=build /build/target/Blog-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]