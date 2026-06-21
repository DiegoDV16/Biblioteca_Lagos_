package com.bibliotecaLagos.Usuarios.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.Usuarios.DTO.UsuarioDTO;
import com.bibliotecaLagos.Usuarios.Model.Usuario;
import com.bibliotecaLagos.Usuarios.Service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")

public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {

        List<Usuario> lista = usuarioService.obtenerUsuarios();

        if(lista.isEmpty()) {

            return ResponseEntity
            .noContent()
            .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario>
            crearUsuario(
                    @Valid
                    @RequestBody
                    UsuarioDTO dto
            ) {

        Usuario usuarioNuevo = usuarioService.crearUsuario(dto);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(usuarioNuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario>
        actualizarUsuario(
            @PathVariable Integer id,
            @Valid
            @RequestBody
            UsuarioDTO dto
        ) {

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, dto);

        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
            eliminarUsuario(@PathVariable Integer id) {

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.ok(
                "Usuario eliminado correctamente"
        );
    }
}