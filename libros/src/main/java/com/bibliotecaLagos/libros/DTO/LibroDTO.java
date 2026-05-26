package com.bibliotecaLagos.libros.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LibroDTO {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "La editorial es obligatoria")
    private String editorial;

    @NotNull(message = "El año de publicacion es obligatorio")
    private Integer anioPublicacion;

    @NotNull(message = "La cantidad total es obligatoria")
    @Positive(message = "La cantidad total debe ser mayor a 0")
    private Integer cantidadTotal;

    @NotNull(message = "La categoria es obligatoria")
    private Integer categoriaId;

    @NotNull(message = "El proveedor es obligatorio")
    private Integer proveedorId;
}