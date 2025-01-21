#!/bin/bash
# Define the path to the file
CACHE_FILE=/app/weatherCache.json
# Check if the file exists; create it if not
if [ ! -f "$CACHE_FILE" ]; then
  echo "{}" > "$CACHE_FILE" # Initialize with an empty JSON object
  echo "Created weatherCache.json file."
else
  echo "weatherCache.json file already exists."
fi
# Start the Spring Boot application
java -jar /app/weatherapp.jar