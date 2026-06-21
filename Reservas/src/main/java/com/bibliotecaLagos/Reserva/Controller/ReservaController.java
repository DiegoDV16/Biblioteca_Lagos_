package com.bibliotecaLagos.Reserva.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.Reserva.DTO.ReservaDTO;
import com.bibliotecaLagos.Reserva.Model.Reserva;
import com.bibliotecaLagos.Reserva.Service.ReservaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservas")

public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {

        List<Reserva> reservas = reservaService.obtenerReservas();

        if(reservas.isEmpty()) {

            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        Reserva reserva = reservaService.obtenerReservaPorId(id);

        return ResponseEntity.ok(reserva);
    }
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaDTO dto,
            BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }

        Reserva reserva = reservaService.crearReserva(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(reserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {

        reservaService.eliminarReserva(id);

        return ResponseEntity.ok(
             "Reserva eliminada correctamente"
        );
    }
}