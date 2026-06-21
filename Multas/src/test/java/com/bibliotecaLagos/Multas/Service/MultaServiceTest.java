package com.bibliotecaLagos.Multas.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.DTO.PrestamoDTO;
import com.bibliotecaLagos.Multas.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Multas.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Repository.MultaRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private WebClient webClientPrestamos;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MultaService multaService;

    private Multa crearMultaEjemplo(Integer id) {
        Multa multa = new Multa();
        multa.setId(id);
        multa.setPrestamoId(1);
        multa.setMonto(new BigDecimal("50.00"));
        multa.setDiasRetraso(5);
        multa.setPagada(false);
        return multa;
    }

    @Test
    @DisplayName("obtenerMultas -> Retorna lista de multas")
    public void obtenerMultas_DeberiaRetornarLista() {
        var multa = crearMultaEjemplo(1);

        when(multaRepository.findAll()).thenReturn(List.of(multa));

        List<Multa> resultado = multaService.obtenerMultas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(false, resultado.get(0).getPagada());
        verify(multaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId -> Retorna multa cuando el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarMulta() {
        var multa = crearMultaEjemplo(1);

        when(multaRepository.findById(1)).thenReturn(Optional.of(multa));

        Multa resultado = multaService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(new BigDecimal("50.00"), resultado.getMonto());
        verify(multaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("buscarPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(multaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> multaService.buscarPorId(99));
        verify(multaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearMulta -> Crea y retorna multa cuando el prestamo existe")
    public void crearMulta_CuandoPrestamoExiste_DeberiaCrear() {
        var dto = new MultaDTO();
        dto.setPrestamoId(1);
        dto.setMonto(new BigDecimal("50.00"));
        dto.setDiasRetraso(5);
        dto.setPagada(false);

        var multaGuardada = crearMultaEjemplo(1);
        var prestamo = new PrestamoDTO();
        prestamo.setId(1);

        when(multaRepository.findByPrestamoId(1)).thenReturn(Optional.empty());

        when(webClientPrestamos.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", 1)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(java.util.function.Predicate.class), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PrestamoDTO.class)).thenReturn(Mono.just(prestamo));

        when(multaRepository.save(any(Multa.class))).thenReturn(multaGuardada);

        Multa resultado = multaService.crearMulta(dto);

        assertNotNull(resultado);
        assertEquals(false, resultado.getPagada());
        assertEquals(new BigDecimal("50.00"), resultado.getMonto());
        verify(multaRepository, times(1)).save(any(Multa.class));
    }

    @Test
    @DisplayName("crearMulta -> Lanza DuplicateResourceException cuando ya existe multa para el prestamo")
    public void crearMulta_CuandoYaExiste_DeberiaLanzarExcepcion() {
        var dto = new MultaDTO();
        dto.setPrestamoId(1);
        dto.setMonto(new BigDecimal("50.00"));
        dto.setDiasRetraso(5);

        when(multaRepository.findByPrestamoId(1)).thenReturn(Optional.of(crearMultaEjemplo(1)));

        assertThrows(DuplicateResourceException.class,
                () -> multaService.crearMulta(dto));
        verify(multaRepository, times(0)).save(any(Multa.class));
    }

    @Test
    @DisplayName("actualizarMulta -> Actualiza y retorna multa")
    public void actualizarMulta_DeberiaActualizar() {
        var multaExistente = crearMultaEjemplo(1);
        var dto = new MultaDTO();
        dto.setPrestamoId(1);
        dto.setMonto(new BigDecimal("75.00"));
        dto.setDiasRetraso(10);
        dto.setPagada(true);

        when(multaRepository.findById(1)).thenReturn(Optional.of(multaExistente));
        when(multaRepository.save(any(Multa.class))).thenAnswer(i -> i.getArgument(0));

        Multa resultado = multaService.actualizarMulta(1, dto);

        assertNotNull(resultado);
        assertEquals(true, resultado.getPagada());
        assertEquals(new BigDecimal("75.00"), resultado.getMonto());
        assertEquals(10, resultado.getDiasRetraso());
        verify(multaRepository, times(1)).save(any(Multa.class));
    }

    @Test
    @DisplayName("eliminarMulta -> Elimina multa cuando el ID existe")
    public void eliminarMulta_CuandoExiste_DeberiaEliminar() {
        var multa = crearMultaEjemplo(1);

        when(multaRepository.findById(1)).thenReturn(Optional.of(multa));
        doNothing().when(multaRepository).delete(multa);

        multaService.eliminarMulta(1);

        verify(multaRepository, times(1)).findById(1);
        verify(multaRepository, times(1)).delete(multa);
    }

    @Test
    @DisplayName("eliminarMulta -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarMulta_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(multaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> multaService.eliminarMulta(99));
        verify(multaRepository, times(1)).findById(99);
        verify(multaRepository, times(0)).delete(any(Multa.class));
    }
}
