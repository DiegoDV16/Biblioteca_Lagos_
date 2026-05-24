package com.bibliotecaLagos.Roles.Repository;

import com.bibliotecaLagos.Roles.Model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByNombre(String nombre);
}