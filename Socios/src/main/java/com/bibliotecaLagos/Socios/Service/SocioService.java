package com.bibliotecaLagos.Socios.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpStatusCode;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Socios.DTO.SocioDTO;
import com.bibliotecaLagos.Socios.DTO.TipoSocioDTO;

import com.bibliotecaLagos.Socios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Socios.Exception.ResourceNotFoundException;

import com.bibliotecaLagos.Socios.Model.Socio;

import com.bibliotecaLagos.Socios.Repository.SocioRepository;

import reactor.core.publisher.Mono;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    @Qualifier("webClientTipoSocio")
    private WebClient webClientTipoSocio;

    public List<Socio> obtenerSocios() {
        return socioRepository.findAll();
    }

    public Socio obtenerSocioPorId(Integer id) {

        return socioRepository.findById(id)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Socio no encontrado"
                ));
    }
    public Socio crearSocio(SocioDTO dto) {

  
        if(socioRepository.findByRut(dto.getRut()).isPresent()) {

            throw new DuplicateResourceException(
                 "El rut ya existe"
            );
        }

        if(socioRepository.findByCorreo(dto.getCorreo()).isPresent()) {

            throw new DuplicateResourceException(
                "El correo ya existe"
            );
        }

        TipoSocioDTO tipoSocio =
                webClientTipoSocio.get()
                .uri("/{id}", dto.getIdTipoSocio())
                .retrieve()
                .onStatus(
                HttpStatusCode::is4xxClientError,
                response -> Mono.error(
                        new ResourceNotFoundException(
                                "Tipo socio no encontrado"
                        )
                ))
                .bodyToMono(TipoSocioDTO.class)
                .block();

        Socio socio = new Socio();

        socio.setNombre(dto.getNombre());
        socio.setApellido(dto.getApellido());
        socio.setRut(dto.getRut());
        socio.setCorreo(dto.getCorreo());
        socio.setTelefono(dto.getTelefono());
        socio.setIdTipoSocio(tipoSocio.getId());
        return socioRepository.save(socio);
    }

    public void eliminarSocio(Integer id) {

        Socio socio = obtenerSocioPorId(id);
        socioRepository.delete(socio);
    }
}