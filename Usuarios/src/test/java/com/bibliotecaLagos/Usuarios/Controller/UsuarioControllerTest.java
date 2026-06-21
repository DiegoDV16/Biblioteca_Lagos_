package com.bibliotecaLagos.Usuarios.Controller;

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

import com.bibliotecaLagos.Usuarios.Assemblers.UsuarioModelAssembler;
import com.bibliotecaLagos.Usuarios.DTO.UsuarioDTO;
import com.bibliotecaLagos.Usuarios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Usuarios.Model.Usuario;
import com.bibliotecaLagos.Usuarios.Service.UsuarioService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(UsuarioController.class)
@Import(UsuarioModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private Usuario crearUsuarioEjemplo(Integer id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsuario("jperez");
        usuario.setContrasena("123456");
        usuario.setRolId(1);
        return usuario;
    }

    @Test
    @DisplayName("GET /api/v1/usuarios -> Retorna 200 con lista HATEOAS")
    public void obtenerUsuarios_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var usuario = crearUsuarioEjemplo(1);

        when(usuarioService.obtenerUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.usuarioList.size()").value(1))
                .andExpect(jsonPath("$._embedded.usuarioList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.usuarioList[0].usuario").value("jperez"))
                .andExpect(jsonPath("$._embedded.usuarioList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/usuarios -> Retorna 204 si no hay usuarios")
    public void obtenerUsuarios_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(usuarioService.obtenerUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarUsuario() throws Exception {
        var usuario = crearUsuarioEjemplo(1);

        when(usuarioService.buscarPorId(1)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.usuarios.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(usuarioService.buscarPorId(99))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios -> Retorna 201 con HATEOAS")
    public void crearUsuario_DeberiaRetornar201() throws Exception {
        var usuarioGuardado = crearUsuarioEjemplo(1);

        when(usuarioService.crearUsuario(any(UsuarioDTO.class))).thenReturn(usuarioGuardado);

        String jsonRequestBody = """
                {
                    "usuario": "jperez",
                    "contrasena": "123456",
                    "rolId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios -> Retorna 400 cuando el nombre de usuario esta vacio")
    public void crearUsuario_CuandoUsuarioVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "usuario": "",
                    "contrasena": "123456",
                    "rolId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/usuarios/{id} -> Retorna 200 con HATEOAS")
    public void actualizarUsuario_DeberiaRetornar200() throws Exception {
        var usuarioActualizado = crearUsuarioEjemplo(1);
        usuarioActualizado.setUsuario("carlos");

        when(usuarioService.actualizarUsuario(anyInt(), any(UsuarioDTO.class))).thenReturn(usuarioActualizado);

        String jsonRequestBody = """
                {
                    "usuario": "carlos",
                    "contrasena": "123456",
                    "rolId": 1
                }
                """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("carlos"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarUsuario_DeberiaRetornar200() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1);

        mockMvc.perform(delete("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} -> Retorna 200 aunque el ID no exista")
    public void eliminarUsuario_CuandoNoExiste_DeberiaRetornar200() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(99);

        mockMvc.perform(delete("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
