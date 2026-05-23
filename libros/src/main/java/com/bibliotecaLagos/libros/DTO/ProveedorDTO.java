package com.bibliotecaLagos.libros.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProveedorDTO {

    private Integer id;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
}