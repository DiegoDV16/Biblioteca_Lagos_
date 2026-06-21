package com.bibliotecaLagos.Socios.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.bibliotecaLagos.Socios.Assemblers.SocioModelAssembler;
import com.bibliotecaLagos.Socios.DTO.SocioDTO;
import com.bibliotecaLagos.Socios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Socios.Model.Socio;
import com.bibliotecaLagos.Socios.Service.SocioService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(SocioController.class)
@Import(SocioModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class SocioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    @DisplayName("GET /api/v1/socios -> Retorna 200 con lista HATEOAS")
    public void listar_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var socio = crearSocioEjemplo(1);

        when(socioService.obtenerSocios()).thenReturn(List.of(socio));

        mockMvc.perform(get("/api/v1/socios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.socioList.size()").value(1))
                .andExpect(jsonPath("$._embedded.socioList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.socioList[0].nombre").value("Juan"))
                .andExpect(jsonPath("$._embedded.socioList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/socios -> Retorna 204 si no hay socios")
    public void listar_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(socioService.obtenerSocios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/socios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/socios/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarSocio() throws Exception {
        var socio = crearSocioEjemplo(1);

        when(socioService.obtenerSocioPorId(1)).thenReturn(socio);

        mockMvc.perform(get("/api/v1/socios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.socios.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/socios/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(socioService.obtenerSocioPorId(99))
                .thenThrow(new ResourceNotFoundException("Socio no encontrado"));

        mockMvc.perform(get("/api/v1/socios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/socios -> Retorna 201 con HATEOAS")
    public void crear_DeberiaRetornar201() throws Exception {
        var socioGuardado = crearSocioEjemplo(1);

        when(socioService.crearSocio(any(SocioDTO.class))).thenReturn(socioGuardado);

        String jsonRequestBody = """
                {
                    "nombre": "Juan",
                    "apellido": "Perez",
                    "rut": "12345678-9",
                    "correo": "juan@correo.com",
                    "telefono": "987654321",
                    "idTipoSocio": 1
                }
                """;

        mockMvc.perform(post("/api/v1/socios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/socios -> Retorna 400 cuando el nombre esta vacio")
    public void crear_CuandoNombreVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "nombre": "",
                    "apellido": "Perez",
                    "rut": "12345678-9",
                    "correo": "juan@correo.com",
                    "idTipoSocio": 1
                }
                """;

        mockMvc.perform(post("/api/v1/socios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/socios/{id} -> Retorna 200 con HATEOAS")
    public void actualizar_DeberiaRetornar200() throws Exception {
        var socioActualizado = crearSocioEjemplo(1);
        socioActualizado.setNombre("Carlos");

        when(socioService.actualizarSocio(anyInt(), any(SocioDTO.class))).thenReturn(socioActualizado);

        String jsonRequestBody = """
                {
                    "nombre": "Carlos",
                    "apellido": "Perez",
                    "rut": "12345678-9",
                    "correo": "juan@correo.com",
                    "telefono": "987654321",
                    "idTipoSocio": 1
                }
                """;

        mockMvc.perform(put("/api/v1/socios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/socios/{id} -> Retorna 200 y mensaje de exito")
    public void eliminar_DeberiaRetornar200() throws Exception {
        doNothing().when(socioService).eliminarSocio(1);

        mockMvc.perform(delete("/api/v1/socios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Socio eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/socios/{id} -> Retorna 200 aunque el ID no exista")
    public void eliminar_CuandoNoExiste_DeberiaRetornar200() throws Exception {
        doNothing().when(socioService).eliminarSocio(99);

        mockMvc.perform(delete("/api/v1/socios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
