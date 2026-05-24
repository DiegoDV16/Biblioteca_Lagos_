package com.bibliotecaLagos.tipoSocios.Model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipoSocios")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TipoSocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_socio", nullable = false)
    private String tipoSocio;
}