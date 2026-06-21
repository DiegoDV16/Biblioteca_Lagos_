package com.bibliotecaLagos.Usuarios.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.bibliotecaLagos.Usuarios.DTO.RolDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuarioDTO;
import com.bibliotecaLagos.Usuarios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Usuarios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Usuarios.Model.Usuario;
import com.bibliotecaLagos.Usuarios.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    @Qualifier("webClientRoles")
    private WebClient webClientRoles;

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Integer id) {

        return usuarioRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
                "Usuario no encontrado"
        ));
    }

    public Usuario crearUsuario(UsuarioDTO dto) {

        if(usuarioRepository.findByUsuario(dto.getUsuario()).isPresent()) {
            throw new DuplicateResourceException(
                    "El usuario ya existe"
            );
        }

        RolDTO rol = webClientRoles.get()
        .uri("/{id}", dto.getRolId())
        .retrieve()
        .onStatus(
                HttpStatusCode::is4xxClientError,

                response -> Mono.error(
                        new ResourceNotFoundException(
                                "Rol no encontrado"
                        )
                )
        )
        .bodyToMono(RolDTO.class)
        .block();

        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setRolId(rol.getId());
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Integer id, UsuarioDTO dto) {

        Usuario usuario = buscarPorId(id);
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setRolId(dto.getRolId());
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Integer id) {

        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}