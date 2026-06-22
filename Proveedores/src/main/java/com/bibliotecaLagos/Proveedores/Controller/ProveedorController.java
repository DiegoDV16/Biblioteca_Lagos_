package com.bibliotecaLagos.Proveedores.Controller;

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
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Proveedores.Assemblers.ProveedorModelAssembler;
import com.bibliotecaLagos.Proveedores.DTO.ProveedorDTO;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;
import com.bibliotecaLagos.Proveedores.Service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private static final Logger log = LoggerFactory.getLogger(ProveedorController.class);

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorModelAssembler proveedorModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Proveedor>>> obtenerProveedores() {
        log.info("GET /api/v1/proveedores - listar todos los proveedores");
        List<Proveedor> lista = proveedorService.obtenerProveedores();

        if (lista.isEmpty()) {
            log.info("No se encontraron proveedores");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Proveedor>> proveedoresModel = lista.stream()
                .map(proveedorModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Proveedor>> collectionModel = CollectionModel.of(proveedoresModel,
                linkTo(methodOn(ProveedorController.class).obtenerProveedores()).withSelfRel());

        log.info("Retornando {} proveedores", lista.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/proveedores/{} - buscar proveedor por ID", id);
        try {
            Proveedor proveedor = proveedorService.buscarPorId(id);
            log.info("Proveedor encontrado: ID={}, nombre={}", proveedor.getId(), proveedor.getNombre());
            return ResponseEntity.ok(proveedorModelAssembler.toModel(proveedor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el proveedor con ID: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Proveedor>> crearProveedor(@Valid @RequestBody ProveedorDTO dto) {
        log.info("POST /api/v1/proveedores - crear nuevo proveedor: {}", dto.getCorreo());
        Proveedor proveedorNuevo = proveedorService.crearProveedor(dto);
        log.info("Proveedor creado exitosamente: ID={}", proveedorNuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proveedorModelAssembler.toModel(proveedorNuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Proveedor>> actualizarProveedor(@PathVariable Integer id,
            @Valid @RequestBody ProveedorDTO dto) {
        log.info("PUT /api/v1/proveedores/{} - actualizar proveedor", id);
        Proveedor proveedorActualizado = proveedorService.actualizarProveedor(id, dto);
        log.info("Proveedor actualizado exitosamente: ID={}", proveedorActualizado.getId());
        return ResponseEntity.ok(proveedorModelAssembler.toModel(proveedorActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProveedor(@PathVariable Integer id) {
        log.info("DELETE /api/v1/proveedores/{} - eliminar proveedor", id);
        proveedorService.eliminarProveedor(id);
        log.info("Proveedor ID={} eliminado exitosamente", id);
        return ResponseEntity.ok("Proveedor eliminado correctamente");
    }
}
