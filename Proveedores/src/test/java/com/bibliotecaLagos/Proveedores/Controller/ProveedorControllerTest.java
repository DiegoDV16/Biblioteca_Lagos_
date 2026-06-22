package com.bibliotecaLagos.Proveedores.Controller;

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.Proveedores.Assemblers.ProveedorModelAssembler;
import com.bibliotecaLagos.Proveedores.DTO.ProveedorDTO;
import com.bibliotecaLagos.Proveedores.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;
import com.bibliotecaLagos.Proveedores.Service.ProveedorService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ProveedorController.class)
@Import(ProveedorModelAssembler.class)
public class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProveedorService proveedorService;

    private Proveedor crearProveedorEjemplo(Integer id) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre("Proveedor Test");
        proveedor.setTelefono("123456789");
        proveedor.setCorreo("test@correo.com");
        proveedor.setDireccion("Direccion Test");
        proveedor.setEstado("Activo");
        return proveedor;
    }

    @Test
    @DisplayName("GET /api/v1/proveedores -> Retorna 200 con lista HATEOAS")
    public void obtenerProveedores_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var proveedor = crearProveedorEjemplo(1);

        when(proveedorService.obtenerProveedores()).thenReturn(List.of(proveedor));

        mockMvc.perform(get("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.proveedorList.size()").value(1))
                .andExpect(jsonPath("$._embedded.proveedorList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.proveedorList[0].nombre").value("Proveedor Test"))
                .andExpect(jsonPath("$._embedded.proveedorList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/proveedores -> Retorna 204 si no hay proveedores")
    public void obtenerProveedores_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(proveedorService.obtenerProveedores()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/proveedores/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarProveedor() throws Exception {
        var proveedor = crearProveedorEjemplo(1);

        when(proveedorService.buscarPorId(anyInt())).thenReturn(proveedor);

        mockMvc.perform(get("/api/v1/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.proveedores.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/proveedores/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(proveedorService.buscarPorId(99))
                .thenThrow(new ResourceNotFoundException("Proveedor no encontrado"));

        mockMvc.perform(get("/api/v1/proveedores/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/proveedores -> Retorna 201 con HATEOAS")
    public void crearProveedor_DeberiaRetornar201() throws Exception {
        var proveedorGuardado = crearProveedorEjemplo(1);

        when(proveedorService.crearProveedor(any(ProveedorDTO.class))).thenReturn(proveedorGuardado);

        String jsonRequestBody = """
                {
                    "nombre": "Proveedor Test",
                    "telefono": "123456789",
                    "correo": "test@correo.com",
                    "direccion": "Direccion Test",
                    "estado": "Activo"
                }
                """;

        mockMvc.perform(post("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/proveedores -> Retorna 400 cuando el nombre esta vacio")
    public void crearProveedor_CuandoNombreVacio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "nombre": "",
                    "telefono": "123456789",
                    "correo": "test@correo.com",
                    "direccion": "Direccion Test",
                    "estado": "Activo"
                }
                """;

        mockMvc.perform(post("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/proveedores/{id} -> Retorna 200 con HATEOAS")
    public void actualizarProveedor_DeberiaRetornar200() throws Exception {
        var proveedorActualizado = crearProveedorEjemplo(1);
        proveedorActualizado.setNombre("Proveedor Actualizado");

        when(proveedorService.actualizarProveedor(anyInt(), any(ProveedorDTO.class)))
                .thenReturn(proveedorActualizado);

        String jsonRequestBody = """
                {
                    "nombre": "Proveedor Actualizado",
                    "telefono": "123456789",
                    "correo": "test@correo.com",
                    "direccion": "Direccion Test",
                    "estado": "Activo"
                }
                """;

        mockMvc.perform(put("/api/v1/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proveedor Actualizado"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/proveedores/{id} -> Retorna 404 si el ID no existe")
    public void actualizarProveedor_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(proveedorService.actualizarProveedor(anyInt(), any(ProveedorDTO.class)))
                .thenThrow(new ResourceNotFoundException("Proveedor no encontrado"));

        String jsonRequestBody = """
                {
                    "nombre": "Test",
                    "telefono": "123456789",
                    "correo": "test@test.com",
                    "direccion": "Direccion",
                    "estado": "Activo"
                }
                """;

        mockMvc.perform(put("/api/v1/proveedores/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/proveedores/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarProveedor_DeberiaRetornar200() throws Exception {
        doNothing().when(proveedorService).eliminarProveedor(1);

        mockMvc.perform(delete("/api/v1/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Proveedor eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/proveedores/{id} -> Retorna 404 si el ID no existe")
    public void eliminarProveedor_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Proveedor no encontrado"))
                .when(proveedorService).eliminarProveedor(99);

        mockMvc.perform(delete("/api/v1/proveedores/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
