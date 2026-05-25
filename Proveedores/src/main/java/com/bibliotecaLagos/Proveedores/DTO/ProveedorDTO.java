package com.bibliotecaLagos.Proveedores.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProveedorDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @Email(message = "Correo invalido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
