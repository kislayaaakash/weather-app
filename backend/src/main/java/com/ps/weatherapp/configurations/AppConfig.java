package com.ps.weatherapp.configurations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ps.weatherapp.models.CityWeatherDetails;
import com.ps.weatherapp.utilities.FileManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(AppConstants.WEATHER_API_BASE_URL).build();
    }

    @Bean
    public Map<String, CityWeatherDetails> weatherBackUpData(FileManager<CityWeatherDetails> fileManager, @Value("${service.cacheFileName}") String fileName) {
        return fileManager
            .loadDataFromFile(fileName, new TypeReference<>() {
            });
    }
}
