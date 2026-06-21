package com.bibliotecaLagos.Reserva.Assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bibliotecaLagos.Reserva.Controller.ReservaController;
import com.bibliotecaLagos.Reserva.Model.Reserva;

@Component
public class ReservaModelAssembler implements RepresentationModelAssembler<Reserva, EntityModel<Reserva>> {

    @Override
    public EntityModel<Reserva> toModel(Reserva reserva) {
        return EntityModel.of(reserva,
                linkTo(methodOn(ReservaController.class).buscarPorId(reserva.getId())).withSelfRel(),
                linkTo(methodOn(ReservaController.class).listar()).withRel("reservas"));
    }
}
