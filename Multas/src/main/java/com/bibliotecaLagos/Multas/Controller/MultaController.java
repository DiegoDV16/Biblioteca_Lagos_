package com.bibliotecaLagos.Multas.Controller;

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
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Multas.Assemblers.MultaModelAssembler;
import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Service.MultaService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
        log.info("GET /api/v1/multas - Inicio de consulta de todas las multas");

        List<EntityModel<Multa>> multas = multaService.obtenerMultas()
                .stream()
                .map(multaModelAssembler::toModel)
                .collect(Collectors.toList());

        if (multas.isEmpty()) {
            log.info("GET /api/v1/multas - No se encontraron multas, respuesta 204");
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<Multa>> collectionModel = CollectionModel.of(multas,
                linkTo(methodOn(MultaController.class).obtenerMultas()).withSelfRel());

        log.info("GET /api/v1/multas - Consulta exitosa, {} multas encontradas, respuesta 200", multas.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/multas/{} - Inicio de busqueda por ID", id);

        Optional<Multa> multaOpt = multaService.buscarPorId(id);
        if (multaOpt.isEmpty()) {
            log.warn("GET /api/v1/multas/{} - Multa no encontrada, respuesta 404", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe la multa con ID: " + id);
        }

        EntityModel<Multa> model = multaModelAssembler.toModel(multaOpt.get());
        log.info("GET /api/v1/multas/{} - Multa encontrada, respuesta 200", id);
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Multa>> crearMulta(@Valid @RequestBody MultaDTO dto) {
        log.info("POST /api/v1/multas - Inicio de creacion");

        Multa multaNueva = multaService.crearMulta(dto);
        EntityModel<Multa> model = multaModelAssembler.toModel(multaNueva);

        log.info("POST /api/v1/multas - Multa creada: ID={}, respuesta 200", multaNueva.getId());
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Multa>> actualizarMulta(
            @PathVariable Integer id, @Valid @RequestBody MultaDTO dto) {
        log.info("PUT /api/v1/multas/{} - Inicio de actualizacion", id);

        Multa multaActualizada = multaService.actualizarMulta(id, dto);
        EntityModel<Multa> model = multaModelAssembler.toModel(multaActualizada);

        log.info("PUT /api/v1/multas/{} - Multa actualizada, respuesta 200", id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMulta(@PathVariable Integer id) {
        log.info("DELETE /api/v1/multas/{} - Inicio de eliminacion", id);

        multaService.eliminarMulta(id);

        log.info("DELETE /api/v1/multas/{} - Multa eliminada exitosamente, respuesta 200", id);
        return ResponseEntity.ok("Multa eliminada correctamente");
    }
}