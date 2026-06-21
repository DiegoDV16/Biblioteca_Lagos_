package com.bibliotecaLagos.Multas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Service.MultaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/multas")

public class MultaController {

    @Autowired
    private MultaService multaService;

    @GetMapping
    public ResponseEntity<List<Multa>>obtenerMultas() {

        List<Multa> lista = multaService.obtenerMultas();

        if(lista.isEmpty()) {

             return ResponseEntity
             .noContent()
             .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Multa> buscarPorId(@PathVariable Integer id) {

        Multa multa = multaService.buscarPorId(id);
        return ResponseEntity.ok(multa);
    }

    @PostMapping
    public ResponseEntity<Multa>
            crearMulta(@Valid @RequestBody MultaDTO dto) {

        Multa multaNueva = multaService.crearMulta(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(multaNueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Multa>
        actualizarMulta(@PathVariable Integer id, @Valid @RequestBody
        MultaDTO dto) {

        Multa multaActualizada = multaService.actualizarMulta(id, dto);

        return ResponseEntity.ok(
                multaActualizada
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMulta(@PathVariable Integer id) {

        multaService.eliminarMulta(id);

        return ResponseEntity.ok(
                "Multa eliminada correctamente"
        );
    }
}