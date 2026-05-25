package com.bibliotecaLagos.tipoSocios.DTO;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TipoSocioDTO {

    @NotBlank(message = "El tipo de socio es obligatorio")
    private String tipoSocio;
}