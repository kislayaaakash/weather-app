package com.ps.weatherapp.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        ClassPathResource resource = new ClassPathResource("openapi.yaml");
        Path path = Paths.get(resource.getURI());
        String content = new String(Files.readAllBytes(path));
        SwaggerParseResult result = new OpenAPIV3Parser().readContents(content);
        if (result.getMessages().isEmpty() && result.getOpenAPI() != null) {
            return result.getOpenAPI();
        } else {
            throw new RuntimeException("Failed to parse OpenAPI definition: " + result.getMessages());
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api-docs/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }
}