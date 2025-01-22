# Use OpenJDK 21 as the base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR to the container
COPY target/weather-app-be.jar /app/weatherapp.jar

# Create and initialize the weatherCache.json file
RUN echo "{}" > /app/weatherCache.json && \
    echo "Created weatherCache.json file."

# Expose the application's port (e.g., 8080)
EXPOSE 8081

# Start the Spring Boot application directly
ENTRYPOINT ["java", "-jar", "/app/weatherapp.jar"]
