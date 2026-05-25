package com.bibliotecaLagos.Socios.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.Socios.DTO.SocioDTO;
import com.bibliotecaLagos.Socios.Model.Socio;
import com.bibliotecaLagos.Socios.Service.SocioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/socios")

public class SocioController {

    @Autowired
    private SocioService socioService;


    @GetMapping
    public ResponseEntity<List<Socio>> listar() {

        List<Socio> socios = socioService.obtenerSocios();

        if(socios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(socios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        Socio socio = socioService.obtenerSocioPorId(id);
        return ResponseEntity.ok(socio);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody SocioDTO dto,BindingResult result) {

        if(result.hasErrors()) {
            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }

        Socio socio = socioService.crearSocio(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(socio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {

        socioService.eliminarSocio(id);

        return ResponseEntity.ok("Socio eliminado correctamente");
    }
}