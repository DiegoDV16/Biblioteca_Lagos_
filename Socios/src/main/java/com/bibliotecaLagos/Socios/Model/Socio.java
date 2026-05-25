package com.bibliotecaLagos.Socios.Model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "socios")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false, unique = true)
    private String correo;

    private String telefono;

    @Column(nullable = false)
    private Integer idTipoSocio;
}