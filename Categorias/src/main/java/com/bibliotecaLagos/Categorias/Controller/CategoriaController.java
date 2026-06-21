package com.bibliotecaLagos.Categorias.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotecaLagos.Categorias.Assemblers.CategoriaModelAssembler;
import com.bibliotecaLagos.Categorias.DTO.CategoriaDTO;
import com.bibliotecaLagos.Categorias.Model.Categoria;
import com.bibliotecaLagos.Categorias.Service.CategoriaService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaModelAssembler categoriaModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Categoria>>> obtenerCategorias() {
        log.info("GET /api/v1/categorias - Inicio de consulta de todas las categorias");

        List<EntityModel<Categoria>> categorias = categoriaService.obtenerCategorias()
                .stream()
                .map(categoriaModelAssembler::toModel)
                .collect(Collectors.toList());

        if (categorias.isEmpty()) {
            log.info("GET /api/v1/categorias - No se encontraron categorias, respuesta 204");
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<Categoria>> collectionModel = CollectionModel.of(categorias,
                linkTo(methodOn(CategoriaController.class).obtenerCategorias()).withSelfRel());

        log.info("GET /api/v1/categorias - Consulta exitosa, {} categorias encontradas, respuesta 200", categorias.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/categorias/{} - Inicio de busqueda por ID", id);

        Categoria categoria = categoriaService.buscarPorId(id);
        EntityModel<Categoria> model = categoriaModelAssembler.toModel(categoria);

        log.info("GET /api/v1/categorias/{} - Categoria encontrada: {}, respuesta 200", id, categoria.getNombre());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Categoria>> crearCategoria(
            @Valid @RequestBody CategoriaDTO dto) {
        log.info("POST /api/v1/categorias - Inicio de creacion: nombre={}", dto.getNombre());

        Categoria categoriaNueva = categoriaService.crearCategoria(dto);
        EntityModel<Categoria> model = categoriaModelAssembler.toModel(categoriaNueva);

        log.info("POST /api/v1/categorias - Categoria creada: ID={}, nombre={}, respuesta 201",
                categoriaNueva.getId(), categoriaNueva.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaDTO dto) {
        log.info("PUT /api/v1/categorias/{} - Inicio de actualizacion", id);

        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, dto);
        EntityModel<Categoria> model = categoriaModelAssembler.toModel(categoriaActualizada);

        log.info("PUT /api/v1/categorias/{} - Categoria actualizada a: {}, respuesta 200",
                id, categoriaActualizada.getNombre());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCategoria(@PathVariable Integer id) {
        log.info("DELETE /api/v1/categorias/{} - Inicio de eliminacion", id);

        categoriaService.eliminarCategoria(id);

        log.info("DELETE /api/v1/categorias/{} - Categoria eliminada exitosamente, respuesta 200", id);
        return ResponseEntity.ok("Categoria eliminada correctamente");
    }
}