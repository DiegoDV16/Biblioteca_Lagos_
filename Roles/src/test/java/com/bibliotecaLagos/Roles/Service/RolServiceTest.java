package com.bibliotecaLagos.Roles.Service;

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

import com.bibliotecaLagos.Roles.DTO.RolDTO;
import com.bibliotecaLagos.Roles.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Roles.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Roles.Model.Rol;
import com.bibliotecaLagos.Roles.Repository.RolRepository;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol crearRolEjemplo(Integer id) {
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("Admin");
        return rol;
    }

    @Test
    @DisplayName("obtenerRoles -> Retorna lista de roles")
    public void obtenerRoles_DeberiaRetornarLista() {
        var rol = crearRolEjemplo(1);

        when(rolRepository.findAll()).thenReturn(List.of(rol));

        List<Rol> resultado = rolService.obtenerRoles();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Admin", resultado.get(0).getNombre());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId -> Retorna rol cuando el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarRol() {
        var rol = crearRolEjemplo(1);

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        Rol resultado = rolService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Admin", resultado.getNombre());
        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("buscarPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rolService.buscarPorId(99));
        verify(rolRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearRol -> Crea y retorna rol cuando el nombre es unico")
    public void crearRol_CuandoNombreUnico_DeberiaCrear() {
        var dto = new RolDTO();
        dto.setNombre("Admin");

        var rolGuardado = crearRolEjemplo(1);

        when(rolRepository.findByNombre("Admin")).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(rolGuardado);

        Rol resultado = rolService.crearRol(dto);

        assertNotNull(resultado);
        assertEquals("Admin", resultado.getNombre());
        verify(rolRepository, times(1)).findByNombre("Admin");
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("crearRol -> Lanza DuplicateResourceException cuando el nombre ya existe")
    public void crearRol_CuandoNombreDuplicado_DeberiaLanzarExcepcion() {
        var dto = new RolDTO();
        dto.setNombre("Admin");

        var existente = crearRolEjemplo(1);

        when(rolRepository.findByNombre("Admin")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> rolService.crearRol(dto));
        verify(rolRepository, times(1)).findByNombre("Admin");
        verify(rolRepository, times(0)).save(any(Rol.class));
    }

    @Test
    @DisplayName("actualizarRol -> Actualiza y retorna rol cuando el ID existe")
    public void actualizarRol_CuandoExiste_DeberiaActualizar() {
        var existente = crearRolEjemplo(1);

        var dto = new RolDTO();
        dto.setNombre("Usuario");

        when(rolRepository.findById(1)).thenReturn(Optional.of(existente));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.actualizarRol(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Usuario", resultado.getNombre());
        verify(rolRepository, times(1)).findById(1);
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("actualizarRol -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarRol_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new RolDTO();
        dto.setNombre("Usuario");

        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rolService.actualizarRol(99, dto));
        verify(rolRepository, times(1)).findById(99);
        verify(rolRepository, times(0)).save(any(Rol.class));
    }

    @Test
    @DisplayName("eliminarRol -> Elimina rol cuando el ID existe")
    public void eliminarRol_CuandoExiste_DeberiaEliminar() {
        var rol = crearRolEjemplo(1);

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        doNothing().when(rolRepository).delete(rol);

        rolService.eliminarRol(1);

        verify(rolRepository, times(1)).findById(1);
        verify(rolRepository, times(1)).delete(rol);
    }

    @Test
    @DisplayName("eliminarRol -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarRol_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rolService.eliminarRol(99));
        verify(rolRepository, times(1)).findById(99);
        verify(rolRepository, times(0)).delete(any(Rol.class));
    }
}
