package com.bibliotecaLagos.Roles.DTO;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RolDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}