package com.bibliotecaLagos.Usuarios.Service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bibliotecaLagos.Usuarios.Client.RolClient;
import com.bibliotecaLagos.Usuarios.Client.RolesResponseDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuariosDTO;
import com.bibliotecaLagos.Usuarios.DTO.UsuariosResponseDTO;
import com.bibliotecaLagos.Usuarios.Model.Usuarios;
import com.bibliotecaLagos.Usuarios.Repository.UsuariosRepository;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private RolClient rolClient;

    public UsuariosResponseDTO crearUsuario(UsuariosDTO dto) {

        RolesResponseDTO rol = rolClient.obtenerRol(dto.getRolId());

        if (rol == null) {
            throw new RuntimeException("Rol no existe");
        }

        if (usuariosRepository.findByUsuario(dto.getUsuario()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        
        Usuarios usuarios = new Usuarios();
        usuarios.setUsuario(dto.getUsuario());
        usuarios.setContrasena(dto.getContrasena());
        usuarios.setRolId(dto.getRolId());
        usuarios.setActivo(true);

        usuariosRepository.save(usuarios);

        UsuariosResponseDTO response = new UsuariosResponseDTO();
        response.setId(usuarios.getId());
        response.setUsuario(usuarios.getUsuario());
        response.setRol(rol.getNombre());

        
        return response;

    }

    public List<UsuariosResponseDTO> listar() {
        return usuariosRepository.findAll().stream().map(u -> {

        RolesResponseDTO rol = rolClient.obtenerRol(u.getRolId());

        UsuariosResponseDTO r = new UsuariosResponseDTO();
        r.setId(u.getId());
        r.setUsuario(u.getUsuario());
        r.setRol(rol != null ? rol.getNombre() : "SIN ROL");

        return r;
    }).toList();
    }
}