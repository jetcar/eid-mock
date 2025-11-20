package com.example.smartid.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartIdMockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart-ID Mock API")
                        .description("Mock server for Smart-ID API for testing purposes")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")))
                .servers(List.of(
                        new Server().url("https://localhost:8083").description("Direct HTTPS Server")));
    }
}
