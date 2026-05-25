package com.bibliotecaLagos.Socios.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecaLagos.Socios.Model.Socio;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Integer> {

    Optional<Socio> findByRut(String rut);
    Optional<Socio> findByCorreo(String correo);
}