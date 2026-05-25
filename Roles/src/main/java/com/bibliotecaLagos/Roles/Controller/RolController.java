package com.bibliotecaLagos.Roles.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.Roles.DTO.RolDTO;
import com.bibliotecaLagos.Roles.Model.Rol;
import com.bibliotecaLagos.Roles.Service.RolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")

public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>>obtenerRoles() {

        List<Rol> lista = rolService.obtenerRoles();

        if(lista.isEmpty()) {
        return ResponseEntity
        .noContent()
        .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol>buscarPorId(@PathVariable Integer id) {

        Rol rol = rolService.buscarPorId(id);
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<Rol>
            crearRol(
                    @Valid
                    @RequestBody
                    RolDTO dto
            ) {

        Rol rolNuevo = rolService.crearRol(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(rolNuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol>
            actualizarRol(
                    @PathVariable Integer id,
                    @Valid
                    @RequestBody
                    RolDTO dto
            ) {

        Rol rolActualizado = rolService.actualizarRol(id, dto);

        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Integer id) {
        rolService.eliminarRol(id);
        return ResponseEntity.ok(
                "Rol eliminado correctamente"
        );
    }
}