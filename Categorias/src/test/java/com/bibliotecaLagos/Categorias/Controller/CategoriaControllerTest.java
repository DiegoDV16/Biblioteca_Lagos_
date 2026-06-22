package com.bibliotecaLagos.Categorias.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.Categorias.Assemblers.CategoriaModelAssembler;
import com.bibliotecaLagos.Categorias.DTO.CategoriaDTO;
import com.bibliotecaLagos.Categorias.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Categorias.Model.Categoria;
import com.bibliotecaLagos.Categorias.Service.CategoriaService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(CategoriaController.class)
@Import(CategoriaModelAssembler.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    @DisplayName("GET /api/v1/categorias -> Retorna 200 con lista HATEOAS")
    public void obtenerCategorias_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Fantasia");
        categoria.setDescripcion("Libros de fantasia");

        when(categoriaService.obtenerCategorias()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.categoriaList.size()").value(1))
                .andExpect(jsonPath("$._embedded.categoriaList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.categoriaList[0].nombre").value("Fantasia"))
                .andExpect(jsonPath("$._embedded.categoriaList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/categorias -> Retorna 204 si no hay categorias")
    public void obtenerCategorias_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(categoriaService.obtenerCategorias()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/categorias/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarCategoria() throws Exception {
        var categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Fantasia");
        categoria.setDescripcion("Libros de fantasia");

        when(categoriaService.buscarPorId(anyInt())).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/v1/categorias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Fantasia"))
                .andExpect(jsonPath("$.descripcion").value("Libros de fantasia"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.categorias.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/categorias/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(categoriaService.buscarPorId(99))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categorias/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No existe la categoria con ID: 99"));
    }

    @Test
    @DisplayName("POST /api/v1/categorias -> Retorna 201 con HATEOAS")
    public void crearCategoria_DeberiaRetornar201() throws Exception {
        var categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1);
        categoriaGuardada.setNombre("Fantasia");
        categoriaGuardada.setDescripcion("Libros de fantasia");

        when(categoriaService.crearCategoria(any(CategoriaDTO.class))).thenReturn(categoriaGuardada);

        String jsonRequestBody = """
                {
                    "nombre": "Fantasia",
                    "descripcion": "Libros de fantasia"
                }
                """;

        mockMvc.perform(post("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Fantasia"))
                .andExpect(jsonPath("$.descripcion").value("Libros de fantasia"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/categorias -> Retorna 400 cuando el nombre esta vacio")
    public void crearCategoria_CuandoNombreVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "nombre": "",
                    "descripcion": "Libros"
                }
                """;

        mockMvc.perform(post("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/categorias/{id} -> Retorna 200 con HATEOAS")
    public void actualizarCategoria_DeberiaRetornar200() throws Exception {
        var categoriaActualizada = new Categoria();
        categoriaActualizada.setId(1);
        categoriaActualizada.setNombre("Ciencia Ficcion");
        categoriaActualizada.setDescripcion("Libros de ciencia ficcion");

        when(categoriaService.actualizarCategoria(anyInt(), any(CategoriaDTO.class)))
                .thenReturn(categoriaActualizada);

        String jsonRequestBody = """
                {
                    "nombre": "Ciencia Ficcion",
                    "descripcion": "Libros de ciencia ficcion"
                }
                """;

        mockMvc.perform(put("/api/v1/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ciencia Ficcion"))
                .andExpect(jsonPath("$.descripcion").value("Libros de ciencia ficcion"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/categorias/{id} -> Retorna 404 si el ID no existe")
    public void actualizarCategoria_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(categoriaService.actualizarCategoria(anyInt(), any(CategoriaDTO.class)))
                .thenThrow(new ResourceNotFoundException("Categoria no encontrada"));

        String jsonRequestBody = """
                {
                    "nombre": "Test"
                }
                """;

        mockMvc.perform(put("/api/v1/categorias/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/categorias/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarCategoria_DeberiaRetornar200() throws Exception {
        doNothing().when(categoriaService).eliminarCategoria(1);

        mockMvc.perform(delete("/api/v1/categorias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Categoria eliminada correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/categorias/{id} -> Retorna 404 si el ID no existe")
    public void eliminarCategoria_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Categoria no encontrada"))
                .when(categoriaService).eliminarCategoria(99);

        mockMvc.perform(delete("/api/v1/categorias/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
