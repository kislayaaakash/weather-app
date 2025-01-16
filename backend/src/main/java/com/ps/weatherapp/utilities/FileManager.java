package com.ps.weatherapp.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FileManager<T> {
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    public FileManager() {
        this.objectMapper = new ObjectMapper();
    }

    // Serialize the cache to a JSON string
    public String serializeData(Map<String, T> cache) {
        try {
            return objectMapper.writeValueAsString(cache);
        } catch (IOException e) {
            logger.error("Error serializing cache: {}", e.getMessage());
            return null;
        }
    }

    // Deserialize JSON string to the cache structure
    public Map<String, T> deserializeData(String json, TypeReference<Map<String, T>> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error("Error deserializing cache: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // Save the cache to a file
    public void saveDataToFile(String fileName, Map<String, T> cache) {
        try {
            String json = serializeData(cache);
            if (json != null) {
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                Files.write(Paths.get(fileName), json.getBytes());
            }
        } catch (IOException e) {
            logger.error("Error saving cache to file: {}", e.getMessage());
        }
    }

    //Load the cache from a file
    public Map<String, T> loadDataFromFile(String fileName, TypeReference<Map<String, T>> typeReference) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                String json = new String(Files.readAllBytes(Paths.get(fileName)));
                return deserializeData(json, typeReference);
            } else {
                logger.info("BackUp file not found.");
                return new LinkedHashMap<>();
            }
        } catch (IOException e) {
            logger.warn("Error loading cache from file: {}", e.getMessage());
            return new LinkedHashMap<>();
        }
    }
}

