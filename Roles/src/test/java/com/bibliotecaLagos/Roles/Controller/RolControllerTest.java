package com.bibliotecaLagos.Roles.Controller;

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

import com.bibliotecaLagos.Roles.Assemblers.RolModelAssembler;
import com.bibliotecaLagos.Roles.DTO.RolDTO;
import com.bibliotecaLagos.Roles.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Roles.Model.Rol;
import com.bibliotecaLagos.Roles.Service.RolService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(RolController.class)
@Import(RolModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService rolService;

    private Rol crearRolEjemplo(Integer id) {
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("Admin");
        return rol;
    }

    @Test
    @DisplayName("GET /api/v1/roles -> Retorna 200 con lista HATEOAS")
    public void obtenerRoles_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var rol = crearRolEjemplo(1);

        when(rolService.obtenerRoles()).thenReturn(List.of(rol));

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.rolList.size()").value(1))
                .andExpect(jsonPath("$._embedded.rolList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.rolList[0].nombre").value("Admin"))
                .andExpect(jsonPath("$._embedded.rolList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/roles -> Retorna 204 si no hay roles")
    public void obtenerRoles_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(rolService.obtenerRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/roles/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarRol() throws Exception {
        var rol = crearRolEjemplo(1);

        when(rolService.buscarPorId(1)).thenReturn(rol);

        mockMvc.perform(get("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Admin"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.roles.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/roles/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(rolService.buscarPorId(99))
                .thenThrow(new ResourceNotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/api/v1/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rol no encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/roles -> Retorna 201 con HATEOAS")
    public void crearRol_DeberiaRetornar201() throws Exception {
        var rolGuardado = crearRolEjemplo(1);

        when(rolService.crearRol(any(RolDTO.class))).thenReturn(rolGuardado);

        String jsonRequestBody = """
                {
                    "nombre": "Admin"
                }
                """;

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Admin"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/roles -> Retorna 400 cuando el nombre esta vacio")
    public void crearRol_CuandoNombreVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "nombre": ""
                }
                """;

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/roles/{id} -> Retorna 200 con HATEOAS")
    public void actualizarRol_DeberiaRetornar200() throws Exception {
        var rolActualizado = crearRolEjemplo(1);
        rolActualizado.setNombre("Usuario");

        when(rolService.actualizarRol(anyInt(), any(RolDTO.class)))
                .thenReturn(rolActualizado);

        String jsonRequestBody = """
                {
                    "nombre": "Usuario"
                }
                """;

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Usuario"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/roles/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarRol_DeberiaRetornar200() throws Exception {
        doNothing().when(rolService).eliminarRol(1);

        mockMvc.perform(delete("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Rol eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/roles/{id} -> Retorna 200 aunque el ID no exista")
    public void eliminarRol_CuandoNoExiste_DeberiaRetornar200() throws Exception {
        doNothing().when(rolService).eliminarRol(99);

        mockMvc.perform(delete("/api/v1/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
