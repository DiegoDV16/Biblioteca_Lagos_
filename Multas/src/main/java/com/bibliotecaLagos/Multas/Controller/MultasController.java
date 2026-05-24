package com.bibliotecaLagos.Multas.Controller;

import com.bibliotecaLagos.Multas.Service.MultasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Multas.Model.Multas;

import java.util.List;

@RestController
@RequestMapping("/api/multas")
@RequiredArgsConstructor
public class MultasController {

    private final MultasService multasService;

    @PostMapping("/generar/{prestamoId}")
    public Multas generar(@PathVariable Integer prestamoId) {
        return multasService.generarMulta(prestamoId);
    }

    @GetMapping
    public List<Multas> listar() {
        return multasService.listar();
    }

    @PutMapping("/pagar/{id}")
    public Multas pagar(@PathVariable Integer id) {
        return multasService.pagar(id);
    }
}