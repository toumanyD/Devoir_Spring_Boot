package com.isept.gestion_etudiants.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI etudiantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion des Étudiants - ISEP-AT")
                        .description("API REST CRUD pour la gestion des étudiants de l'ISEP-AT Diamniadio")
                        .version("1.0.0"));
    }
}