package com.bibliotecaLagos.Usuarios.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotecaLagos.Usuarios.Model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
 
    Optional<Usuario> findByUsuario(String usuario);
}