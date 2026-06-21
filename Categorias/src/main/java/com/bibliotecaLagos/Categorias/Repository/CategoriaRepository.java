package com.bibliotecaLagos.Categorias.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecaLagos.Categorias.Model.Categoria;

@Repository
public interface CategoriaRepository
        extends JpaRepository<Categoria, Integer> {
}