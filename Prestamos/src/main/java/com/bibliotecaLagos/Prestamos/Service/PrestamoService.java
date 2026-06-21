package com.bibliotecaLagos.Prestamos.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Prestamos.DTO.LibroDTO;
import com.bibliotecaLagos.Prestamos.DTO.PrestamoDTO;
import com.bibliotecaLagos.Prestamos.DTO.SocioDTO;
import com.bibliotecaLagos.Prestamos.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Prestamos.Model.Prestamo;
import com.bibliotecaLagos.Prestamos.Repository.PrestamoRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    @Qualifier("webClientLibros")
    private WebClient webClientLibros;

    @Autowired
    @Qualifier("webClientSocios")
    private WebClient webClientSocios;

    public List<Prestamo> obtenerPrestamos() {
        log.info("Iniciando consulta de todos los prestamos");
        List<Prestamo> prestamos = prestamoRepository.findAll();
        log.info("Consulta completada: {} prestamos encontrados", prestamos.size());
        return prestamos;
    }

    public Prestamo obtenerPrestamoPorId(Integer id) {
        log.info("Buscando prestamo por ID: {}", id);
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Prestamo con ID {} no encontrado", id);
                    return new ResourceNotFoundException("Prestamo no encontrado");
                });
        log.info("Prestamo encontrado: ID={}", prestamo.getId());
        return prestamo;
    }

    public Prestamo crearPrestamo(PrestamoDTO dto) {
        log.info("Iniciando creacion de prestamo: libroId={}, socioId={}", dto.getLibroId(), dto.getSocioId());

        log.info("Validando libro ID={} con microservicio libros", dto.getLibroId());
        LibroDTO libro = webClientLibros.get()
                .uri("/{id}", dto.getLibroId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("Libro no encontrado")))
                .bodyToMono(LibroDTO.class)
                .block();
        log.info("Libro validado: ID={}, disponible={}", libro.getId(), libro.getCantidadDisponible());

        log.info("Validando socio ID={} con microservicio socios", dto.getSocioId());
        webClientSocios.get()
                .uri("/{id}", dto.getSocioId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("Socio no encontrado")))
                .bodyToMono(SocioDTO.class)
                .block();
        log.info("Socio validado: ID={}", dto.getSocioId());

        if (libro.getCantidadDisponible() <= 0) {
            log.warn("Stock insuficiente para libro ID={}", dto.getLibroId());
            throw new ResourceNotFoundException("No hay stock disponible");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setSocioId(dto.getSocioId());
        prestamo.setLibroId(dto.getLibroId());
        prestamo.setFechaPrestamo(dto.getFechaPrestamo());
        prestamo.setFechaDevolucion(dto.getFechaDevolucion());
        prestamo.setFechaEntrega(dto.getFechaEntrega());
        prestamo.setEstado(dto.getEstado());

        Prestamo guardado = prestamoRepository.save(prestamo);
        log.info("Prestamo creado exitosamente: ID={}", guardado.getId());
        return guardado;
    }

    public void eliminarPrestamo(Integer id) {
        log.info("Iniciando eliminacion de prestamo ID={}", id);
        Prestamo prestamo = obtenerPrestamoPorId(id);
        prestamoRepository.delete(prestamo);
        log.info("Prestamo ID={} eliminado exitosamente", id);
    }
}
