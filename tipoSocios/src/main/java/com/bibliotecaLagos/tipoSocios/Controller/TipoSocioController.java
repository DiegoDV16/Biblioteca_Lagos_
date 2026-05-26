package com.bibliotecaLagos.tipoSocios.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;
import com.bibliotecaLagos.tipoSocios.Service.TipoSocioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tipos-socio")

public class TipoSocioController {

    @Autowired
    private TipoSocioService tipoSocioService;

    @GetMapping
    public ResponseEntity<List<TipoSocio>> listar() {

        List<TipoSocio> tipos = tipoSocioService.obtenerTiposSocio();

        if(tipos.isEmpty()) {
            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(tipos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        TipoSocio tipo = tipoSocioService.obtenerTipoSocioPorId(id);
        return ResponseEntity.ok(tipo);
    }
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TipoSocioDTO dto,
            BindingResult result) {

        if(result.hasErrors()) {
            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }
        TipoSocio tipo = tipoSocioService.crearTipoSocio(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(tipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        tipoSocioService.eliminarTipoSocio(id);

        return ResponseEntity.ok("Tipo de socio eliminado correctamente");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
            @Valid @RequestBody TipoSocioDTO dto,
            BindingResult result) {

        if(result.hasErrors()) {
            return ResponseEntity
            .badRequest()
            .body(result.getAllErrors());
        }
        TipoSocio tipo = tipoSocioService.actualizarTipoSocio(id, dto);

        return ResponseEntity.ok(tipo);
    }
}