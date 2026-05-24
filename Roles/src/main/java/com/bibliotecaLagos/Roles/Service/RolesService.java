package com.bibliotecaLagos.Roles.Service;


import com.bibliotecaLagos.Roles.Repository.RolesRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bibliotecaLagos.Roles.DTO.RolesDTO;
import com.bibliotecaLagos.Roles.DTO.RolesResponseDTO;
import com.bibliotecaLagos.Roles.Model.Roles;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;

    public RolesResponseDTO crear(RolesDTO request) {

        if (rolesRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new RuntimeException("El rol ya existe");
        }

        Roles roles = Roles.builder()
                .nombre(request.getNombre())
                .build();

        rolesRepository.save(roles);

        return RolesResponseDTO.builder()
                .id(roles.getId())
                .nombre(roles.getNombre())
                .build();
    }

    public List<RolesResponseDTO> listar() {
        return rolesRepository.findAll()
                .stream()
                .map(roles -> RolesResponseDTO.builder()
                        .id(roles.getId())
                        .nombre(roles.getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    public RolesResponseDTO obtenerPorId(Integer id) {
        Roles roles = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return RolesResponseDTO.builder()
                .id(roles.getId())
                .nombre(roles.getNombre())
                .build();
    }

    public void eliminar(Integer id) {
        rolesRepository.deleteById(id);
    }
}
