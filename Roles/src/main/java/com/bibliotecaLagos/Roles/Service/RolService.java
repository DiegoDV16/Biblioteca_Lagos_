package com.bibliotecaLagos.Roles.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RolService.class);

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> obtenerRoles() {
        log.info("Iniciando consulta de todos los roles");
        List<Rol> roles = rolRepository.findAll();
        log.info("Consulta completada: {} roles encontrados", roles.size());
        return roles;
    }

    public Optional<Rol> buscarPorId(Integer id) {
        log.info("Buscando rol por ID: {}", id);
        Optional<Rol> rol = rolRepository.findById(id);
        if (rol.isPresent()) {
            log.info("Rol encontrado: ID={}, nombre={}", rol.get().getId(), rol.get().getNombre());
        } else {
            log.warn("Rol con ID {} no encontrado", id);
        }
        return rol;
    }

    public Rol crearRol(RolDTO dto) {
        log.info("Iniciando creacion de rol: nombre={}", dto.getNombre());

        if (rolRepository.findByNombre(dto.getNombre()).isPresent()) {
            log.warn("El rol '{}' ya existe", dto.getNombre());
            throw new DuplicateResourceException("El rol ya existe");
        }

        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());

        Rol guardado = rolRepository.save(rol);
        log.info("Rol creado exitosamente: ID={}, nombre={}", guardado.getId(), guardado.getNombre());
        return guardado;
    }

    public Rol actualizarRol(Integer id, RolDTO dto) {
        log.info("Iniciando actualizacion de rol ID={}", id);

        Rol rol = buscarPorId(id).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        rol.setNombre(dto.getNombre());

        Rol actualizado = rolRepository.save(rol);
        log.info("Rol ID={} actualizado a: nombre={}", id, actualizado.getNombre());
        return actualizado;
    }

    public void eliminarRol(Integer id) {
        log.info("Iniciando eliminacion de rol ID={}", id);

        Rol rol = buscarPorId(id).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        rolRepository.delete(rol);

        log.info("Rol ID={} eliminado exitosamente", id);
    }
}