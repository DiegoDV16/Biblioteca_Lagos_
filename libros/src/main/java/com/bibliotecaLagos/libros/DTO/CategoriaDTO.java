package com.bibliotecaLagos.libros.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CategoriaDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
}