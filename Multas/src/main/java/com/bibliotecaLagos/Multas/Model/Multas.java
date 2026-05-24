package com.bibliotecaLagos.Multas.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "multas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer prestamoId;

    private Double monto;

    private Integer diasRetraso;

    private Boolean pagada;
}