package com.bibliotecaLagos.Prestamos.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Prestamos.Controller.PrestamoController;
import com.bibliotecaLagos.Prestamos.Model.Prestamo;

@Component
public class PrestamoModelAssembler implements RepresentationModelAssembler<Prestamo, EntityModel<Prestamo>> {

    @Override
    public EntityModel<Prestamo> toModel(Prestamo prestamo) {
        return EntityModel.of(prestamo,
                linkTo(methodOn(PrestamoController.class).buscarPorId(prestamo.getId())).withSelfRel(),
                linkTo(methodOn(PrestamoController.class).listar()).withRel("prestamos"));
    }
}
