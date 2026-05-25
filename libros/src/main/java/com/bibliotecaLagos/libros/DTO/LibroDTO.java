package com.bibliotecaLagos.libros.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LibroDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    @NotBlank(message = "El autor es obligatorio")
    private String autor;
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;
    @NotBlank(message = "La editorial es obligatoria")
    private String editorial;
    @NotNull(message = "El año es obligatorio")
    private Integer anioPublicacion;
    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;
    @NotNull(message = "La cantidad total es obligatoria")
    @Min(value = 1, message = "La cantidad total debe ser mayor a 0")
    private Integer cantidadTotal;
    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;
    @NotNull(message = "El proveedor es obligatorio")
    private Integer proveedorId;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}