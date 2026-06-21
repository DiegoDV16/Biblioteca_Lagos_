package com.bibliotecaLagos.tipoSocios.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.tipoSocios.Assemblers.TipoSocioModelAssembler;
import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;
import com.bibliotecaLagos.tipoSocios.Service.TipoSocioService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/tipos-socio")
public class TipoSocioController {

    private static final Logger log = LoggerFactory.getLogger(TipoSocioController.class);

    @Autowired
    private TipoSocioService tipoSocioService;

    @Autowired
    private TipoSocioModelAssembler tipoSocioModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<TipoSocio>>> listar() {
        log.info("GET /api/v1/tipos-socio - Inicio de consulta de todos los tipos de socio");

        List<EntityModel<TipoSocio>> tipos = tipoSocioService.obtenerTiposSocio()
                .stream()
                .map(tipoSocioModelAssembler::toModel)
                .collect(Collectors.toList());

        if (tipos.isEmpty()) {
            log.info("GET /api/v1/tipos-socio - No se encontraron tipos de socio, respuesta 204");
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<TipoSocio>> collectionModel = CollectionModel.of(tipos,
                linkTo(methodOn(TipoSocioController.class).listar()).withSelfRel());

        log.info("GET /api/v1/tipos-socio - Consulta exitosa, {} tipos de socio encontrados, respuesta 200", tipos.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TipoSocio>> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/tipos-socio/{} - Inicio de busqueda por ID", id);

        TipoSocio tipo = tipoSocioService.obtenerTipoSocioPorId(id);
        EntityModel<TipoSocio> model = tipoSocioModelAssembler.toModel(tipo);

        log.info("GET /api/v1/tipos-socio/{} - Tipo de socio encontrado: tipoSocio={}, respuesta 200", id, tipo.getTipoSocio());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<TipoSocio>> crear(@Valid @RequestBody TipoSocioDTO dto) {
        log.info("POST /api/v1/tipos-socio - Inicio de creacion: tipoSocio={}", dto.getTipoSocio());

        TipoSocio tipoNuevo = tipoSocioService.crearTipoSocio(dto);
        EntityModel<TipoSocio> model = tipoSocioModelAssembler.toModel(tipoNuevo);

        log.info("POST /api/v1/tipos-socio - Tipo de socio creado: ID={}, tipoSocio={}, respuesta 201",
                tipoNuevo.getId(), tipoNuevo.getTipoSocio());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TipoSocio>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TipoSocioDTO dto) {
        log.info("PUT /api/v1/tipos-socio/{} - Inicio de actualizacion", id);

        TipoSocio tipoActualizado = tipoSocioService.actualizarTipoSocio(id, dto);
        EntityModel<TipoSocio> model = tipoSocioModelAssembler.toModel(tipoActualizado);

        log.info("PUT /api/v1/tipos-socio/{} - Tipo de socio actualizado a: tipoSocio={}, respuesta 200",
                id, tipoActualizado.getTipoSocio());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/v1/tipos-socio/{} - Inicio de eliminacion", id);

        tipoSocioService.eliminarTipoSocio(id);

        log.info("DELETE /api/v1/tipos-socio/{} - Tipo de socio eliminado exitosamente, respuesta 200", id);
        return ResponseEntity.ok("Tipo de socio eliminado correctamente");
    }
}