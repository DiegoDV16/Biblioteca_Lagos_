package com.bibliotecaLagos.Usuarios.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Usuarios.Controller.UsuarioController;
import com.bibliotecaLagos.Usuarios.Model.Usuario;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscarPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios"));
    }
}
