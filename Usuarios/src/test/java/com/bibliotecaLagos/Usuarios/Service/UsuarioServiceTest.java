package com.bibliotecaLagos.Usuarios.Service;

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
import org.springframework.web.reactive.function.client.WebClient;

import com.bibliotecaLagos.Usuarios.DTO.RolDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuarioDTO;
import com.bibliotecaLagos.Usuarios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Usuarios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Usuarios.Model.Usuario;
import com.bibliotecaLagos.Usuarios.Repository.UsuarioRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private WebClient webClientRoles;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario crearUsuarioEjemplo(Integer id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsuario("jperez");
        usuario.setContrasena("123456");
        usuario.setRolId(1);
        return usuario;
    }

    @Test
    @DisplayName("obtenerUsuarios -> Retorna lista de usuarios")
    public void obtenerUsuarios_DeberiaRetornarLista() {
        var usuario = crearUsuarioEjemplo(1);

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.obtenerUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("jperez", resultado.get(0).getUsuario());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId -> Retorna usuario cuando el ID existe")
    public void buscarPorId_CuandoExiste_DeberiaRetornarUsuario() {
        var usuario = crearUsuarioEjemplo(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("jperez", resultado.getUsuario());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("buscarPorId -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.buscarPorId(99));
        verify(usuarioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("crearUsuario -> Crea y retorna usuario cuando los datos son validos")
    public void crearUsuario_CuandoDatosValidos_DeberiaCrear() {
        var dto = new UsuarioDTO();
        dto.setUsuario("jperez");
        dto.setContrasena("123456");
        dto.setRolId(1);

        var usuarioGuardado = crearUsuarioEjemplo(1);
        var rolDTO = new RolDTO(1, "Administrador");

        when(usuarioRepository.findByUsuario("jperez")).thenReturn(Optional.empty());
        when(webClientRoles.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/{id}", 1)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(java.util.function.Predicate.class), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RolDTO.class)).thenReturn(Mono.just(rolDTO));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals("jperez", resultado.getUsuario());
        verify(usuarioRepository, times(1)).findByUsuario("jperez");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crearUsuario -> Lanza DuplicateResourceException cuando el usuario ya existe")
    public void crearUsuario_CuandoUsuarioDuplicado_DeberiaLanzarExcepcion() {
        var dto = new UsuarioDTO();
        dto.setUsuario("jperez");

        var existente = crearUsuarioEjemplo(1);

        when(usuarioRepository.findByUsuario("jperez")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateResourceException.class,
                () -> usuarioService.crearUsuario(dto));
        verify(usuarioRepository, times(1)).findByUsuario("jperez");
        verify(usuarioRepository, times(0)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("actualizarUsuario -> Actualiza y retorna usuario cuando el ID existe")
    public void actualizarUsuario_CuandoExiste_DeberiaActualizar() {
        var existente = crearUsuarioEjemplo(1);

        var dto = new UsuarioDTO();
        dto.setUsuario("carlos");
        dto.setContrasena("123456");
        dto.setRolId(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(1, dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("carlos", resultado.getUsuario());
        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("actualizarUsuario -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void actualizarUsuario_CuandoNoExiste_DeberiaLanzarExcepcion() {
        var dto = new UsuarioDTO();
        dto.setUsuario("carlos");

        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.actualizarUsuario(99, dto));
        verify(usuarioRepository, times(1)).findById(99);
        verify(usuarioRepository, times(0)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("eliminarUsuario -> Elimina usuario cuando el ID existe")
    public void eliminarUsuario_CuandoExiste_DeberiaEliminar() {
        var usuario = crearUsuarioEjemplo(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(usuario);

        usuarioService.eliminarUsuario(1);

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("eliminarUsuario -> Lanza ResourceNotFoundException cuando el ID no existe")
    public void eliminarUsuario_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.eliminarUsuario(99));
        verify(usuarioRepository, times(1)).findById(99);
        verify(usuarioRepository, times(0)).delete(any(Usuario.class));
    }
}
