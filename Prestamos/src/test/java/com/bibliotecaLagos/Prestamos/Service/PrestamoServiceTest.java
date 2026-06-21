package com.bibliotecaLagos.Prestamos.Service;

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

import com.bibliotecaLagos.Prestamos.DTO.LibroDTO;
import com.bibliotecaLagos.Prestamos.DTO.PrestamoDTO;
import com.bibliotecaLagos.Prestamos.DTO.SocioDTO;
import com.bibliotecaLagos.Prestamos.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Prestamos.Model.Prestamo;
import com.bibliotecaLagos.Prestamos.Repository.PrestamoRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

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
    private PrestamoService prestamoService;

    private Prestamo crearPrestamoEjemplo(Integer id) {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setSocioId(1);
        prestamo.setLibroId(1);
        prestamo.setFechaPrestamo(LocalDate.of(2026, 6, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2026, 6, 15));
        prestamo.setEstado("Prestado");
        return prestamo;
    }

    @Test
    @DisplayName("obtenerPrestamos -> Retorna lista de prestamos")
    public void obtenerPrestamos_DeberiaRetornarLista() {
        var prestamo = crearPrestamoEjemplo(1);

        when(prestamoRepository.findAll()).thenReturn(List.of(prestamo));

        List<Prestamo> resultado = prestamoService.obtenerPrestamos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Prestado", resultado.get(0).getEstado());
        verify(prestamoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPrestamoPorId -> Retorna prestamo cuando el ID existe")
    public void obtenerPrestamoPorId_CuandoExiste_DeberiaRetornarPrestamo() {
        var prestamo = crearPrestamoEjemplo(1);

        when(prestamoRepository.findById(1)).thenReturn(Optional.of(prestamo));

        Prestamo resultado = prestamoService.obtenerPrestamoPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Prestado", resultado.getEstado());
        verify(prestamoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("obtenerPrestamoPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void obtenerPrestamoPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(prestamoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prestamoService.obtenerPrestamoPorId(99));
        verify(prestamoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearPrestamo -> Crea y retorna prestamo cuando hay stock")
    public void crearPrestamo_CuandoHayStock_DeberiaCrear() {
        var dto = new PrestamoDTO();
        dto.setSocioId(1);
        dto.setLibroId(1);
        dto.setFechaPrestamo(LocalDate.of(2026, 6, 1));
        dto.setFechaDevolucion(LocalDate.of(2026, 6, 15));
        dto.setEstado("Prestado");

        var prestamoGuardado = crearPrestamoEjemplo(1);
        var libro = new LibroDTO();
        libro.setId(1);
        libro.setCantidadDisponible(5);
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

        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoGuardado);

        Prestamo resultado = prestamoService.crearPrestamo(dto);

        assertNotNull(resultado);
        assertEquals("Prestado", resultado.getEstado());
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("crearPrestamo -> Lanza ResourceNotFoundException cuando no hay stock")
    public void crearPrestamo_CuandoSinStock_DeberiaLanzarExcepcion() {
        var dto = new PrestamoDTO();
        dto.setSocioId(1);
        dto.setLibroId(1);
        dto.setFechaPrestamo(LocalDate.of(2026, 6, 1));
        dto.setFechaDevolucion(LocalDate.of(2026, 6, 15));
        dto.setEstado("Prestado");

        var libro = new LibroDTO();
        libro.setId(1);
        libro.setCantidadDisponible(0);
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

        assertThrows(ResourceNotFoundException.class,
                () -> prestamoService.crearPrestamo(dto));
        verify(prestamoRepository, times(0)).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("eliminarPrestamo -> Elimina prestamo cuando el ID existe")
    public void eliminarPrestamo_CuandoExiste_DeberiaEliminar() {
        var prestamo = crearPrestamoEjemplo(1);

        when(prestamoRepository.findById(1)).thenReturn(Optional.of(prestamo));
        doNothing().when(prestamoRepository).delete(prestamo);

        prestamoService.eliminarPrestamo(1);

        verify(prestamoRepository, times(1)).findById(1);
        verify(prestamoRepository, times(1)).delete(prestamo);
    }

    @Test
    @DisplayName("eliminarPrestamo -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarPrestamo_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(prestamoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prestamoService.eliminarPrestamo(99));
        verify(prestamoRepository, times(1)).findById(99);
        verify(prestamoRepository, times(0)).delete(any(Prestamo.class));
    }
}
