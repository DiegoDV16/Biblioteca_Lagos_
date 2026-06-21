package com.bibliotecaLagos.Prestamos.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @Qualifier("webClientLibros")
    public WebClient webClientLibros() {

        return WebClient.builder()
        .baseUrl("http://localhost:8081/api/v1/libros")
        .build();
    }
    @Bean
    @Qualifier("webClientSocios")
    public WebClient webClientSocios() {

        return WebClient.builder()
        .baseUrl("http://localhost:8085/api/v1/socios")
        .build();
    }
}