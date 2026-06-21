package com.bibliotecaLagos.Socios.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Socios.Controller.SocioController;
import com.bibliotecaLagos.Socios.Model.Socio;

@Component
public class SocioModelAssembler implements RepresentationModelAssembler<Socio, EntityModel<Socio>> {

    @Override
    public EntityModel<Socio> toModel(Socio socio) {
        return EntityModel.of(socio,
                linkTo(methodOn(SocioController.class).buscarPorId(socio.getId())).withSelfRel(),
                linkTo(methodOn(SocioController.class).listar()).withRel("socios"));
    }
}
