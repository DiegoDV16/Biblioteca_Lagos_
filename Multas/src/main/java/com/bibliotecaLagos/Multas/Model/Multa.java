package com.bibliotecaLagos.Multas.Model;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "multas")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @Column(name = "prestamo_id",
    nullable = false,
    unique = true)

    private Integer prestamoId;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "dias_retraso",
    nullable = false)

    private Integer diasRetraso;

    @Column(nullable = false)
    private Boolean pagada = false;
}