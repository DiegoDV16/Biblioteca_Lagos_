package com.bibliotecaLagos.libros.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.libros.DTO.CategoriaDTO;
import com.bibliotecaLagos.libros.DTO.LibroDTO;
import com.bibliotecaLagos.libros.DTO.ProveedorDTO;
import com.bibliotecaLagos.libros.Exception.DuplicateResourceException;
import com.bibliotecaLagos.libros.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.libros.Model.Libro;
import com.bibliotecaLagos.libros.Repository.LibroRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    @Qualifier("webClientCategorias")
    private WebClient webClientCategorias;

    @Autowired
    @Qualifier("webClientProveedores")
    private WebClient webClientProveedores;

    public List<Libro> obtenerLibros() {
        log.info("Iniciando consulta de todos los libros");
        List<Libro> libros = libroRepository.findAll();
        log.info("Consulta completada: {} libros encontrados", libros.size());
        return libros;
    }

    public Libro buscarPorId(Integer id) {
        log.info("Buscando libro por ID: {}", id);
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Libro con ID {} no encontrado", id);
                    return new ResourceNotFoundException("Libro no encontrado");
                });
        log.info("Libro encontrado: ID={}, ISBN={}", libro.getId(), libro.getIsbn());
        return libro;
    }

    public Libro buscarPorIsbn(String isbn) {
        log.info("Buscando libro por ISBN: {}", isbn);
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.warn("Libro con ISBN {} no encontrado", isbn);
                    return new ResourceNotFoundException("Libro no encontrado");
                });
        log.info("Libro encontrado: ID={}, ISBN={}", libro.getId(), libro.getIsbn());
        return libro;
    }

    public Libro crearLibro(LibroDTO dto) {
        log.info("Iniciando creacion de libro: ISBN={}, titulo={}", dto.getIsbn(), dto.getTitulo());

        if (libroRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            log.warn("El ISBN {} ya existe", dto.getIsbn());
            throw new DuplicateResourceException("El ISBN ya existe");
        }

        CategoriaDTO categoria = webClientCategorias.get()
                .uri("/{id}", dto.getCategoriaId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(
                                new ResourceNotFoundException("Categoria no encontrada")))
                .bodyToMono(CategoriaDTO.class)
                .block();

        ProveedorDTO proveedor = webClientProveedores.get()
                .uri("/{id}", dto.getProveedorId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("Proveedor no encontrado")))
                .bodyToMono(ProveedorDTO.class)
                .block();

        Libro libro = new Libro();
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setIsbn(dto.getIsbn());
        libro.setEditorial(dto.getEditorial());
        libro.setAnioPublicacion(dto.getAnioPublicacion());
        libro.setCantidadDisponible(dto.getCantidadDisponible());
        libro.setCantidadTotal(dto.getCantidadTotal());
        libro.setCategoriaId(categoria.getId());
        libro.setProveedorId(proveedor.getId());
        libro.setEstado(dto.getEstado());

        Libro guardado = libroRepository.save(libro);
        log.info("Libro creado exitosamente: ID={}, ISBN={}", guardado.getId(), guardado.getIsbn());
        return guardado;
    }

    public Libro actualizarLibro(Integer id, LibroDTO dto) {
        log.info("Iniciando actualizacion de libro ID={}", id);

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Libro con ID {} no encontrado para actualizar", id);
                    return new ResourceNotFoundException("Libro no encontrado");
                });

        Libro isbnExistente = libroRepository.findByIsbn(dto.getIsbn()).orElse(null);
        if (isbnExistente != null && !isbnExistente.getId().equals(id)) {
            log.warn("El ISBN {} ya pertenece a otro libro (ID={})", dto.getIsbn(), isbnExistente.getId());
            throw new DuplicateResourceException("El ISBN ya pertenece a otro libro");
        }

        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setIsbn(dto.getIsbn());
        libro.setEditorial(dto.getEditorial());
        libro.setAnioPublicacion(dto.getAnioPublicacion());
        libro.setCantidadDisponible(dto.getCantidadDisponible());
        libro.setCantidadTotal(dto.getCantidadTotal());
        libro.setEstado(dto.getEstado());

        Libro actualizado = libroRepository.save(libro);
        log.info("Libro ID={} actualizado a: ISBN={}", id, actualizado.getIsbn());
        return actualizado;
    }

    public void eliminarLibro(Integer id) {
        log.info("Iniciando eliminacion de libro ID={}", id);

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Libro con ID {} no encontrado para eliminar", id);
                    return new ResourceNotFoundException("Libro no encontrado");
                });

        libroRepository.delete(libro);
        log.info("Libro ID={} eliminado exitosamente", id);
    }
}
