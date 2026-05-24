package com.bibliotecaLagos.Usuarios.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RolClient {

    private final WebClient webClient;

    public RolClient(@Value("${roles.service.url:http://localhost:8081}") String rolesServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(rolesServiceUrl).build();
    }

    public RolesResponseDTO obtenerRol(Integer rolId) {
        return webClient.get()
                .uri("/roles/{id}", rolId)
                .retrieve()
                .bodyToMono(RolesResponseDTO.class)
                .block();
    }
}
