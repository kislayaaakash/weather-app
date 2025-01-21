# Use OpenJDK 21 as the base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR to the container
COPY target/weather-app-be.jar /app/weatherapp.jar

# Copy the initialization script to the container
COPY intial.sh /app/intial.sh

# Make the initialization script executable
RUN chmod +x /app/intial.sh

# Expose the application's port (e.g., 8080)
EXPOSE 8080

# Start the initialization script on container start
ENTRYPOINT ["/bin/bash", "/app/intial.sh"]