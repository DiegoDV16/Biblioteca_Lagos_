package com.bibliotecaLagos.Socios.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SocioService {

    private static final Logger log = LoggerFactory.getLogger(SocioService.class);

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    @Qualifier("webClientTipoSocio")
    private WebClient webClientTipoSocio;

    public List<Socio> obtenerSocios() {
        log.info("Iniciando consulta de todos los socios");
        List<Socio> socios = socioRepository.findAll();
        log.info("Consulta completada: {} socios encontrados", socios.size());
        return socios;
    }

    public Socio obtenerSocioPorId(Integer id) {
        log.info("Buscando socio por ID: {}", id);
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Socio con ID {} no encontrado", id);
                    return new ResourceNotFoundException("Socio no encontrado");
                });
        log.info("Socio encontrado: ID={}, nombre={}", socio.getId(), socio.getNombre());
        return socio;
    }

    public Socio crearSocio(SocioDTO dto) {
        log.info("Iniciando creacion de socio: rut={}, correo={}", dto.getRut(), dto.getCorreo());

        if (socioRepository.findByRut(dto.getRut()).isPresent()) {
            log.warn("El rut {} ya existe", dto.getRut());
            throw new DuplicateResourceException("El rut ya existe");
        }

        if (socioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            log.warn("El correo {} ya existe", dto.getCorreo());
            throw new DuplicateResourceException("El correo ya existe");
        }

        log.info("Validando tipo de socio ID={} con microservicio tipoSocios", dto.getIdTipoSocio());
        TipoSocioDTO tipoSocio = webClientTipoSocio.get()
                .uri("/{id}", dto.getIdTipoSocio())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("Tipo socio no encontrado")))
                .bodyToMono(TipoSocioDTO.class)
                .block();

        log.info("Tipo de socio validado: ID={}, nombre={}", tipoSocio.getId(), tipoSocio.getNombre());

        Socio socio = new Socio();
        socio.setNombre(dto.getNombre());
        socio.setApellido(dto.getApellido());
        socio.setRut(dto.getRut());
        socio.setCorreo(dto.getCorreo());
        socio.setTelefono(dto.getTelefono());
        socio.setIdTipoSocio(tipoSocio.getId());

        Socio guardado = socioRepository.save(socio);
        log.info("Socio creado exitosamente: ID={}, nombre={}", guardado.getId(), guardado.getNombre());
        return guardado;
    }

    public Socio actualizarSocio(Integer id, SocioDTO dto) {
        log.info("Iniciando actualizacion de socio ID={}", id);
        Socio socioExistente = obtenerSocioPorId(id);

        if (!socioExistente.getRut().equals(dto.getRut()) &&
                socioRepository.findByRut(dto.getRut()).isPresent()) {
            log.warn("El rut {} ya existe", dto.getRut());
            throw new DuplicateResourceException("El rut ya existe");
        }

        if (!socioExistente.getCorreo().equals(dto.getCorreo()) &&
                socioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            log.warn("El correo {} ya existe", dto.getCorreo());
            throw new DuplicateResourceException("El correo ya existe");
        }

        if (dto.getIdTipoSocio() != null && !socioExistente.getIdTipoSocio().equals(dto.getIdTipoSocio())) {
            log.info("Validando tipo de socio ID={} con microservicio tipoSocios", dto.getIdTipoSocio());
            webClientTipoSocio.get()
                    .uri("/{id}", dto.getIdTipoSocio())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            response -> Mono.error(new ResourceNotFoundException("Tipo socio no encontrado")))
                    .bodyToMono(TipoSocioDTO.class)
                    .block();
        }

        socioExistente.setNombre(dto.getNombre());
        socioExistente.setApellido(dto.getApellido());
        socioExistente.setRut(dto.getRut());
        socioExistente.setCorreo(dto.getCorreo());
        socioExistente.setTelefono(dto.getTelefono());
        if (dto.getIdTipoSocio() != null) {
            socioExistente.setIdTipoSocio(dto.getIdTipoSocio());
        }

        Socio guardado = socioRepository.save(socioExistente);
        log.info("Socio ID={} actualizado exitosamente", guardado.getId());
        return guardado;
    }

    public void eliminarSocio(Integer id) {
        log.info("Iniciando eliminacion de socio ID={}", id);
        Socio socio = obtenerSocioPorId(id);
        socioRepository.delete(socio);
        log.info("Socio ID={} eliminado exitosamente", id);
    }
}