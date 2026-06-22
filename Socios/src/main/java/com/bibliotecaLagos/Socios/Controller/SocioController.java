package com.bibliotecaLagos.Socios.Controller;

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

import com.bibliotecaLagos.Socios.Assemblers.SocioModelAssembler;
import com.bibliotecaLagos.Socios.DTO.SocioDTO;
import com.bibliotecaLagos.Socios.Model.Socio;
import com.bibliotecaLagos.Socios.Service.SocioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/socios")
public class SocioController {

    private static final Logger log = LoggerFactory.getLogger(SocioController.class);

    @Autowired
    private SocioService socioService;

    @Autowired
    private SocioModelAssembler socioModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Socio>>> listar() {
        log.info("GET /api/v1/socios - listar todos los socios");
        List<Socio> socios = socioService.obtenerSocios();

        if (socios.isEmpty()) {
            log.info("No se encontraron socios");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Socio>> sociosModel = socios.stream()
                .map(socioModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Socio>> collectionModel = CollectionModel.of(sociosModel,
                linkTo(methodOn(SocioController.class).listar()).withSelfRel());

        log.info("Retornando {} socios", socios.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/socios/{} - buscar socio por ID", id);
        Optional<Socio> socio = socioService.obtenerSocioPorId(id);
        if (socio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el socio con ID: " + id);
        }
        log.info("Socio encontrado: ID={}, nombre={}", socio.get().getId(), socio.get().getNombre());
        return ResponseEntity.ok(socioModelAssembler.toModel(socio.get()));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Socio>> crear(@Valid @RequestBody SocioDTO dto) {
        log.info("POST /api/v1/socios - crear nuevo socio: rut={}", dto.getRut());
        Socio socio = socioService.crearSocio(dto);
        log.info("Socio creado exitosamente: ID={}", socio.getId());
        return ResponseEntity.ok()
                .body(socioModelAssembler.toModel(socio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Socio>> actualizar(@PathVariable Integer id, @Valid @RequestBody SocioDTO dto) {
        log.info("PUT /api/v1/socios/{} - actualizar socio", id);
        Socio socio = socioService.actualizarSocio(id, dto);
        log.info("Socio actualizado exitosamente: ID={}", socio.getId());
        return ResponseEntity.ok(socioModelAssembler.toModel(socio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/v1/socios/{} - eliminar socio", id);
        socioService.eliminarSocio(id);
        log.info("Socio ID={} eliminado exitosamente", id);
        return ResponseEntity.ok("Socio eliminado correctamente");
    }
}