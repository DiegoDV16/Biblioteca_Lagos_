package com.bibliotecaLagos.Categorias.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
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

        List<Categoria> categorias = categoriaService.obtenerCategorias();

        if(categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {

        Categoria categoria = categoriaService.buscarPorId(id);

        if(categoria == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Categoría no encontrada");
        }

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaDTO dto, BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getFieldError().getDefaultMessage());
        }

        Categoria categoria = categoriaService.crearCategoria(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody CategoriaDTO dto,
            BindingResult result) {

        if(result.hasErrors()) {

            return ResponseEntity
            .badRequest()
            .body(result.getFieldError().getDefaultMessage());
        }

        Categoria categoria = categoriaService.actualizarCategoria(id, dto);

        if(categoria == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Categoría no encontrada");
        }

        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {

        Categoria categoria = categoriaService.buscarPorId(id);

        if(categoria == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Categoría no encontrada");
        }

        categoriaService.eliminarCategoria(id);
        return ResponseEntity.ok("Categoría eliminada");
    }
}