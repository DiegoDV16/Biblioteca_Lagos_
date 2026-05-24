package com.bibliotecaLagos.Prestamos.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PrestamoDTO {

    @NotNull(message = "El socio es obligatorio")
    private Integer socioId;

    @NotNull(message = "El libro es obligatorio")
    private Integer libroId;

    @NotNull(message = "La fecha de prestamo es obligatoria")
    private LocalDate fechaPrestamo;

    @NotNull(message = "La fecha de devolucion es obligatoria")
    private LocalDate fechaDevolucion;

    private LocalDate fechaEntrega;

    private String estado;
}