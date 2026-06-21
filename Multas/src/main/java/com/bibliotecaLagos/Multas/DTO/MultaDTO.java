package com.bibliotecaLagos.Multas.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MultaDTO {

    @NotNull(message = "El prestamo es obligatorio")
    private Integer prestamoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
    message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "Los dias de retraso son obligatorios")
    @PositiveOrZero(message = "Los dias no pueden ser negativos")
    private Integer diasRetraso;

    private Boolean pagada;
}