package com.opspilot.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for API documentation.
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configure OpenAPI documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI opsPilotOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("OpsPilot Team")
                .email("support@opspilot.com")
                .url("https://opspilot.com");

        License license = new License()
                .name("Proprietary")
                .url("https://opspilot.com/license");

        Info info = new Info()
                .title("OpsPilot Operations Platform API")
                .version("1.0.0")
                .description("RESTful API for OpsPilot internal operations management platform. " +
                        "Provides endpoints for employee management, work item tracking, and administrative operations.")
                .contact(contact)
                .license(license);

        // JWT Security Scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("bearerAuth")
                .description("JWT authentication token. Obtain token via /api/auth/login endpoint.");

        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .components(components);
    }
}

