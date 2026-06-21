package com.bibliotecaLagos.tipoSocios.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.tipoSocios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.tipoSocios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;
import com.bibliotecaLagos.tipoSocios.Repository.TipoSocioRepository;

@ExtendWith(MockitoExtension.class)
public class TipoSocioServiceTest {

    @Mock
    private TipoSocioRepository tipoSocioRepository;

    @InjectMocks
    private TipoSocioService tipoSocioService;

    private TipoSocio crearTipoSocioEjemplo(Integer id) {
        TipoSocio tipoSocio = new TipoSocio();
        tipoSocio.setId(id);
        tipoSocio.setTipoSocio("Estudiante");
        return tipoSocio;
    }

    @Test
    @DisplayName("obtenerTiposSocio -> Retorna lista de tipos de socio")
    public void obtenerTiposSocio_DeberiaRetornarLista() {
        var tipo = crearTipoSocioEjemplo(1);

        when(tipoSocioRepository.findAll()).thenReturn(List.of(tipo));

        List<TipoSocio> resultado = tipoSocioService.obtenerTiposSocio();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Estudiante", resultado.get(0).getTipoSocio());
        verify(tipoSocioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTipoSocioPorId -> Retorna tipo de socio cuando el ID existe")
    public void obtenerTipoSocioPorId_CuandoExiste_DeberiaRetornarTipo() {
        var tipo = crearTipoSocioEjemplo(1);

        when(tipoSocioRepository.findById(1)).thenReturn(Optional.of(tipo));

        TipoSocio resultado = tipoSocioService.obtenerTipoSocioPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Estudiante", resultado.getTipoSocio());
        verify(tipoSocioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("obtenerTipoSocioPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void obtenerTipoSocioPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(tipoSocioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tipoSocioService.obtenerTipoSocioPorId(99));
        verify(tipoSocioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearTipoSocio -> Crea y retorna tipo de socio cuando el nombre es unico")
    public void crearTipoSocio_CuandoNombreUnico_DeberiaCrear() {
        var dto = new TipoSocioDTO();
        dto.setTipoSocio("Estudiante");

        var tipoGuardado = crearTipoSocioEjemplo(1);

        when(tipoSocioRepository.findByTipoSocio("Estudiante")).thenReturn(Optional.empty());
        when(tipoSocioRepository.save(any(TipoSocio.class))).thenReturn(tipoGuardado);

        TipoSocio resultado = tipoSocioService.crearTipoSocio(dto);

        assertNotNull(resultado);
        assertEquals("Estudiante", resultado.getTipoSocio());
        verify(tipoSocioRepository, times(1)).findByTipoSocio("Estudiante");
        verify(tipoSocioRepository, times(1)).save(any(TipoSocio.class));
    }

    @Test
    @DisplayName("crearTipoSocio -> Lanza DuplicateResourceException cuando el nombre ya existe")
    public void crearTipoSocio_CuandoNombreDuplicado_DeberiaLanzarExcepcion() {
        var dto = new TipoSocioDTO();
        dto.setTipoSocio("Estudiante");

        var existente = crearTipoSocioEjemplo(1);

        when(tipoSocioRepository.findByTipoSocio("Estudiante")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> tipoSocioService.crearTipoSocio(dto));
        verify(tipoSocioRepository, times(1)).findByTipoSocio("Estudiante");
        verify(tipoSocioRepository, times(0)).save(any(TipoSocio.class));
    }

    @Test
    @DisplayName("actualizarTipoSocio -> Actualiza y retorna tipo de socio cuando el ID existe")
    public void actualizarTipoSocio_CuandoExiste_DeberiaActualizar() {
        var existente = crearTipoSocioEjemplo(1);

        var dto = new TipoSocioDTO();
        dto.setTipoSocio("Profesor");

        when(tipoSocioRepository.findById(1)).thenReturn(Optional.of(existente));
        when(tipoSocioRepository.save(any(TipoSocio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoSocio resultado = tipoSocioService.actualizarTipoSocio(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Profesor", resultado.getTipoSocio());
        verify(tipoSocioRepository, times(1)).findById(1);
        verify(tipoSocioRepository, times(1)).save(any(TipoSocio.class));
    }

    @Test
    @DisplayName("actualizarTipoSocio -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarTipoSocio_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new TipoSocioDTO();
        dto.setTipoSocio("Profesor");

        when(tipoSocioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tipoSocioService.actualizarTipoSocio(99, dto));
        verify(tipoSocioRepository, times(1)).findById(99);
        verify(tipoSocioRepository, times(0)).save(any(TipoSocio.class));
    }

    @Test
    @DisplayName("eliminarTipoSocio -> Elimina tipo de socio cuando el ID existe")
    public void eliminarTipoSocio_CuandoExiste_DeberiaEliminar() {
        var tipo = crearTipoSocioEjemplo(1);

        when(tipoSocioRepository.findById(1)).thenReturn(Optional.of(tipo));
        doNothing().when(tipoSocioRepository).delete(tipo);

        tipoSocioService.eliminarTipoSocio(1);

        verify(tipoSocioRepository, times(1)).findById(1);
        verify(tipoSocioRepository, times(1)).delete(tipo);
    }

    @Test
    @DisplayName("eliminarTipoSocio -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarTipoSocio_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(tipoSocioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tipoSocioService.eliminarTipoSocio(99));
        verify(tipoSocioRepository, times(1)).findById(99);
        verify(tipoSocioRepository, times(0)).delete(any(TipoSocio.class));
    }
}
