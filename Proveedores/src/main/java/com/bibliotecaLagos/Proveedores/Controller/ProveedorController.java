package com.bibliotecaLagos.Proveedores.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Proveedores.DTO.ProveedorDTO;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;
import com.bibliotecaLagos.Proveedores.Service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proveedores")

public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {

        List<Proveedor> proveedores = proveedorService.obtenerProveedores();

        if(proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);

        if(proveedor == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Proveedor no encontrado");
        }

        return ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProveedorDTO dto, BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }

        Proveedor proveedor = proveedorService.crearProveedor(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorDTO dto,
        BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }

        Proveedor proveedor = proveedorService.actualizarProveedor(id, dto);

        if(proveedor == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Proveedor no encontrado");
        }

        return ResponseEntity.ok(proveedor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {

        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);

        if(proveedor == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Proveedor no encontrado");
        }

        proveedorService.eliminarProveedor(id);

        return ResponseEntity.ok("Proveedor eliminado correctamente");
    }
}