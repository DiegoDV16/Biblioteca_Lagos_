package com.bibliotecaLagos.Usuarios.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    @Qualifier("webClientRoles")
    private WebClient webClientRoles;

    public List<Usuario> obtenerUsuarios() {
        log.info("Iniciando consulta de todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Consulta completada: {} usuarios encontrados", usuarios.size());
        return usuarios;
    }

    public Usuario buscarPorId(Integer id) {
        log.info("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con ID {} no encontrado", id);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
        log.info("Usuario encontrado: ID={}, usuario={}", usuario.getId(), usuario.getUsuario());
        return usuario;
    }

    public Usuario crearUsuario(UsuarioDTO dto) {
        log.info("Iniciando creacion de usuario: {}", dto.getUsuario());

        if (usuarioRepository.findByUsuario(dto.getUsuario()).isPresent()) {
            log.warn("El usuario {} ya existe", dto.getUsuario());
            throw new DuplicateResourceException("El usuario ya existe");
        }

        log.info("Validando rol ID={} con microservicio roles", dto.getRolId());
        RolDTO rol = webClientRoles.get()
                .uri("/{id}", dto.getRolId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("Rol no encontrado")))
                .bodyToMono(RolDTO.class)
                .block();

        log.info("Rol validado: ID={}, nombre={}", rol.getId(), rol.getNombre());

        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setRolId(rol.getId());

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente: ID={}, usuario={}", guardado.getId(), guardado.getUsuario());
        return guardado;
    }

    public Usuario actualizarUsuario(Integer id, UsuarioDTO dto) {
        log.info("Iniciando actualizacion de usuario ID={}", id);
        Usuario usuarioExistente = buscarPorId(id);

        if (!usuarioExistente.getUsuario().equals(dto.getUsuario()) &&
                usuarioRepository.findByUsuario(dto.getUsuario()).isPresent()) {
            log.warn("El usuario {} ya existe", dto.getUsuario());
            throw new DuplicateResourceException("El usuario ya existe");
        }

        if (dto.getRolId() != null && !usuarioExistente.getRolId().equals(dto.getRolId())) {
            log.info("Validando rol ID={} con microservicio roles", dto.getRolId());
            webClientRoles.get()
                    .uri("/{id}", dto.getRolId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            response -> Mono.error(new ResourceNotFoundException("Rol no encontrado")))
                    .bodyToMono(RolDTO.class)
                    .block();
        }

        usuarioExistente.setUsuario(dto.getUsuario());
        usuarioExistente.setContrasena(dto.getContrasena());
        usuarioExistente.setRolId(dto.getRolId());

        Usuario guardado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario ID={} actualizado exitosamente", guardado.getId());
        return guardado;
    }

    public void eliminarUsuario(Integer id) {
        log.info("Iniciando eliminacion de usuario ID={}", id);
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario ID={} eliminado exitosamente", id);
    }
}
