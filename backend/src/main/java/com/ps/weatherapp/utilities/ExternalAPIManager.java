package com.ps.weatherapp.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ExternalAPIManager {

    private static final Logger logger = LoggerFactory.getLogger(ExternalAPIManager.class);
    private final WebClient webClient;

    public ExternalAPIManager(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Generic method to fetch data from an external API.
     *
     * @param path          the API endpoint path
     * @param queryParams   the query parameters as a map
     * @param responseClass the response type
     * @param <T>           the type of the response
     * @return a Mono containing the API response
     */
    public <T> Mono<T> fetchData(String path, Map<String, Object> queryParams, Class<T> responseClass) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .exchangeToMono(response -> response.bodyToMono(responseClass))
                .onErrorResume(error -> {
                    logger.error("Error during API call to {}: {}", path, error.getMessage());
                    return Mono.error(error);
                });
    }
}