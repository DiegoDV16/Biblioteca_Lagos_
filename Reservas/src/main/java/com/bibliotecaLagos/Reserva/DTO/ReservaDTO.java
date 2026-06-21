package com.bibliotecaLagos.Reserva.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReservaDTO {

    @NotNull(message = "El socio es obligatorio")
    private Integer socioId;

    @NotNull(message = "El libro es obligatorio")
    private Integer libroId;

    @NotNull(message = "La fecha de reserva es obligatoria")
    private LocalDate fechaReserva;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}