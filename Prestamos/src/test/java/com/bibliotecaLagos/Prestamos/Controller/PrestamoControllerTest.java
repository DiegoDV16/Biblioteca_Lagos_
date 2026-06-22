package com.bibliotecaLagos.Prestamos.Controller;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.Prestamos.Assemblers.PrestamoModelAssembler;
import com.bibliotecaLagos.Prestamos.DTO.PrestamoDTO;
import com.bibliotecaLagos.Prestamos.Model.Prestamo;
import com.bibliotecaLagos.Prestamos.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Prestamos.Service.PrestamoService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(PrestamoController.class)
@Import(PrestamoModelAssembler.class)
public class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrestamoService prestamoService;

    private Prestamo crearPrestamoEjemplo(Integer id) {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setSocioId(1);
        prestamo.setLibroId(1);
        prestamo.setFechaPrestamo(LocalDate.of(2026, 6, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2026, 6, 15));
        prestamo.setEstado("Prestado");
        return prestamo;
    }

    @Test
    @DisplayName("GET /api/v1/prestamos -> Retorna 200 con lista HATEOAS")
    public void listar_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var prestamo = crearPrestamoEjemplo(1);

        when(prestamoService.obtenerPrestamos()).thenReturn(List.of(prestamo));

        mockMvc.perform(get("/api/v1/prestamos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.prestamoList.size()").value(1))
                .andExpect(jsonPath("$._embedded.prestamoList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.prestamoList[0].estado").value("Prestado"))
                .andExpect(jsonPath("$._embedded.prestamoList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/prestamos -> Retorna 204 si no hay prestamos")
    public void listar_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(prestamoService.obtenerPrestamos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/prestamos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/prestamos/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarPrestamo() throws Exception {
        var prestamo = crearPrestamoEjemplo(1);

        when(prestamoService.obtenerPrestamoPorId(anyInt())).thenReturn(Optional.of(prestamo));

        mockMvc.perform(get("/api/v1/prestamos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Prestado"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.prestamos.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/prestamos/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(prestamoService.obtenerPrestamoPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/prestamos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/prestamos -> Retorna 201 con HATEOAS")
    public void crear_DeberiaRetornar201() throws Exception {
        var prestamoGuardado = crearPrestamoEjemplo(1);

        when(prestamoService.crearPrestamo(any(PrestamoDTO.class))).thenReturn(prestamoGuardado);

        String jsonRequestBody = """
                {
                    "socioId": 1,
                    "libroId": 1,
                    "fechaPrestamo": "2026-06-01",
                    "fechaDevolucion": "2026-06-15",
                    "estado": "Prestado"
                }
                """;

        mockMvc.perform(post("/api/v1/prestamos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Prestado"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/prestamos -> Retorna 200 cuando el socio falta")
    public void crear_CuandoFaltaSocio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "libroId": 1,
                    "fechaPrestamo": "2026-06-01",
                    "fechaDevolucion": "2026-06-15"
                }
                """;

        mockMvc.perform(post("/api/v1/prestamos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/prestamos/{id} -> Retorna 200 con HATEOAS")
    public void actualizar_DeberiaRetornar200() throws Exception {
        var prestamoActualizado = crearPrestamoEjemplo(1);
        prestamoActualizado.setEstado("Devuelto");

        when(prestamoService.actualizarPrestamo(anyInt(), any(PrestamoDTO.class)))
                .thenReturn(prestamoActualizado);

        String jsonRequestBody = """
                {
                    "socioId": 1,
                    "libroId": 1,
                    "fechaPrestamo": "2026-06-01",
                    "fechaDevolucion": "2026-06-15",
                    "estado": "Devuelto"
                }
                """;

        mockMvc.perform(put("/api/v1/prestamos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Devuelto"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/prestamos/{id} -> Retorna 404 si el ID no existe")
    public void actualizar_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(prestamoService.actualizarPrestamo(anyInt(), any(PrestamoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Prestamo no encontrado"));

        String jsonRequestBody = """
                {
                    "socioId": 1,
                    "libroId": 1,
                    "fechaPrestamo": "2026-06-01",
                    "fechaDevolucion": "2026-06-15",
                    "estado": "Devuelto"
                }
                """;

        mockMvc.perform(put("/api/v1/prestamos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/prestamos/{id} -> Retorna 200 y mensaje de exito")
    public void eliminar_DeberiaRetornar200() throws Exception {
        doNothing().when(prestamoService).eliminarPrestamo(1);

        mockMvc.perform(delete("/api/v1/prestamos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Prestamo eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/prestamos/{id} -> Retorna 404 si el ID no existe")
    public void eliminarPrestamo_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Prestamo no encontrado"))
                .when(prestamoService).eliminarPrestamo(99);

        mockMvc.perform(delete("/api/v1/prestamos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
