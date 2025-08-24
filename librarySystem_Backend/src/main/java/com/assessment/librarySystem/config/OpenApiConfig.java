package com.assessment.librarySystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for the Library Management System
 * Generates automatic API documentation for the 5 core endpoints
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI librarySystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("REST API for managing library books and borrowers with 5 core endpoints")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Library System Team")
                                .email("support@librarysystem.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Alternative development server")
                ));
    }
}
