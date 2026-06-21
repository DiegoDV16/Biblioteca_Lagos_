package com.bibliotecaLagos.Roles.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Roles.Controller.RolController;
import com.bibliotecaLagos.Roles.Model.Rol;

@Component
public class RolModelAssembler implements RepresentationModelAssembler<Rol, EntityModel<Rol>> {

    @Override
    public EntityModel<Rol> toModel(Rol entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(RolController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).obtenerRoles()).withRel("roles"));
    }
}
