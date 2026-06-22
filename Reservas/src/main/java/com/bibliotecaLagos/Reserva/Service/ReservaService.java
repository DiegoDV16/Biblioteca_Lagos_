package com.bibliotecaLagos.Reserva.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Reserva.DTO.LibroDTO;
import com.bibliotecaLagos.Reserva.DTO.ReservaDTO;
import com.bibliotecaLagos.Reserva.DTO.SocioDTO;
import com.bibliotecaLagos.Reserva.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Reserva.Model.Reserva;
import com.bibliotecaLagos.Reserva.Repository.ReservaRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    @Qualifier("webClientLibros")
    private WebClient webClientLibros;

    @Autowired
    @Qualifier("webClientSocios")
    private WebClient webClientSocios;

    public List<Reserva> obtenerReservas() {
        log.info("Iniciando consulta de todas las reservas");
        List<Reserva> reservas = reservaRepository.findAll();
        log.info("Consulta completada: {} reservas encontradas", reservas.size());
        return reservas;
    }

    public Optional<Reserva> obtenerReservaPorId(Integer id) {
        log.info("Buscando reserva por ID: {}", id);
        Optional<Reserva> reserva = reservaRepository.findById(id);
        if (reserva.isPresent()) {
            log.info("Reserva encontrada: ID={}", reserva.get().getId());
        } else {
            log.warn("Reserva con ID {} no encontrada", id);
        }
        return reserva;
    }

    public Reserva crearReserva(ReservaDTO dto) {
        log.info("Iniciando creacion de reserva: libroId={}, socioId={}", dto.getLibroId(), dto.getSocioId());

        log.info("Validando libro ID={} con microservicio libros", dto.getLibroId());
        LibroDTO libro = webClientLibros.get()
                .uri("/{id}", dto.getLibroId())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> {
                            log.warn("Libro ID={} no encontrado en microservicio", dto.getLibroId());
                            return Mono.error(new ResourceNotFoundException("Libro no encontrado"));
                        })
                .bodyToMono(LibroDTO.class)
                .block();
        log.info("Libro validado: ID={}", libro.getId());

        log.info("Validando socio ID={} con microservicio socios", dto.getSocioId());
        SocioDTO socio = webClientSocios.get()
                .uri("/{id}", dto.getSocioId())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> {
                            log.warn("Socio ID={} no encontrado en microservicio", dto.getSocioId());
                            return Mono.error(new ResourceNotFoundException("Socio no encontrado"));
                        })
                .bodyToMono(SocioDTO.class)
                .block();
        log.info("Socio validado: ID={}", socio.getId());

        Reserva reserva = new Reserva();
        reserva.setLibroId(libro.getId());
        reserva.setSocioId(socio.getId());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setEstado(dto.getEstado());

        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada exitosamente: ID={}", guardada.getId());
        return guardada;
    }

    public Reserva actualizarReserva(Integer id, ReservaDTO dto) {
        log.info("Iniciando actualizacion de reserva ID={}", id);
        Reserva reserva = obtenerReservaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        reserva.setSocioId(dto.getSocioId());
        reserva.setLibroId(dto.getLibroId());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setEstado(dto.getEstado());
        Reserva actualizada = reservaRepository.save(reserva);
        log.info("Reserva ID={} actualizada exitosamente", id);
        return actualizada;
    }

    public void eliminarReserva(Integer id) {
        log.info("Iniciando eliminacion de reserva ID={}", id);
        Reserva reserva = obtenerReservaPorId(id).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        reservaRepository.delete(reserva);
        log.info("Reserva ID={} eliminada exitosamente", id);
    }
}