package com.bibliotecaLagos.libros.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.libros.Controller.LibroController;
import com.bibliotecaLagos.libros.Model.Libro;

@Component
public class LibroModelAssembler implements RepresentationModelAssembler<Libro, EntityModel<Libro>> {

    @Override
    public EntityModel<Libro> toModel(Libro entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(LibroController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(LibroController.class).obtenerLibros()).withRel("libros"));
    }
}
