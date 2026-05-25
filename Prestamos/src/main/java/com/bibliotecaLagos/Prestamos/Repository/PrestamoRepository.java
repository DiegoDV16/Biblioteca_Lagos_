package com.bibliotecaLagos.Prestamos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecaLagos.Prestamos.Model.Prestamo;

@Repository
public interface PrestamoRepository
        extends JpaRepository<Prestamo, Integer> {
}