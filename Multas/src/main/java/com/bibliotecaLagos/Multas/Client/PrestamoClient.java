package com.bibliotecaLagos.Multas.Client;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PrestamoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String PRESTAMO_URL = "http://localhost:8083/api/prestamos/";

    public Map<String, Object> obtenerPrestamo(Integer prestamoId) {
        return restTemplate.getForObject(PRESTAMO_URL + prestamoId, Map.class);
    }
}