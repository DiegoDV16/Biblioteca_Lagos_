package com.bibliotecaLagos.Reserva.Model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "socio_id", nullable = false)
    private Integer socioId;

    @Column(name = "libro_id", nullable = false)
    private Integer libroId;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(nullable = false)
    private String estado;
}