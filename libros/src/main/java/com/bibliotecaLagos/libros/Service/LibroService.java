package com.bibliotecaLagos.libros.Service;

import java.util.List;
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

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    @Qualifier("webClientCategorias")
    private WebClient webClientCategorias;
    @Autowired
    @Qualifier("webClientProveedores")
    private WebClient webClientProveedores;
    public List<Libro> obtenerLibros() {
        return libroRepository.findAll();
    }

    public Libro buscarPorId(Integer id) {
        return libroRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException("Libro no encontrado"));
    }

    public Libro buscarPorIsbn(String isbn) {

        return libroRepository.findByIsbn(isbn)
        .orElseThrow(() ->
        new ResourceNotFoundException("Libro no encontrado"));
    }

    public Libro crearLibro(LibroDTO dto) {

        if (libroRepository.findByIsbn(dto.getIsbn()).isPresent()) {
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

        return libroRepository.save(libro);
    }

    public Libro actualizarLibro(Integer id, LibroDTO dto) {

        Libro libro = libroRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        Libro isbnExistente = libroRepository.findByIsbn(dto.getIsbn()).orElse(null);

        if (
                isbnExistente != null &&
                !isbnExistente.getId().equals(id)
        ) {

            throw new DuplicateResourceException(
                    "El ISBN ya pertenece a otro libro"
            );
        }

        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setIsbn(dto.getIsbn());
        libro.setEditorial(dto.getEditorial());
        libro.setAnioPublicacion(dto.getAnioPublicacion());
        libro.setCantidadDisponible(dto.getCantidadDisponible());
        libro.setCantidadTotal(dto.getCantidadTotal());
        libro.setEstado(dto.getEstado());
        return libroRepository.save(libro);
    }

    public void eliminarLibro(Integer id) {

        Libro libro = libroRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
            "Libro no encontrado"
        ));

        libroRepository.delete(libro);
    }
}