package com.bibliotecaLagos.Reserva.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Reserva.DTO.LibroDTO;
import com.bibliotecaLagos.Reserva.DTO.ReservaDTO;
import com.bibliotecaLagos.Reserva.DTO.SocioDTO;
import com.bibliotecaLagos.Reserva.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Reserva.Model.Reserva;
import com.bibliotecaLagos.Reserva.Repository.ReservaRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private WebClient webClientLibros;

    @Mock
    private WebClient webClientSocios;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva crearReservaEjemplo(Integer id) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setSocioId(1);
        reserva.setLibroId(1);
        reserva.setFechaReserva(LocalDate.of(2026, 6, 21));
        reserva.setEstado("Pendiente");
        return reserva;
    }

    @Test
    @DisplayName("obtenerReservas -> Retorna lista de reservas")
    public void obtenerReservas_DeberiaRetornarLista() {
        var reserva = crearReservaEjemplo(1);

        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<Reserva> resultado = reservaService.obtenerReservas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Pendiente", resultado.get(0).getEstado());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerReservaPorId -> Retorna reserva cuando el ID existe")
    public void obtenerReservaPorId_CuandoExiste_DeberiaRetornarReserva() {
        var reserva = crearReservaEjemplo(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        Reserva resultado = reservaService.obtenerReservaPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Pendiente", resultado.getEstado());
        verify(reservaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("obtenerReservaPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void obtenerReservaPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservaService.obtenerReservaPorId(99));
        verify(reservaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearReserva -> Crea y retorna reserva cuando libro y socio existen")
    public void crearReserva_CuandoLibroYSocioExisten_DeberiaCrear() {
        var dto = new ReservaDTO();
        dto.setSocioId(1);
        dto.setLibroId(1);
        dto.setFechaReserva(LocalDate.of(2026, 6, 21));
        dto.setEstado("Pendiente");

        var reservaGuardada = crearReservaEjemplo(1);
        var libro = new LibroDTO();
        libro.setId(1);
        var socio = new SocioDTO();
        socio.setId(1);

        when(webClientLibros.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", 1)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(java.util.function.Predicate.class), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LibroDTO.class)).thenReturn(Mono.just(libro));

        when(webClientSocios.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", 1)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SocioDTO.class)).thenReturn(Mono.just(socio));

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        Reserva resultado = reservaService.crearReserva(dto);

        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("eliminarReserva -> Elimina reserva cuando el ID existe")
    public void eliminarReserva_CuandoExiste_DeberiaEliminar() {
        var reserva = crearReservaEjemplo(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaRepository).delete(reserva);

        reservaService.eliminarReserva(1);

        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).delete(reserva);
    }

    @Test
    @DisplayName("eliminarReserva -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarReserva_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservaService.eliminarReserva(99));
        verify(reservaRepository, times(1)).findById(99);
        verify(reservaRepository, times(0)).delete(any(Reserva.class));
    }
}
