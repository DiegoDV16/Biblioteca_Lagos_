package com.bibliotecaLagos.Usuarios.Controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecaLagos.Usuarios.Assemblers.UsuarioModelAssembler;
import com.bibliotecaLagos.Usuarios.DTO.UsuarioDTO;
import com.bibliotecaLagos.Usuarios.Model.Usuario;
import com.bibliotecaLagos.Usuarios.Service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> obtenerUsuarios() {
        log.info("GET /api/v1/usuarios - listar todos los usuarios");
        List<Usuario> lista = usuarioService.obtenerUsuarios();

        if (lista.isEmpty()) {
            log.info("No se encontraron usuarios");
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Usuario>> usuariosModel = lista.stream()
                .map(usuarioModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> collectionModel = CollectionModel.of(usuariosModel,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withSelfRel());

        log.info("Retornando {} usuarios", lista.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/v1/usuarios/{} - buscar usuario por ID", id);
        Usuario usuario = usuarioService.buscarPorId(id);
        log.info("Usuario encontrado: ID={}, usuario={}", usuario.getId(), usuario.getUsuario());
        return ResponseEntity.ok(usuarioModelAssembler.toModel(usuario));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> crearUsuario(@Valid @RequestBody UsuarioDTO dto) {
        log.info("POST /api/v1/usuarios - crear nuevo usuario: {}", dto.getUsuario());
        Usuario usuarioNuevo = usuarioService.crearUsuario(dto);
        log.info("Usuario creado exitosamente: ID={}", usuarioNuevo.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioModelAssembler.toModel(usuarioNuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO dto) {
        log.info("PUT /api/v1/usuarios/{} - actualizar usuario", id);
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, dto);
        log.info("Usuario actualizado exitosamente: ID={}", usuarioActualizado.getId());
        return ResponseEntity.ok(usuarioModelAssembler.toModel(usuarioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Integer id) {
        log.info("DELETE /api/v1/usuarios/{} - eliminar usuario", id);
        usuarioService.eliminarUsuario(id);
        log.info("Usuario ID={} eliminado exitosamente", id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}
