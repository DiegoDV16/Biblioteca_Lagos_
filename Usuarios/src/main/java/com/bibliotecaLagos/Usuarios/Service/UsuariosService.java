package com.bibliotecaLagos.Usuarios.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotecaLagos.Usuarios.Client.RolesResponseDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuariosDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuariosResponseDTO;
import com.bibliotecaLagos.Usuarios.Model.Usuarios;
import com.bibliotecaLagos.Usuarios.Repository.UsuariosRepository;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private RolesClient rolesClient;

    public UsuariosResponseDTO crearUsuario(UsuariosDTO dto) {

        // validar rol en microservicio roles
        RolesResponseDTO rol = rolesClient.obtenerRol(dto.getRolId());

        if (rol == null) {
            throw new RuntimeException("Rol no existe");
        }

        Usuarios usuarios = new Usuarios();
        usuarios.setUsuario(dto.getUsuario());
        usuarios.setContrasena(dto.getContrasena());
        usuarios.setRolId(dto.getRolId());
        usuarios.setActivo(true);

        usuarioRepository.save(usuarios);

        UsuariosResponseDTO response = new UsuariosResponseDTO();
        response.setId(usuarios.getId());
        response.setUsuario(usuarios.getUsuario());
        response.setRol(rol.getNombre());

        return response;
    }
}