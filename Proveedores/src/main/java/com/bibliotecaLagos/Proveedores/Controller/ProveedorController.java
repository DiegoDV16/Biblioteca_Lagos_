package com.bibliotecaLagos.Proveedores.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<List<Proveedor>> obtenerProveedores() {

        List<Proveedor> lista = proveedorService.obtenerProveedores();

        if(lista.isEmpty()) {

            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> buscarPorId(@PathVariable Integer id) {

        Proveedor proveedor = proveedorService.buscarPorId(id);

        return ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<Proveedor>
            crearProveedor(
                    @Valid
                    @RequestBody
                    ProveedorDTO dto
            ) {

        Proveedor proveedorNuevo = proveedorService.crearProveedor(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(proveedorNuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor>
            actualizarProveedor(
                    @PathVariable Integer id,

                    @Valid
                    @RequestBody
                    ProveedorDTO dto
            ) {

        Proveedor proveedorActualizado = proveedorService.actualizarProveedor(id, dto);

        return ResponseEntity.ok(proveedorActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProveedor(@PathVariable Integer id) {

        proveedorService.eliminarProveedor(id);

        return ResponseEntity.ok(
                "Proveedor eliminado correctamente"
        );
    }
}