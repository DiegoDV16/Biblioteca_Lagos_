package com.bibliotecaLagos.Categorias.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.bibliotecaLagos.Categorias.DTO.CategoriaDTO;
import com.bibliotecaLagos.Categorias.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Categorias.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Categorias.Model.Categoria;
import com.bibliotecaLagos.Categorias.Repository.CategoriaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Integer id) {

        return categoriaRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
                "Categoria no encontrada"
        ));
    }

    public Categoria crearCategoria(CategoriaDTO dto) {

        if(categoriaRepository
            .findByNombre(dto.getNombre())
            .isPresent()) {

            throw new DuplicateResourceException(
            "La categoria ya existe"
            );
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Integer id, CategoriaDTO dto) {

        Categoria categoria = buscarPorId(id);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Integer id) {

        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}