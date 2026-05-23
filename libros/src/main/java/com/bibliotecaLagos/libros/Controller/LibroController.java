package com.bibliotecaLagos.libros.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.libros.DTO.LibroDTO;
import com.bibliotecaLagos.libros.Model.Libro;
import com.bibliotecaLagos.libros.Service.LibroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/libros")

public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<List<Libro>> obtenerLibros() {

        List<Libro> listaLibros = libroService.obtenerLibros();

        if(listaLibros.isEmpty()) {

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listaLibros);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        Libro libro = libroService.buscarPorId(id);

        if(libro == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Libro no encontrado");
        }

        return ResponseEntity.ok(libro);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> buscarPorIsbn(@PathVariable String isbn) {

        Libro libro = libroService.buscarPorIsbn(isbn);

        if(libro == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Libro no encontrado");
        }

        return ResponseEntity.ok(libro);
    }

    @PostMapping
    public ResponseEntity<?> crearLibro(
            @Valid @RequestBody LibroDTO dto) {

        try {

            Libro libroNuevo = libroService.crearLibro(dto);

            return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(libroNuevo);

        } catch (Exception e) {

            return ResponseEntity
            .badRequest()
            .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro( @PathVariable Integer id, @Valid @RequestBody LibroDTO dto) {

        Libro libroActualizado = libroService.actualizarLibro(id, dto);

        if(libroActualizado == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Libro no encontrado");
        }

        return ResponseEntity.ok(libroActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Integer id) {

        Libro libro = libroService.buscarPorId(id);

        if(libro == null) {

            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Libro no encontrado");
        }

        libroService.eliminarLibro(id);

        return ResponseEntity.ok("Libro eliminado correctamente");
    }
}