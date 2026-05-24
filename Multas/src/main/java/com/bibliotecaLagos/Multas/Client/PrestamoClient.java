package com.bibliotecaLagos.Multas.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PrestamoClient {

    private final RestTemplate restTemplate;

    private final String URL = "http://localhost:8083/api/prestamos/";

    public Object obtenerPrestamo(Integer id) {
        return restTemplate.getForObject(URL + id, Object.class);
    }
}