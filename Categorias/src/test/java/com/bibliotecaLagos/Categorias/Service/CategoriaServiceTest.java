package com.bibliotecaLagos.Categorias.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bibliotecaLagos.Categorias.DTO.CategoriaDTO;
import com.bibliotecaLagos.Categorias.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Categorias.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Categorias.Model.Categoria;
import com.bibliotecaLagos.Categorias.Repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("obtenerCategorias -> Retorna lista de categorias")
    public void obtenerCategorias_DeberiaRetornarLista() {
        var categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Fantasia");
        categoria.setDescripcion("Libros de fantasia");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> resultado = categoriaService.obtenerCategorias();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Fantasia", resultado.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId -> Retorna categoria cuando el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarCategoria() {
        var categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Fantasia");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Fantasia", resultado.getNombre());
        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("buscarPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.buscarPorId(99));
        verify(categoriaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearCategoria -> Crea y retorna categoria cuando el nombre es unico")
    public void crearCategoria_CuandoNombreUnico_DeberiaCrear() {
        var dto = new CategoriaDTO("Nuevo Genero", "Descripcion del genero");
        var categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1);
        categoriaGuardada.setNombre("Nuevo Genero");
        categoriaGuardada.setDescripcion("Descripcion del genero");

        when(categoriaRepository.findByNombre("Nuevo Genero")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        Categoria resultado = categoriaService.crearCategoria(dto);

        assertNotNull(resultado);
        assertEquals("Nuevo Genero", resultado.getNombre());
        assertEquals("Descripcion del genero", resultado.getDescripcion());
        verify(categoriaRepository, times(1)).findByNombre("Nuevo Genero");
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("crearCategoria -> Lanza DuplicateResourceException cuando el nombre ya existe")
    public void crearCategoria_CuandoNombreDuplicado_DeberiaLanzarExcepcion() {
        var dto = new CategoriaDTO("Fantasia", "Libros de fantasia");
        var existente = new Categoria();
        existente.setId(1);
        existente.setNombre("Fantasia");

        when(categoriaRepository.findByNombre("Fantasia")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> categoriaService.crearCategoria(dto));
        verify(categoriaRepository, times(1)).findByNombre("Fantasia");
        verify(categoriaRepository, times(0)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("actualizarCategoria -> Actualiza y retorna categoria cuando el ID existe")
    public void actualizarCategoria_CuandoExiste_DeberiaActualizar() {
        var existente = new Categoria();
        existente.setId(1);
        existente.setNombre("Fantasia");
        existente.setDescripcion("Antigua descripcion");

        var dto = new CategoriaDTO("Fantasia", "Nueva descripcion");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = categoriaService.actualizarCategoria(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Fantasia", resultado.getNombre());
        assertEquals("Nueva descripcion", resultado.getDescripcion());
        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("actualizarCategoria -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarCategoria_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new CategoriaDTO("Fantasia", "Descripcion");

        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.actualizarCategoria(99, dto));
        verify(categoriaRepository, times(1)).findById(99);
        verify(categoriaRepository, times(0)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("eliminarCategoria -> Elimina categoria cuando el ID existe")
    public void eliminarCategoria_CuandoExiste_DeberiaEliminar() {
        var categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Fantasia");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).delete(categoria);

        categoriaService.eliminarCategoria(1);

        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    @DisplayName("eliminarCategoria -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarCategoria_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.eliminarCategoria(99));
        verify(categoriaRepository, times(1)).findById(99);
        verify(categoriaRepository, times(0)).delete(any(Categoria.class));
    }
}
