package com.bibliotecaLagos.Usuarios.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuariosDTO {

    @NotBlank
    private String usuario;

    @NotBlank
    private String contrasena;

    @NotNull
    private Integer rolId;
}