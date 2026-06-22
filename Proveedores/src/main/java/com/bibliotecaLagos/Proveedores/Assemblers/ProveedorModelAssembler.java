package com.bibliotecaLagos.Proveedores.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Proveedores.Controller.ProveedorController;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;

@Component
public class ProveedorModelAssembler implements RepresentationModelAssembler<Proveedor, EntityModel<Proveedor>> {

    @Override
    public EntityModel<Proveedor> toModel(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(ProveedorController.class).buscarPorId(proveedor.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).obtenerProveedores()).withRel("proveedores"));
    }
}
