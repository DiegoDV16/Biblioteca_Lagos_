package com.bibliotecaLagos.Proveedores;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bibliotecaLagos.Proveedores.DTO.ProveedorDTO;
import com.bibliotecaLagos.Proveedores.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Proveedores.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;
import com.bibliotecaLagos.Proveedores.Repository.ProveedorRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> obtenerProveedores() {

        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Integer id) {

        return proveedorRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
                "Proveedor no encontrado"
        ));
    }

    public Proveedor crearProveedor(ProveedorDTO dto) {

        if(proveedorRepository
        .findByCorreo(dto.getCorreo())
        .isPresent()) {

            throw new DuplicateResourceException(
                    "El correo ya existe"
            );
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarProveedor(Integer id, ProveedorDTO dto) {

        Proveedor proveedor = buscarPorId(id);
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());

        return proveedorRepository.save(proveedor);
    }

    public void eliminarProveedor(Integer id) {

        Proveedor proveedor = buscarPorId(id);
        proveedorRepository.delete(proveedor);
    }
}