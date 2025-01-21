#!/bin/bash
CACHE_FILE=/app/weatherCache.json
if [ ! -f "$CACHE_FILE" ]; then
  echo "{}" > "$CACHE_FILE"
  echo "Created weatherCache.json file."
else
  echo "weatherCache.json file already exists."
fi
java -jar /app/weatherapp.jar