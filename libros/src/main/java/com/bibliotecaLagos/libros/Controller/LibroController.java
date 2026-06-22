package com.bibliotecaLagos.libros.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.libros.Assemblers.LibroModelAssembler;
import com.bibliotecaLagos.libros.DTO.LibroDTO;
import com.bibliotecaLagos.libros.Model.Libro;
import com.bibliotecaLagos.libros.Service.LibroService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    private static final Logger log = LoggerFactory.getLogger(LibroController.class);

    @Autowired
    private LibroService libroService;

    @Autowired
    private LibroModelAssembler libroModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Libro>>> obtenerLibros() {
        log.info("GET /api/v1/libros - Inicio de consulta de todos los libros");

        List<EntityModel<Libro>> libros = libroService.obtenerLibros()
                .stream()
                .map(libroModelAssembler::toModel)
                .collect(Collectors.toList());

        if (libros.isEmpty()) {
            log.info("GET /api/v1/libros - No se encontraron libros, respuesta 204");
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<Libro>> collectionModel = CollectionModel.of(libros,
                linkTo(methodOn(LibroController.class).obtenerLibros()).withSelfRel());

        log.info("GET /api/v1/libros - Consulta exitosa, {} libros encontrados, respuesta 200", libros.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/libros/{} - Inicio de busqueda por ID", id);

        Optional<Libro> libro = libroService.buscarPorId(id);
        if (libro.isEmpty()) {
            log.warn("GET /api/v1/libros/{} - Libro no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el libro con ID: " + id);
        }
        EntityModel<Libro> model = libroModelAssembler.toModel(libro.get());

        log.info("GET /api/v1/libros/{} - Libro encontrado: ISBN={}, respuesta 200", id, libro.get().getIsbn());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<EntityModel<Libro>> buscarPorIsbn(@PathVariable String isbn) {
        log.info("GET /api/v1/libros/isbn/{} - Inicio de busqueda por ISBN", isbn);

        Libro libro = libroService.buscarPorIsbn(isbn);
        EntityModel<Libro> model = libroModelAssembler.toModel(libro);

        log.info("GET /api/v1/libros/isbn/{} - Libro encontrado: ID={}, respuesta 200", isbn, libro.getId());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Libro>> crearLibro(@Valid @RequestBody LibroDTO dto) {
        log.info("POST /api/v1/libros - Inicio de creacion: ISBN={}, titulo={}", dto.getIsbn(), dto.getTitulo());

        Libro libroNuevo = libroService.crearLibro(dto);
        EntityModel<Libro> model = libroModelAssembler.toModel(libroNuevo);

        log.info("POST /api/v1/libros - Libro creado: ID={}, ISBN={}, respuesta 200",
                libroNuevo.getId(), libroNuevo.getIsbn());
        return ResponseEntity.ok().body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Libro>> actualizarLibro(
            @PathVariable Integer id,
            @Valid @RequestBody LibroDTO dto) {
        log.info("PUT /api/v1/libros/{} - Inicio de actualizacion", id);

        Libro libroActualizado = libroService.actualizarLibro(id, dto);
        EntityModel<Libro> model = libroModelAssembler.toModel(libroActualizado);

        log.info("PUT /api/v1/libros/{} - Libro actualizado a: ISBN={}, respuesta 200",
                id, libroActualizado.getIsbn());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarLibro(@PathVariable Integer id) {
        log.info("DELETE /api/v1/libros/{} - Inicio de eliminacion", id);

        libroService.eliminarLibro(id);

        log.info("DELETE /api/v1/libros/{} - Libro eliminado exitosamente, respuesta 200", id);
        return ResponseEntity.ok("Libro eliminado correctamente");
    }
}
