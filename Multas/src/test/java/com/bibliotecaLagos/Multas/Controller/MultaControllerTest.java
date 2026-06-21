package com.bibliotecaLagos.Multas.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.Multas.Assemblers.MultaModelAssembler;
import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Service.MultaService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(MultaController.class)
@Import(MultaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class MultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MultaService multaService;

    private Multa crearMultaEjemplo(Integer id) {
        Multa multa = new Multa();
        multa.setId(id);
        multa.setPrestamoId(1);
        multa.setMonto(new BigDecimal("50.00"));
        multa.setDiasRetraso(5);
        multa.setPagada(false);
        return multa;
    }

    @Test
    @DisplayName("GET /api/v1/multas -> Retorna 200 con lista HATEOAS")
    public void obtenerMultas_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var multa = crearMultaEjemplo(1);

        when(multaService.obtenerMultas()).thenReturn(List.of(multa));

        mockMvc.perform(get("/api/v1/multas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.multaList.size()").value(1))
                .andExpect(jsonPath("$._embedded.multaList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.multaList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/multas -> Retorna 204 si no hay multas")
    public void obtenerMultas_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(multaService.obtenerMultas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/multas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/multas/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarMulta() throws Exception {
        var multa = crearMultaEjemplo(1);

        when(multaService.buscarPorId(1)).thenReturn(multa);

        mockMvc.perform(get("/api/v1/multas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.multas.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/multas/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(multaService.buscarPorId(99))
                .thenThrow(new ResourceNotFoundException("Multa no encontrada"));

        mockMvc.perform(get("/api/v1/multas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/multas -> Retorna 201 con HATEOAS")
    public void crearMulta_DeberiaRetornar201() throws Exception {
        var multaGuardada = crearMultaEjemplo(1);

        when(multaService.crearMulta(any(MultaDTO.class))).thenReturn(multaGuardada);

        String jsonRequestBody = """
                {
                    "prestamoId": 1,
                    "monto": 50.00,
                    "diasRetraso": 5,
                    "pagada": false
                }
                """;

        mockMvc.perform(post("/api/v1/multas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/multas -> Retorna 400 cuando falta el prestamo")
    public void crearMulta_CuandoFaltaPrestamo_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "monto": 50.00,
                    "diasRetraso": 5
                }
                """;

        mockMvc.perform(post("/api/v1/multas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/multas/{id} -> Retorna 200 con HATEOAS")
    public void actualizarMulta_DeberiaRetornar200() throws Exception {
        var multaActualizada = crearMultaEjemplo(1);
        multaActualizada.setPagada(true);

        when(multaService.actualizarMulta(any(Integer.class), any(MultaDTO.class))).thenReturn(multaActualizada);

        String jsonRequestBody = """
                {
                    "prestamoId": 1,
                    "monto": 50.00,
                    "diasRetraso": 5,
                    "pagada": true
                }
                """;

        mockMvc.perform(put("/api/v1/multas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/multas/{id} -> Retorna 200 y mensaje de exito")
    public void eliminarMulta_DeberiaRetornar200() throws Exception {
        doNothing().when(multaService).eliminarMulta(1);

        mockMvc.perform(delete("/api/v1/multas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Multa eliminada correctamente"));
    }
}
