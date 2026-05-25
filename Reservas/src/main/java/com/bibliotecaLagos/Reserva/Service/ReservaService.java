package com.bibliotecaLagos.Reserva.Service;

import java.util.List;
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

import reactor.core.publisher.Mono;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    @Qualifier("webClientLibros")
    private WebClient webClientLibros;

    @Autowired
    @Qualifier("webClientSocios")
    private WebClient webClientSocios;

    public List<Reserva> obtenerReservas() {

        return reservaRepository.findAll();
    }

    public Reserva obtenerReservaPorId(Integer id) {

        return reservaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }

    public Reserva crearReserva(ReservaDTO dto) {

        LibroDTO libro = webClientLibros.get()
        .uri("/{id}", dto.getLibroId())
        .retrieve()
        .onStatus(
        HttpStatusCode::is4xxClientError,
        response -> Mono.error(
                new ResourceNotFoundException(
                        "Libro no encontrado"
                )
        )
        )
        .bodyToMono(LibroDTO.class)
        .block();

        SocioDTO socio = webClientSocios.get()
        .uri("/{id}", dto.getSocioId())
        .retrieve()
        .onStatus(
                HttpStatusCode::is4xxClientError,
                response -> Mono.error(
                        new ResourceNotFoundException(
                                "Socio no encontrado"
                        )
                )
        )
        .bodyToMono(SocioDTO.class)
        .block();

        Reserva reserva = new Reserva();

        reserva.setLibroId(libro.getId()
        );

        reserva.setSocioId(socio.getId()
        );

        reserva.setFechaReserva(dto.getFechaReserva()
        );

        reserva.setEstado(dto.getEstado()
        );

        return reservaRepository.save(reserva);
    }

    public void eliminarReserva(Integer id) {

        Reserva reserva = obtenerReservaPorId(id);

        reservaRepository.delete(reserva);
    }
}