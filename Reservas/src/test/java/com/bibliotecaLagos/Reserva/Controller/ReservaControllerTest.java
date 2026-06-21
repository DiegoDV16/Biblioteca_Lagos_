package com.bibliotecaLagos.Reserva.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecaLagos.Reserva.Assemblers.ReservaModelAssembler;
import com.bibliotecaLagos.Reserva.DTO.ReservaDTO;
import com.bibliotecaLagos.Reserva.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Reserva.Model.Reserva;
import com.bibliotecaLagos.Reserva.Service.ReservaService;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ReservaController.class)
@Import(ReservaModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    private Reserva crearReservaEjemplo(Integer id) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setSocioId(1);
        reserva.setLibroId(1);
        reserva.setFechaReserva(LocalDate.of(2026, 6, 21));
        reserva.setEstado("Pendiente");
        return reserva;
    }

    @Test
    @DisplayName("GET /api/v1/reservas -> Retorna 200 con lista HATEOAS")
    public void listar_CuandoExisten_DeberiaRetornarLista() throws Exception {
        var reserva = crearReservaEjemplo(1);

        when(reservaService.obtenerReservas()).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.reservaList.size()").value(1))
                .andExpect(jsonPath("$._embedded.reservaList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.reservaList[0]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/reservas -> Retorna 204 si no hay reservas")
    public void listar_CuandoNoExisten_DeberiaRetornarNoContent() throws Exception {
        when(reservaService.obtenerReservas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/reservas/{id} -> Retorna 200 con HATEOAS si el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarReserva() throws Exception {
        var reserva = crearReservaEjemplo(1);

        when(reservaService.obtenerReservaPorId(1)).thenReturn(reserva);

        mockMvc.perform(get("/api/v1/reservas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.reservas.href").exists());
    }

    @Test
    @DisplayName("GET /api/v1/reservas/{id} -> Retorna 404 si el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(reservaService.obtenerReservaPorId(99))
                .thenThrow(new ResourceNotFoundException("Reserva no encontrada"));

        mockMvc.perform(get("/api/v1/reservas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/reservas -> Retorna 201 con HATEOAS")
    public void crear_DeberiaRetornar201() throws Exception {
        var reservaGuardada = crearReservaEjemplo(1);

        when(reservaService.crearReserva(any(ReservaDTO.class))).thenReturn(reservaGuardada);

        String jsonRequestBody = """
                {
                    "socioId": 1,
                    "libroId": 1,
                    "fechaReserva": "2026-06-21",
                    "estado": "Pendiente"
                }
                """;

        mockMvc.perform(post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("POST /api/v1/reservas -> Retorna 400 cuando falta el socio")
    public void crear_CuandoFaltaSocio_DeberiaRetornar400() throws Exception {
        String jsonRequestBody = """
                {
                    "libroId": 1,
                    "fechaReserva": "2026-06-21",
                    "estado": "Pendiente"
                }
                """;

        mockMvc.perform(post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/reservas/{id} -> Retorna 200 y mensaje de exito")
    public void eliminar_DeberiaRetornar200() throws Exception {
        doNothing().when(reservaService).eliminarReserva(1);

        mockMvc.perform(delete("/api/v1/reservas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Reserva eliminada correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/reservas/{id} -> Retorna 200 aunque el ID no exista")
    public void eliminar_CuandoNoExiste_DeberiaRetornar200() throws Exception {
        doNothing().when(reservaService).eliminarReserva(99);

        mockMvc.perform(delete("/api/v1/reservas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
