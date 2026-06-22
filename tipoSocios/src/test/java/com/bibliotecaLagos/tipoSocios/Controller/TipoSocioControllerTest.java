package com.bibliotecaLagos.tipoSocios.Controller;

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

import com.bibliotecaLagos.tipoSocios.Assemblers.TipoSocioModelAssembler;
import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.tipoSocios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;
import com.bibliotecaLagos.tipoSocios.Service.TipoSocioService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(TipoSocioController.class)
@Import(TipoSocioModelAssembler.class)
public class TipoSocioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TipoSocioService tipoSocioService;

    private TipoSocio crearTipoSocioEjemplo(Integer id) {
        TipoSocio tipoSocio = new TipoSocio();
        tipoSocio.setId(id);
        tipoSocio.setTipoSocio("Estudiante");
        return tipoSocio;
    }

    @Test
    @DisplayName("GET /api/v1/tipos-socio -> Retorna 200 con lista HATEOAS")
    public void listar_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var tipo = crearTipoSocioEjemplo(1);

        when(tipoSocioService.obtenerTiposSocio()).thenReturn(List.of(tipo));

        mockMvc.perform(get("/api/v1/tipos-socio")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.tipoSocioList.size()").value(1))
                .andExpect(jsonPath("$._embedded.tipoSocioList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.tipoSocioList[0].tipoSocio").value("Estudiante"))
                .andExpect(jsonPath("$._embedded.tipoSocioList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-socio -> Retorna 204 si no hay tipos de socio")
    public void listar_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(tipoSocioService.obtenerTiposSocio()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tipos-socio")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-socio/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarTipo() throws Exception {
        var tipo = crearTipoSocioEjemplo(1);

        when(tipoSocioService.obtenerTipoSocioPorId(anyInt())).thenReturn(Optional.of(tipo));

        mockMvc.perform(get("/api/v1/tipos-socio/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoSocio").value("Estudiante"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.tipos-socio.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-socio/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(tipoSocioService.obtenerTipoSocioPorId(99))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tipos-socio/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No existe el tipo de socio con ID: 99"));
    }

    @Test
    @DisplayName("POST /api/v1/tipos-socio -> Retorna 201 con HATEOAS")
    public void crear_DeberiaRetornar201() throws Exception {
        var tipoGuardado = crearTipoSocioEjemplo(1);

        when(tipoSocioService.crearTipoSocio(any(TipoSocioDTO.class))).thenReturn(tipoGuardado);

        String jsonRequestBody = """
                {
                    "tipoSocio": "Estudiante"
                }
                """;

        mockMvc.perform(post("/api/v1/tipos-socio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoSocio").value("Estudiante"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/tipos-socio -> Retorna 400 cuando el nombre esta vacio")
    public void crear_CuandoNombreVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "tipoSocio": ""
                }
                """;

        mockMvc.perform(post("/api/v1/tipos-socio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/tipos-socio/{id} -> Retorna 200 con HATEOAS")
    public void actualizar_DeberiaRetornar200() throws Exception {
        var tipoActualizado = crearTipoSocioEjemplo(1);
        tipoActualizado.setTipoSocio("Profesor");

        when(tipoSocioService.actualizarTipoSocio(anyInt(), any(TipoSocioDTO.class)))
                .thenReturn(tipoActualizado);

        String jsonRequestBody = """
                {
                    "tipoSocio": "Profesor"
                }
                """;

        mockMvc.perform(put("/api/v1/tipos-socio/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoSocio").value("Profesor"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/tipos-socio/{id} -> Retorna 404 si el ID no existe")
    public void actualizar_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(tipoSocioService.actualizarTipoSocio(anyInt(), any(TipoSocioDTO.class)))
                .thenThrow(new ResourceNotFoundException("TipoSocio no encontrado"));

        String jsonRequestBody = """
                {
                    "tipoSocio": "Test"
                }
                """;

        mockMvc.perform(put("/api/v1/tipos-socio/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/tipos-socio/{id} -> Retorna 200 y mensaje de exito")
    public void eliminar_DeberiaRetornar200() throws Exception {
        doNothing().when(tipoSocioService).eliminarTipoSocio(1);

        mockMvc.perform(delete("/api/v1/tipos-socio/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Tipo de socio eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tipos-socio/{id} -> Retorna 404 si el ID no existe")
    public void eliminar_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("TipoSocio no encontrado"))
                .when(tipoSocioService).eliminarTipoSocio(99);

        mockMvc.perform(delete("/api/v1/tipos-socio/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
