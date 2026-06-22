package com.bibliotecaLagos.Roles.Controller;

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

import com.bibliotecaLagos.Roles.Assemblers.RolModelAssembler;
import com.bibliotecaLagos.Roles.DTO.RolDTO;
import com.bibliotecaLagos.Roles.Model.Rol;
import com.bibliotecaLagos.Roles.Service.RolService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    private static final Logger log = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @Autowired
    private RolModelAssembler rolModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Rol>>> obtenerRoles() {
        log.info("GET /api/v1/roles - Inicio de consulta de todos los roles");

        List<EntityModel<Rol>> roles = rolService.obtenerRoles()
                .stream()
                .map(rolModelAssembler::toModel)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            log.info("GET /api/v1/roles - No se encontraron roles, respuesta 204");
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<Rol>> collectionModel = CollectionModel.of(roles,
                linkTo(methodOn(RolController.class).obtenerRoles()).withSelfRel());

        log.info("GET /api/v1/roles - Consulta exitosa, {} roles encontrados, respuesta 200", roles.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/roles/{} - Inicio de busqueda por ID", id);

        Optional<Rol> rol = rolService.buscarPorId(id);
        if (rol.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el rol con ID: " + id);
        }
        EntityModel<Rol> model = rolModelAssembler.toModel(rol.get());

        log.info("GET /api/v1/roles/{} - Rol encontrado: nombre={}, respuesta 200", id, rol.get().getNombre());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Rol>> crearRol(@Valid @RequestBody RolDTO dto) {
        log.info("POST /api/v1/roles - Inicio de creacion: nombre={}", dto.getNombre());

        Rol rolNuevo = rolService.crearRol(dto);
        EntityModel<Rol> model = rolModelAssembler.toModel(rolNuevo);

        log.info("POST /api/v1/roles - Rol creado: ID={}, nombre={}, respuesta 200",
                rolNuevo.getId(), rolNuevo.getNombre());
        return ResponseEntity.ok().body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> actualizarRol(
            @PathVariable Integer id,
            @Valid @RequestBody RolDTO dto) {
        log.info("PUT /api/v1/roles/{} - Inicio de actualizacion", id);

        Rol rolActualizado = rolService.actualizarRol(id, dto);
        EntityModel<Rol> model = rolModelAssembler.toModel(rolActualizado);

        log.info("PUT /api/v1/roles/{} - Rol actualizado a: nombre={}, respuesta 200",
                id, rolActualizado.getNombre());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Integer id) {
        log.info("DELETE /api/v1/roles/{} - Inicio de eliminacion", id);

        rolService.eliminarRol(id);

        log.info("DELETE /api/v1/roles/{} - Rol eliminado exitosamente, respuesta 200", id);
        return ResponseEntity.ok("Rol eliminado correctamente");
    }
}