package com.bibliotecaLagos.Socios.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("webClientTipoSocio")
    public WebClient webClientTipoSocio() {

        return WebClient.builder()
        .baseUrl("http://localhost:8086/api/v1/tipos-socio")
        .build();
    }
}