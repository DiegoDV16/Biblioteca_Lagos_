package com.bibliotecaLagos.Roles.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bibliotecaLagos.Roles.DTO.RolDTO;
import com.bibliotecaLagos.Roles.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Roles.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Roles.Model.Rol;
import com.bibliotecaLagos.Roles.Repository.RolRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional

public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    public Rol buscarPorId(Integer id) {

        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                 "Rol no encontrado"));
    }

    public Rol crearRol(RolDTO dto) {

        if(rolRepository
            .findByNombre(dto.getNombre())
            .isPresent()) {

            throw new DuplicateResourceException( "El rol ya existe");
        }

        Rol rol = new Rol();

        rol.setNombre(dto.getNombre());
        return rolRepository.save(rol);
    }

    public Rol actualizarRol(Integer id, RolDTO dto) {

        Rol rol = buscarPorId(id);
        rol.setNombre(dto.getNombre());
        return rolRepository.save(rol);
    }

    public void eliminarRol(Integer id) {

        Rol rol = buscarPorId(id);
        rolRepository.delete(rol);
    }
}