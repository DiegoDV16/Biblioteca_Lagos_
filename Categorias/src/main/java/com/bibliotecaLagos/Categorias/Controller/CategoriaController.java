package com.bibliotecaLagos.Categorias.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Categorias.DTO.CategoriaDTO;
import com.bibliotecaLagos.Categorias.Model.Categoria;
import com.bibliotecaLagos.Categorias.Service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categorias")

public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerCategorias() {

        List<Categoria> lista = categoriaService.obtenerCategorias();

        if(lista.isEmpty()) {

            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria>buscarPorId(@PathVariable Integer id) {

        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<Categoria> crearCategoria(
                    @Valid
                    @RequestBody
                    CategoriaDTO dto
            ) {

        Categoria categoriaNueva = categoriaService.crearCategoria(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(categoriaNueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria>
        actualizarCategoria(
                @PathVariable Integer id,

                @Valid
                @RequestBody
                CategoriaDTO dto
        ) {

        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, dto);

        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
        eliminarCategoria(@PathVariable Integer id) {

        categoriaService.eliminarCategoria(id);

        return ResponseEntity.ok(
            "Categoria eliminada correctamente"
        );
    }
}