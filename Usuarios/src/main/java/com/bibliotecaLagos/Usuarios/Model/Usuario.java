package com.bibliotecaLagos.Usuarios.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    @Column(nullable = false, unique = true)
    private String usuario;
    @Column(nullable = false)
    private String contrasena;
    @Column(name = "rol_id", nullable = false)
    private Integer rolId;
}