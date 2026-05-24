package com.bibliotecaLagos.Prestamos.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Prestamos.DTO.PrestamoDTO;

import com.bibliotecaLagos.Prestamos.Model.Prestamo;

import com.bibliotecaLagos.Prestamos.Service.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/prestamos")

public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;
    @GetMapping
    public ResponseEntity<List<Prestamo>> listar() {

        List<Prestamo> prestamos =
        prestamoService.obtenerPrestamos();

        if(prestamos.isEmpty()) {

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);

        return ResponseEntity.ok(prestamo);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PrestamoDTO dto, BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }

        Prestamo prestamo = prestamoService.crearPrestamo(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(prestamo);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {

        prestamoService.eliminarPrestamo(id);

        return ResponseEntity.ok(
                "Prestamo eliminado correctamente"
        );
    }
}