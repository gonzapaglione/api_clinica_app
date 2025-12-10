package com.gonzalo.labo6final.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Clínica Odontológica")
                        .version("1.0")
                        .description("API REST para la gestión de turnos y pacientes en clínica odontológica. " +
                                "Incluye gestión de odontólogos, pacientes, turnos, horarios laborales, " +
                                "obras sociales e historias clínicas.")
                        .contact(new Contact()
                                .name("Gonzalo López Paglione")
                                .email("gonzalo@clinica.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local"),
                        new Server()
                                .url("http://10.0.2.2:8080")
                                .description("Servidor para Emulador Android")));
    }
}
