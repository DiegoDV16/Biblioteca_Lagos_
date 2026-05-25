package com.bibliotecaLagos.Reserva.Repository;

import com.bibliotecaLagos.Reserva.Model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}
