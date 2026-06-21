package com.bibliotecaLagos.Categorias.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerCategorias() {
        log.info("Iniciando consulta de todas las categorias");
        List<Categoria> categorias = categoriaRepository.findAll();
        log.info("Consulta completada: {} categorias encontradas", categorias.size());
        return categorias;
    }

    public Categoria buscarPorId(Integer id) {
        log.info("Buscando categoria por ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria con ID {} no encontrada", id);
                    return new ResourceNotFoundException("Categoria no encontrada");
                });
        log.info("Categoria encontrada: ID={}, nombre={}", categoria.getId(), categoria.getNombre());
        return categoria;
    }

    public Categoria crearCategoria(CategoriaDTO dto) {
        log.info("Iniciando creacion de categoria: nombre={}", dto.getNombre());

        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            log.warn("La categoria con nombre '{}' ya existe", dto.getNombre());
            throw new DuplicateResourceException("La categoria ya existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoria creada exitosamente: ID={}, nombre={}", guardada.getId(), guardada.getNombre());
        return guardada;
    }

    public Categoria actualizarCategoria(Integer id, CategoriaDTO dto) {
        log.info("Iniciando actualizacion de categoria ID={}", id);
        Categoria categoria = buscarPorId(id);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        Categoria actualizada = categoriaRepository.save(categoria);
        log.info("Categoria ID={} actualizada a: nombre={}", id, actualizada.getNombre());
        return actualizada;
    }

    public void eliminarCategoria(Integer id) {
        log.info("Iniciando eliminacion de categoria ID={}", id);
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
        log.info("Categoria ID={} eliminada exitosamente", id);
    }
}