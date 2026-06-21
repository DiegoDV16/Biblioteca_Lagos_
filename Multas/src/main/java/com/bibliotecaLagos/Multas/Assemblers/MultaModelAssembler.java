package com.bibliotecaLagos.Multas.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Multas.Controller.MultaController;
import com.bibliotecaLagos.Multas.Model.Multa;

@Component
public class MultaModelAssembler implements RepresentationModelAssembler<Multa, EntityModel<Multa>> {

    @Override
    public EntityModel<Multa> toModel(Multa multa) {
        return EntityModel.of(multa,
                linkTo(methodOn(MultaController.class).buscarPorId(multa.getId())).withSelfRel(),
                linkTo(methodOn(MultaController.class).obtenerMultas()).withRel("multas"));
    }
}
