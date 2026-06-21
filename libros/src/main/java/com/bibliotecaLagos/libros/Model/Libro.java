package com.bibliotecaLagos.libros.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false)
    private String autor;
    @Column(unique = true, nullable = false)
    private String isbn;
    @Column(nullable = false)
    private String editorial;
    @Column(nullable = false)
    private Integer anioPublicacion;
    @Column(nullable = false)
    private Integer cantidadDisponible;
    @Column(nullable = false)
    private Integer cantidadTotal;
    @Column(nullable = false)
    private Integer categoriaId;
    @Column(nullable = false)
    private Integer proveedorId;
    @Column(nullable = false)
    private String estado;
}