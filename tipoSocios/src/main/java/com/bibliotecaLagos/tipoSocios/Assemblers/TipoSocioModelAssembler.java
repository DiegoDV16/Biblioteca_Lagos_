package com.bibliotecaLagos.tipoSocios.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.tipoSocios.Controller.TipoSocioController;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;

@Component
public class TipoSocioModelAssembler implements RepresentationModelAssembler<TipoSocio, EntityModel<TipoSocio>> {

    @Override
    public EntityModel<TipoSocio> toModel(TipoSocio entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TipoSocioController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(TipoSocioController.class).listar()).withRel("tipos-socio"));
    }
}
