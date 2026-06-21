package com.bibliotecaLagos.Categorias.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Categorias.Controller.CategoriaController;
import com.bibliotecaLagos.Categorias.Model.Categoria;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public EntityModel<Categoria> toModel(Categoria entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(CategoriaController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).obtenerCategorias()).withRel("categorias"));
    }
}
