package com.bibliotecaLagos.libros.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.libros.Assemblers.LibroModelAssembler;
import com.bibliotecaLagos.libros.DTO.LibroDTO;
import com.bibliotecaLagos.libros.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.libros.Model.Libro;
import com.bibliotecaLagos.libros.Service.LibroService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(LibroController.class)
@Import(LibroModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    @DisplayName("GET /api/v1/libros -> Retorna 200 con lista HATEOAS")
    public void obtenerLibros_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var libro = crearLibroEjemplo(1);

        when(libroService.obtenerLibros()).thenReturn(List.of(libro));

        mockMvc.perform(get("/api/v1/libros")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.libroList.size()").value(1))
                .andExpect(jsonPath("$._embedded.libroList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.libroList[0].titulo").value("Cien Anios de Soledad"))
                .andExpect(jsonPath("$._embedded.libroList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/libros -> Retorna 204 si no hay libros")
    public void obtenerLibros_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(libroService.obtenerLibros()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/libros")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarLibro() throws Exception {
        var libro = crearLibroEjemplo(1);

        when(libroService.buscarPorId(1)).thenReturn(libro);

        mockMvc.perform(get("/api/v1/libros/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Cien Anios de Soledad"))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.libros.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(libroService.buscarPorId(99))
                .thenThrow(new ResourceNotFoundException("Libro no encontrado"));

        mockMvc.perform(get("/api/v1/libros/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/libros/isbn/{isbn} -> Retorna 200 con HATEOAS")
    public void buscarPorIsbn_CuandoExiste_DeberiaRetornarLibro() throws Exception {
        var libro = crearLibroEjemplo(1);

        when(libroService.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libro);

        mockMvc.perform(get("/api/v1/libros/isbn/978-3-16-148410-0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/libros -> Retorna 201 con HATEOAS")
    public void crearLibro_DeberiaRetornar201() throws Exception {
        var libroGuardado = crearLibroEjemplo(1);

        when(libroService.crearLibro(any(LibroDTO.class))).thenReturn(libroGuardado);

        String jsonRequestBody = """
                {
                    "titulo": "Cien Anios de Soledad",
                    "autor": "Gabriel Garcia Marquez",
                    "isbn": "978-3-16-148410-0",
                    "editorial": "Editorial Sudamericana",
                    "anioPublicacion": 1967,
                    "cantidadDisponible": 5,
                    "cantidadTotal": 10,
                    "categoriaId": 1,
                    "proveedorId": 1,
                    "estado": "Disponible"
                }
                """;

        mockMvc.perform(post("/api/v1/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Cien Anios de Soledad"))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/libros -> Retorna 400 cuando el titulo esta vacio")
    public void crearLibro_CuandoTituloVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "titulo": "",
                    "autor": "Gabriel Garcia Marquez",
                    "isbn": "978-3-16-148410-0",
                    "editorial": "Editorial Sudamericana",
                    "anioPublicacion": 1967,
                    "cantidadDisponible": 5,
                    "cantidadTotal": 10,
                    "categoriaId": 1,
                    "proveedorId": 1,
                    "estado": "Disponible"
                }
                """;

        mockMvc.perform(post("/api/v1/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/libros/{id} -> Retorna 200 con HATEOAS")
    public void actualizarLibro_DeberiaRetornar200() throws Exception {
        var libroActualizado = crearLibroEjemplo(1);
        libroActualizado.setTitulo("El Amor en los Tiempos del Colera");

        when(libroService.actualizarLibro(anyInt(), any(LibroDTO.class)))
                .thenReturn(libroActualizado);

        String jsonRequestBody = """
                {
                    "titulo": "El Amor en los Tiempos del Colera",
                    "autor": "Gabriel Garcia Marquez",
                    "isbn": "978-3-16-148410-0",
                    "editorial": "Editorial Sudamericana",
                    "anioPublicacion": 1985,
                    "cantidadDisponible": 5,
                    "cantidadTotal": 10,
                    "categoriaId": 1,
                    "proveedorId": 1,
                    "estado": "Disponible"
                }
                """;

        mockMvc.perform(put("/api/v1/libros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("El Amor en los Tiempos del Colera"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/libros/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarLibro_DeberiaRetornar200() throws Exception {
        doNothing().when(libroService).eliminarLibro(1);

        mockMvc.perform(delete("/api/v1/libros/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Libro eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/libros/{id} -> Retorna 200 aunque el ID no exista")
    public void eliminarLibro_CuandoNoExiste_DeberiaRetornar200() throws Exception {
        doNothing().when(libroService).eliminarLibro(99);

        mockMvc.perform(delete("/api/v1/libros/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
