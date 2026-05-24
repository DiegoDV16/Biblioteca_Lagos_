package com.bibliotecaLagos.libros.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("webClientCategorias")
    public WebClient webClientCategorias() {

        return WebClient.builder()
        .baseUrl("http://localhost:8082/api/v1/categorias")
        .build();
    }

    @Bean("webClientProveedores")
    public WebClient webClientProveedores() {

        return WebClient.builder()
        .baseUrl("http://localhost:8083/api/v1/proveedores")
        .build();
    }
}