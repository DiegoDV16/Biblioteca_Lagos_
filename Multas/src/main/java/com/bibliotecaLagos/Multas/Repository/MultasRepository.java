package com.bibliotecaLagos.Multas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotecaLagos.Multas.Model.Multas;

import java.util.Optional;

public interface MultasRepository extends JpaRepository<Multas, Integer> {

    Optional<Multas> findByPrestamoId(Integer prestamoId);
}
