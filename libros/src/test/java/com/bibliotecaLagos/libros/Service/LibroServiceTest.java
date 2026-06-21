package com.bibliotecaLagos.libros.Service;

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
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.libros.DTO.CategoriaDTO;
import com.bibliotecaLagos.libros.DTO.LibroDTO;
import com.bibliotecaLagos.libros.DTO.ProveedorDTO;
import com.bibliotecaLagos.libros.Exception.DuplicateResourceException;
import com.bibliotecaLagos.libros.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.libros.Model.Libro;
import com.bibliotecaLagos.libros.Repository.LibroRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    @SuppressWarnings("unused")
    private WebClient webClientCategorias;

    @Mock
    @SuppressWarnings("unused")
    private WebClient webClientProveedores;

    @InjectMocks
    private LibroService libroService;

    private Libro crearLibroEjemplo(Integer id) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setTitulo("Cien Anios de Soledad");
        libro.setAutor("Gabriel Garcia Marquez");
        libro.setIsbn("978-3-16-148410-0");
        libro.setEditorial("Editorial Sudamericana");
        libro.setAnioPublicacion(1967);
        libro.setCantidadDisponible(5);
        libro.setCantidadTotal(10);
        libro.setCategoriaId(1);
        libro.setProveedorId(1);
        libro.setEstado("Disponible");
        return libro;
    }

    @Test
    @DisplayName("obtenerLibros -> Retorna lista de libros")
    public void obtenerLibros_DeberiaRetornarLista() {
        var libro = crearLibroEjemplo(1);

        when(libroRepository.findAll()).thenReturn(List.of(libro));

        List<Libro> resultado = libroService.obtenerLibros();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Cien Anios de Soledad", resultado.get(0).getTitulo());
        verify(libroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId -> Retorna libro cuando el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarLibro() {
        var libro = crearLibroEjemplo(1);

        when(libroRepository.findById(1)).thenReturn(Optional.of(libro));

        Libro resultado = libroService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("978-3-16-148410-0", resultado.getIsbn());
        verify(libroRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("buscarPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(libroRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> libroService.buscarPorId(99));
        verify(libroRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("buscarPorIsbn -> Retorna libro cuando el ISBN existe")
    public void buscarPorIsbn_CuandoExiste_DeberiaRetornarLibro() {
        var libro = crearLibroEjemplo(1);

        when(libroRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Optional.of(libro));

        Libro resultado = libroService.buscarPorIsbn("978-3-16-148410-0");

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("978-3-16-148410-0", resultado.getIsbn());
        verify(libroRepository, times(1)).findByIsbn("978-3-16-148410-0");
    }

    @Test
    @DisplayName("buscarPorIsbn -> Lanza ResourceNotFoundException cuando el ISBN no existe")
    public void buscarPorIsbn_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(libroRepository.findByIsbn("ISBN-INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> libroService.buscarPorIsbn("ISBN-INEXISTENTE"));
        verify(libroRepository, times(1)).findByIsbn("ISBN-INEXISTENTE");
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientCategoria(WebClient webClient, Integer categoriaId, CategoriaDTO response) {
        var requestHeadersUriSpec = org.mockito.Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        var requestHeadersSpec = org.mockito.Mockito.mock(WebClient.RequestHeadersSpec.class);
        var responseSpec = org.mockito.Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", categoriaId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CategoriaDTO.class)).thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientProveedor(WebClient webClient, Integer proveedorId, ProveedorDTO response) {
        var requestHeadersUriSpec = org.mockito.Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        var requestHeadersSpec = org.mockito.Mockito.mock(WebClient.RequestHeadersSpec.class);
        var responseSpec = org.mockito.Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", proveedorId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProveedorDTO.class)).thenReturn(Mono.just(response));
    }

    @Test
    @DisplayName("crearLibro -> Crea y retorna libro cuando el ISBN es unico")
    public void crearLibro_CuandoIsbnUnico_DeberiaCrear() {
        var dto = new LibroDTO();
        dto.setTitulo("Cien Anios de Soledad");
        dto.setAutor("Gabriel Garcia Marquez");
        dto.setIsbn("978-3-16-148410-0");
        dto.setEditorial("Editorial Sudamericana");
        dto.setAnioPublicacion(1967);
        dto.setCantidadDisponible(5);
        dto.setCantidadTotal(10);
        dto.setCategoriaId(1);
        dto.setProveedorId(1);
        dto.setEstado("Disponible");

        var libroGuardado = crearLibroEjemplo(1);

        mockWebClientCategoria(webClientCategorias, 1, new CategoriaDTO(1, "Fantasia", "Libros de fantasia"));
        mockWebClientProveedor(webClientProveedores, 1, new ProveedorDTO(1, "Proveedor 1", "123456789", "correo@test.com", "Direccion"));

        when(libroRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Optional.empty());
        when(libroRepository.save(any(Libro.class))).thenReturn(libroGuardado);

        Libro resultado = libroService.crearLibro(dto);

        assertNotNull(resultado);
        assertEquals("Cien Anios de Soledad", resultado.getTitulo());
        assertEquals("978-3-16-148410-0", resultado.getIsbn());
        verify(libroRepository, times(1)).findByIsbn("978-3-16-148410-0");
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("crearLibro -> Lanza DuplicateResourceException cuando el ISBN ya existe")
    public void crearLibro_CuandoIsbnDuplicado_DeberiaLanzarExcepcion() {
        var dto = new LibroDTO();
        dto.setIsbn("978-3-16-148410-0");

        var existente = crearLibroEjemplo(1);

        when(libroRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> libroService.crearLibro(dto));
        verify(libroRepository, times(1)).findByIsbn("978-3-16-148410-0");
        verify(libroRepository, times(0)).save(any(Libro.class));
    }

    @Test
    @DisplayName("actualizarLibro -> Actualiza y retorna libro cuando el ID existe")
    public void actualizarLibro_CuandoExiste_DeberiaActualizar() {
        var existente = crearLibroEjemplo(1);

        var dto = new LibroDTO();
        dto.setTitulo("El Amor en los Tiempos del Colera");
        dto.setAutor("Gabriel Garcia Marquez");
        dto.setIsbn("978-3-16-148410-0");
        dto.setEditorial("Editorial Sudamericana");
        dto.setAnioPublicacion(1985);
        dto.setCantidadDisponible(3);
        dto.setCantidadTotal(8);
        dto.setCategoriaId(1);
        dto.setProveedorId(1);
        dto.setEstado("Disponible");

        when(libroRepository.findById(1)).thenReturn(Optional.of(existente));
        when(libroRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Optional.of(existente));
        when(libroRepository.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro resultado = libroService.actualizarLibro(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("El Amor en los Tiempos del Colera", resultado.getTitulo());
        verify(libroRepository, times(1)).findById(1);
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("actualizarLibro -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarLibro_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new LibroDTO();
        dto.setIsbn("978-3-16-148410-0");

        when(libroRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> libroService.actualizarLibro(99, dto));
        verify(libroRepository, times(1)).findById(99);
        verify(libroRepository, times(0)).save(any(Libro.class));
    }

    @Test
    @DisplayName("eliminarLibro -> Elimina libro cuando el ID existe")
    public void eliminarLibro_CuandoExiste_DeberiaEliminar() {
        var libro = crearLibroEjemplo(1);

        when(libroRepository.findById(1)).thenReturn(Optional.of(libro));
        doNothing().when(libroRepository).delete(libro);

        libroService.eliminarLibro(1);

        verify(libroRepository, times(1)).findById(1);
        verify(libroRepository, times(1)).delete(libro);
    }

    @Test
    @DisplayName("eliminarLibro -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarLibro_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(libroRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> libroService.eliminarLibro(99));
        verify(libroRepository, times(1)).findById(99);
        verify(libroRepository, times(0)).delete(any(Libro.class));
    }
}
