package com.bibliotecaLagos.tipoSocios.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;

@Repository
public interface TipoSocioRepository extends JpaRepository<TipoSocio, Integer> {

    Optional<TipoSocio> findByTipoSocio(String tipoSocio);
}