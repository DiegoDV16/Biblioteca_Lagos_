package com.bibliotecaLagos.Prestamos.Model;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestamos")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "socio_id", nullable = false)
    private Integer socioId;

    @Column(name = "libro_id", nullable = false)
    private Integer libroId;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    private String estado;
}