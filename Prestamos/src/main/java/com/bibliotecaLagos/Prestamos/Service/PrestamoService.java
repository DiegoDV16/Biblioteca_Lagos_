package com.bibliotecaLagos.Prestamos.Service;

import java.util.List;

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

import reactor.core.publisher.Mono;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    @Qualifier("webClientLibros")
    private WebClient webClientLibros;

    @Autowired
    @Qualifier("webClientSocios")
    private WebClient webClientSocios;

    public List<Prestamo> obtenerPrestamos() {

        return prestamoRepository.findAll();
    }

    public Prestamo obtenerPrestamoPorId(Integer id) {

        return prestamoRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException("Prestamo no encontrado"));
    }

    public Prestamo crearPrestamo(PrestamoDTO dto) {
        LibroDTO libro = webClientLibros.get()
        .uri("/{id}", dto.getLibroId())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResourceNotFoundException("Libro no encontrado")))
        .bodyToMono(LibroDTO.class)
        .block();

        webClientSocios.get()
                .uri("/{id}", dto.getSocioId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,response -> Mono.error(new ResourceNotFoundException("Socio no encontrado")))
                .bodyToMono(SocioDTO.class)
                .block();

        if (libro.getCantidadDisponible() <= 0) {
            throw new ResourceNotFoundException(
                    "No hay stock disponible"
            );
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setSocioId(dto.getSocioId());
        prestamo.setLibroId(dto.getLibroId());
        prestamo.setFechaPrestamo(dto.getFechaPrestamo());
        prestamo.setFechaDevolucion(dto.getFechaDevolucion());
        prestamo.setFechaEntrega(dto.getFechaEntrega());
        prestamo.setEstado(dto.getEstado());
        return prestamoRepository.save(prestamo);
    }

    public void eliminarPrestamo(Integer id) {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        prestamoRepository.delete(prestamo);
    }
}