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

        if (listaLibros.isEmpty()) {

            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(listaLibros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarPorId(@PathVariable Integer id) {

        Libro libro = libroService.buscarPorId(id);
        return ResponseEntity.ok(libro);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Libro> buscarPorIsbn(@PathVariable String isbn) {

        Libro libro = libroService.buscarPorIsbn(isbn);
        return ResponseEntity.ok(libro);
    }

    @PostMapping
    public ResponseEntity<Libro> crearLibro(@Valid @RequestBody LibroDTO dto) {

        Libro libroNuevo = libroService.crearLibro(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(libroNuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Integer id,@Valid @RequestBody LibroDTO dto) {

        Libro libroActualizado = libroService.actualizarLibro(id, dto);
        return ResponseEntity.ok(libroActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarLibro(@PathVariable Integer id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.ok("Libro eliminado correctamente");
    }
}