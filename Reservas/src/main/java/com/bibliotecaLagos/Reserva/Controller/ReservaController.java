package com.bibliotecaLagos.Reserva.Controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecaLagos.Reserva.Assemblers.ReservaModelAssembler;
import com.bibliotecaLagos.Reserva.DTO.ReservaDTO;
import com.bibliotecaLagos.Reserva.Model.Reserva;
import com.bibliotecaLagos.Reserva.Service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaModelAssembler reservaModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Reserva>>> listar() {
        log.info("GET /api/v1/reservas - listar todas las reservas");
        List<Reserva> reservas = reservaService.obtenerReservas();

        if (reservas.isEmpty()) {
            log.info("No se encontraron reservas");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Reserva>> reservasModel = reservas.stream()
                .map(reservaModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Reserva>> collectionModel = CollectionModel.of(reservasModel,
                linkTo(methodOn(ReservaController.class).listar()).withSelfRel());

        log.info("Retornando {} reservas", reservas.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/reservas/{} - buscar reserva por ID", id);
        Optional<Reserva> reservaOpt = reservaService.obtenerReservaPorId(id);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe la reserva con ID: " + id);
        }
        EntityModel<Reserva> model = reservaModelAssembler.toModel(reservaOpt.get());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Reserva>> crear(@Valid @RequestBody ReservaDTO dto) {
        log.info("POST /api/v1/reservas - crear nueva reserva: libroId={}, socioId={}", dto.getLibroId(), dto.getSocioId());
        Reserva reserva = reservaService.crearReserva(dto);
        log.info("Reserva creada exitosamente: ID={}", reserva.getId());
        return ResponseEntity
                .ok()
                .body(reservaModelAssembler.toModel(reserva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Reserva>> actualizar(@PathVariable Integer id, @Valid @RequestBody ReservaDTO dto) {
        log.info("PUT /api/v1/reservas/{} - actualizar reserva", id);
        Reserva reserva = reservaService.actualizarReserva(id, dto);
        log.info("Reserva ID={} actualizada exitosamente", id);
        return ResponseEntity.ok(reservaModelAssembler.toModel(reserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/v1/reservas/{} - eliminar reserva", id);
        reservaService.eliminarReserva(id);
        log.info("Reserva ID={} eliminada exitosamente", id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }
}