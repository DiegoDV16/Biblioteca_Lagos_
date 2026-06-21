package com.bibliotecaLagos.Multas.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class WebClientConfig {

    @Bean("webClientPrestamos")
    public WebClient webClientPrestamos() {

        return WebClient.builder()
        .baseUrl("http://localhost:8086/api/v1/prestamos")

    }
}