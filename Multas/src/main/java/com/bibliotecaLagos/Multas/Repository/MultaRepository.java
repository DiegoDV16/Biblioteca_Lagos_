package com.bibliotecaLagos.Multas.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotecaLagos.Multas.Model.Multa;

public interface MultaRepository extends JpaRepository<Multa, Integer> {

    Optional<Multa> findByPrestamoId(Integer prestamoId);
}