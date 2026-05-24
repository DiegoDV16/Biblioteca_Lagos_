package com.bibliotecaLagos.Roles.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.bibliotecaLagos.Roles.DTO.RolesDTO;
import com.bibliotecaLagos.Roles.DTO.RolesResponseDTO;
import com.bibliotecaLagos.Roles.Service.RolesService;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolesController {

    private final RolesService rolesService;

    @PostMapping
    public RolesResponseDTO crear(@RequestBody RolesDTO request) {
        return rolesService.crear(request);
    }

    @GetMapping
    public List<RolesResponseDTO> listar() {
        return rolesService.listar();
    }

    @GetMapping("/{id}")
    public RolesResponseDTO obtener(@PathVariable Integer id) {
        return rolesService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        rolesService.eliminar(id);
    }
}