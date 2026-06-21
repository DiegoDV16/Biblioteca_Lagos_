package com.bibliotecaLagos.Socios.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Socios.DTO.SocioDTO;
import com.bibliotecaLagos.Socios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.Socios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Socios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Socios.Model.Socio;
import com.bibliotecaLagos.Socios.Repository.SocioRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class SocioServiceTest {

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private WebClient webClientTipoSocio;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SocioService socioService;

    private Socio crearSocioEjemplo(Integer id) {
        Socio socio = new Socio();
        socio.setId(id);
        socio.setNombre("Juan");
        socio.setApellido("Perez");
        socio.setRut("12345678-9");
        socio.setCorreo("juan@correo.com");
        socio.setTelefono("987654321");
        socio.setIdTipoSocio(1);
        return socio;
    }

    @Test
    @DisplayName("obtenerSocios -> Retorna lista de socios")
    public void obtenerSocios_DeberiaRetornarLista() {
        var socio = crearSocioEjemplo(1);

        when(socioRepository.findAll()).thenReturn(List.of(socio));

        List<Socio> resultado = socioService.obtenerSocios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(socioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerSocioPorId -> Retorna socio cuando el ID existe")
    public void obtenerSocioPorId_CuandoExiste_DeberiaRetornarSocio() {
        var socio = crearSocioEjemplo(1);

        when(socioRepository.findById(1)).thenReturn(Optional.of(socio));

        Socio resultado = socioService.obtenerSocioPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        verify(socioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("obtenerSocioPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void obtenerSocioPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(socioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> socioService.obtenerSocioPorId(99));
        verify(socioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearSocio -> Crea y retorna socio cuando los datos son validos")
    public void crearSocio_CuandoDatosValidos_DeberiaCrear() {
        var dto = new SocioDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setRut("12345678-9");
        dto.setCorreo("juan@correo.com");
        dto.setTelefono("987654321");
        dto.setIdTipoSocio(1);

        var socioGuardado = crearSocioEjemplo(1);
        var tipoSocioDTO = new TipoSocioDTO(1, "Estudiante");

        when(socioRepository.findByRut("12345678-9")).thenReturn(Optional.empty());
        when(socioRepository.findByCorreo("juan@correo.com")).thenReturn(Optional.empty());
        when(webClientTipoSocio.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", 1)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(java.util.function.Predicate.class), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TipoSocioDTO.class)).thenReturn(Mono.just(tipoSocioDTO));
        when(socioRepository.save(any(Socio.class))).thenReturn(socioGuardado);

        Socio resultado = socioService.crearSocio(dto);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(socioRepository, times(1)).findByRut("12345678-9");
        verify(socioRepository, times(1)).findByCorreo("juan@correo.com");
        verify(socioRepository, times(1)).save(any(Socio.class));
    }

    @Test
    @DisplayName("crearSocio -> Lanza DuplicateResourceException cuando el rut ya existe")
    public void crearSocio_CuandoRutDuplicado_DeberiaLanzarExcepcion() {
        var dto = new SocioDTO();
        dto.setRut("12345678-9");
        dto.setCorreo("juan@correo.com");

        var existente = crearSocioEjemplo(1);

        when(socioRepository.findByRut("12345678-9")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> socioService.crearSocio(dto));
        verify(socioRepository, times(1)).findByRut("12345678-9");
        verify(socioRepository, times(0)).save(any(Socio.class));
    }

    @Test
    @DisplayName("crearSocio -> Lanza DuplicateResourceException cuando el correo ya existe")
    public void crearSocio_CuandoCorreoDuplicado_DeberiaLanzarExcepcion() {
        var dto = new SocioDTO();
        dto.setRut("12345678-9");
        dto.setCorreo("juan@correo.com");

        when(socioRepository.findByRut("12345678-9")).thenReturn(Optional.empty());
        when(socioRepository.findByCorreo("juan@correo.com")).thenReturn(Optional.of(crearSocioEjemplo(1)));

        assertThrows(DuplicateResourceException.class,
                () -> socioService.crearSocio(dto));
        verify(socioRepository, times(1)).findByRut("12345678-9");
        verify(socioRepository, times(1)).findByCorreo("juan@correo.com");
        verify(socioRepository, times(0)).save(any(Socio.class));
    }

    @Test
    @DisplayName("actualizarSocio -> Actualiza y retorna socio cuando el ID existe")
    public void actualizarSocio_CuandoExiste_DeberiaActualizar() {
        var existente = crearSocioEjemplo(1);

        var dto = new SocioDTO();
        dto.setNombre("Carlos");
        dto.setApellido("Perez");
        dto.setRut("12345678-9");
        dto.setCorreo("juan@correo.com");
        dto.setTelefono("987654321");
        dto.setIdTipoSocio(1);

        when(socioRepository.findById(1)).thenReturn(Optional.of(existente));
        when(socioRepository.save(any(Socio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Socio resultado = socioService.actualizarSocio(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Carlos", resultado.getNombre());
        verify(socioRepository, times(1)).findById(1);
        verify(socioRepository, times(1)).save(any(Socio.class));
    }

    @Test
    @DisplayName("actualizarSocio -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarSocio_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new SocioDTO();
        dto.setNombre("Carlos");

        when(socioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> socioService.actualizarSocio(99, dto));
        verify(socioRepository, times(1)).findById(99);
        verify(socioRepository, times(0)).save(any(Socio.class));
    }

    @Test
    @DisplayName("eliminarSocio -> Elimina socio cuando el ID existe")
    public void eliminarSocio_CuandoExiste_DeberiaEliminar() {
        var socio = crearSocioEjemplo(1);

        when(socioRepository.findById(1)).thenReturn(Optional.of(socio));
        doNothing().when(socioRepository).delete(socio);

        socioService.eliminarSocio(1);

        verify(socioRepository, times(1)).findById(1);
        verify(socioRepository, times(1)).delete(socio);
    }

    @Test
    @DisplayName("eliminarSocio -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarSocio_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(socioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> socioService.eliminarSocio(99));
        verify(socioRepository, times(1)).findById(99);
        verify(socioRepository, times(0)).delete(any(Socio.class));
    }
}
