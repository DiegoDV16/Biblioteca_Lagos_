package com.bibliotecaLagos.tipoSocios.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;

import com.bibliotecaLagos.tipoSocios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.tipoSocios.Exception.ResourceNotFoundException;

import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;

import com.bibliotecaLagos.tipoSocios.Repository.TipoSocioRepository;

@Service
public class TipoSocioService {

    @Autowired
    private TipoSocioRepository tipoSocioRepository;

    public List<TipoSocio> obtenerTiposSocio() {
        return tipoSocioRepository.findAll();
    }

    public TipoSocio obtenerTipoSocioPorId(Integer id) {

        return tipoSocioRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
                "Tipo de socio no encontrado"
        ));
    }

    public TipoSocio crearTipoSocio(TipoSocioDTO dto) {

        if(tipoSocioRepository.findByTipoSocio(dto.getTipoSocio()).isPresent()) {

            throw new DuplicateResourceException(
            "El tipo de socio ya existe"
            );
        }

        TipoSocio tipoSocio = new TipoSocio();

        tipoSocio.setTipoSocio(dto.getTipoSocio());
        return tipoSocioRepository.save(tipoSocio);
    }
    public void eliminarTipoSocio(Integer id) {

        TipoSocio tipoSocio = obtenerTipoSocioPorId(id);
        tipoSocioRepository.delete(tipoSocio);
    }
}