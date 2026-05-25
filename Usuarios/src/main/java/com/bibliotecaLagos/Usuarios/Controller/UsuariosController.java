package com.bibliotecaLagos.Usuarios.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecaLagos.Usuarios.DTO.UsuariosDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuariosResponseDTO;
import com.bibliotecaLagos.Usuarios.Service.UsuariosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    @Autowired
    private UsuariosService service;

    @PostMapping
    public ResponseEntity<UsuariosResponseDTO> crear(@Valid @RequestBody UsuariosDTO dto) {
        return ResponseEntity.ok(service.crearUsuario(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuariosResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}