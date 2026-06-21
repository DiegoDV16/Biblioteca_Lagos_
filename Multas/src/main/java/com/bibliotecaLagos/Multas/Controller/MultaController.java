package com.bibliotecaLagos.Multas.Controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
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

import com.bibliotecaLagos.Multas.Assemblers.MultaModelAssembler;
import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Service.MultaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/multas")
public class MultaController {

    private static final Logger log = LoggerFactory.getLogger(MultaController.class);

    @Autowired
    private MultaService multaService;

    @Autowired
    private MultaModelAssembler multaModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Multa>>> obtenerMultas() {
        log.info("GET /api/v1/multas - listar todas las multas");
        List<Multa> multas = multaService.obtenerMultas();

        if (multas.isEmpty()) {
            log.info("No se encontraron multas");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Multa>> multasModel = multas.stream()
                .map(multaModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Multa>> collectionModel = CollectionModel.of(multasModel,
                linkTo(methodOn(MultaController.class).obtenerMultas()).withSelfRel());

        log.info("Retornando {} multas", multas.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Multa>> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/multas/{} - buscar multa por ID", id);
        Multa multa = multaService.buscarPorId(id);
        log.info("Multa encontrada: ID={}", multa.getId());
        return ResponseEntity.ok(multaModelAssembler.toModel(multa));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Multa>> crearMulta(@Valid @RequestBody MultaDTO dto) {
        log.info("POST /api/v1/multas - crear nueva multa: prestamoId={}, monto={}", dto.getPrestamoId(), dto.getMonto());
        Multa multa = multaService.crearMulta(dto);
        log.info("Multa creada exitosamente: ID={}", multa.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(multaModelAssembler.toModel(multa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Multa>> actualizarMulta(@PathVariable Integer id, @Valid @RequestBody MultaDTO dto) {
        log.info("PUT /api/v1/multas/{} - actualizar multa", id);
        Multa multa = multaService.actualizarMulta(id, dto);
        log.info("Multa ID={} actualizada exitosamente", id);
        return ResponseEntity.ok(multaModelAssembler.toModel(multa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMulta(@PathVariable Integer id) {
        log.info("DELETE /api/v1/multas/{} - eliminar multa", id);
        multaService.eliminarMulta(id);
        log.info("Multa ID={} eliminada exitosamente", id);
        return ResponseEntity.ok("Multa eliminada correctamente");
    }
}