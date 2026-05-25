package com.bibliotecaLagos.Usuarios.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RolClient {

    @Autowired
    private RestTemplate restTemplate;

    public RolesResponseDTO obtenerRol(Integer rolId) {
        String url = "http://localhost:8088/api/roles/" + rolId;
        return restTemplate.getForObject(url, RolesResponseDTO.class);
    }
}
