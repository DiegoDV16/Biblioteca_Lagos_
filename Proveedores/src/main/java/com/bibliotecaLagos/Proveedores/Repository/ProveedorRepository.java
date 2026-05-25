package com.bibliotecaLagos.Proveedores.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotecaLagos.Proveedores.Model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByCorreo(String correo);
}