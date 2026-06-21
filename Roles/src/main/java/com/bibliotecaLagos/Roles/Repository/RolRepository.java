package com.bibliotecaLagos.Roles.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotecaLagos.Roles.Model.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombre(String nombre);
}