package com.bibliotecaLagos.Reserva.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "webClientLibros")
    public WebClient webClientLibros() {

        return WebClient.builder()
        .baseUrl("http://localhost:8081/api/v1/libros")
        .build();
    }

    @Bean(name = "webClientSocios")
    public WebClient webClientSocios() {

        return WebClient.builder()
        .baseUrl("http://localhost:8085/api/v1/socios")
        .build();
    }
}