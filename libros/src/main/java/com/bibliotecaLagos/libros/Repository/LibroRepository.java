package com.bibliotecaLagos.libros.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bibliotecaLagos.libros.Model.Libro;
@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer> {
    Optional<Libro> findByIsbn(String isbn);
}