package com.bibliotecaLagos.Proveedores.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotecaLagos.Proveedores.DTO.ProveedorDTO;
import com.bibliotecaLagos.Proveedores.Model.Proveedor;
import com.bibliotecaLagos.Proveedores.Repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> obtenerProveedores() {
        return proveedorRepository.findAll();
    }

    public Proveedor obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    public Proveedor crearProveedor(ProveedorDTO dto) {

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarProveedor(Integer id, ProveedorDTO dto) {

        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);

        if(proveedor == null) {
            return null;
        }

        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());
        return proveedorRepository.save(proveedor);
    }

    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }
}