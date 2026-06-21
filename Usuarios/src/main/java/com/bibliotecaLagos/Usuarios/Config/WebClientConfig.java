package com.bibliotecaLagos.Usuarios.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class WebClientConfig {

    @Bean("webClientRoles")
    public WebClient webClientRoles() {

        return WebClient.builder()
        .baseUrl("http://localhost:8088/api/v1/roles")
        .build();
    }
}