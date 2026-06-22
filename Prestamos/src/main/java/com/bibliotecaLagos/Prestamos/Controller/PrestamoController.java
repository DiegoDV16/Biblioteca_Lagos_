package com.bibliotecaLagos.Prestamos.Controller;

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

import com.bibliotecaLagos.Prestamos.Assemblers.PrestamoModelAssembler;
import com.bibliotecaLagos.Prestamos.DTO.PrestamoDTO;
import com.bibliotecaLagos.Prestamos.Model.Prestamo;
import com.bibliotecaLagos.Prestamos.Service.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private static final Logger log = LoggerFactory.getLogger(PrestamoController.class);

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoModelAssembler prestamoModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Prestamo>>> listar() {
        log.info("GET /api/v1/prestamos - listar todos los prestamos");
        List<Prestamo> prestamos = prestamoService.obtenerPrestamos();

        if (prestamos.isEmpty()) {
            log.info("No se encontraron prestamos");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Prestamo>> prestamosModel = prestamos.stream()
                .map(prestamoModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Prestamo>> collectionModel = CollectionModel.of(prestamosModel,
                linkTo(methodOn(PrestamoController.class).listar()).withSelfRel());

        log.info("Retornando {} prestamos", prestamos.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/prestamos/{} - buscar prestamo por ID", id);
        Optional<Prestamo> prestamoOpt = prestamoService.obtenerPrestamoPorId(id);
        if (prestamoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el prestamo con ID: " + id);
        }
        EntityModel<Prestamo> model = prestamoModelAssembler.toModel(prestamoOpt.get());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Prestamo>> crear(@Valid @RequestBody PrestamoDTO dto) {
        log.info("POST /api/v1/prestamos - crear nuevo prestamo: libroId={}, socioId={}", dto.getLibroId(), dto.getSocioId());
        Prestamo prestamo = prestamoService.crearPrestamo(dto);
        log.info("Prestamo creado exitosamente: ID={}", prestamo.getId());
        return ResponseEntity
                .ok()
                .body(prestamoModelAssembler.toModel(prestamo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Prestamo>> actualizar(@PathVariable Integer id, @Valid @RequestBody PrestamoDTO dto) {
        log.info("PUT /api/v1/prestamos/{} - actualizar prestamo", id);
        Prestamo prestamo = prestamoService.actualizarPrestamo(id, dto);
        log.info("Prestamo ID={} actualizado exitosamente", id);
        return ResponseEntity.ok(prestamoModelAssembler.toModel(prestamo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/v1/prestamos/{} - eliminar prestamo", id);
        prestamoService.eliminarPrestamo(id);
        log.info("Prestamo ID={} eliminado exitosamente", id);
        return ResponseEntity.ok("Prestamo eliminado correctamente");
    }
}

