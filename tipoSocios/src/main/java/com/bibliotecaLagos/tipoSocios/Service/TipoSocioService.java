package com.bibliotecaLagos.tipoSocios.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotecaLagos.tipoSocios.DTO.TipoSocioDTO;
import com.bibliotecaLagos.tipoSocios.Exception.DuplicateResourceException;
import com.bibliotecaLagos.tipoSocios.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.tipoSocios.Model.TipoSocio;
import com.bibliotecaLagos.tipoSocios.Repository.TipoSocioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TipoSocioService {

    private static final Logger log = LoggerFactory.getLogger(TipoSocioService.class);

    @Autowired
    private TipoSocioRepository tipoSocioRepository;

    public List<TipoSocio> obtenerTiposSocio() {
        log.info("Iniciando consulta de todos los tipos de socio");
        List<TipoSocio> tipos = tipoSocioRepository.findAll();
        log.info("Consulta completada: {} tipos de socio encontrados", tipos.size());
        return tipos;
    }

    public TipoSocio obtenerTipoSocioPorId(Integer id) {
        log.info("Buscando tipo de socio por ID: {}", id);
        TipoSocio tipoSocio = tipoSocioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo de socio con ID {} no encontrado", id);
                    return new ResourceNotFoundException("Tipo de socio no encontrado");
                });
        log.info("Tipo de socio encontrado: ID={}, tipoSocio={}", tipoSocio.getId(), tipoSocio.getTipoSocio());
        return tipoSocio;
    }

    public TipoSocio crearTipoSocio(TipoSocioDTO dto) {
        log.info("Iniciando creacion de tipo de socio: tipoSocio={}", dto.getTipoSocio());

        if (tipoSocioRepository.findByTipoSocio(dto.getTipoSocio()).isPresent()) {
            log.warn("El tipo de socio '{}' ya existe", dto.getTipoSocio());
            throw new DuplicateResourceException("El tipo de socio ya existe");
        }

        TipoSocio tipoSocio = new TipoSocio();
        tipoSocio.setTipoSocio(dto.getTipoSocio());

        TipoSocio guardado = tipoSocioRepository.save(tipoSocio);
        log.info("Tipo de socio creado exitosamente: ID={}, tipoSocio={}", guardado.getId(), guardado.getTipoSocio());
        return guardado;
    }

    public TipoSocio actualizarTipoSocio(Integer id, TipoSocioDTO dto) {
        log.info("Iniciando actualizacion de tipo de socio ID={}", id);

        TipoSocio tipoSocio = obtenerTipoSocioPorId(id);
        tipoSocio.setTipoSocio(dto.getTipoSocio());

        TipoSocio actualizado = tipoSocioRepository.save(tipoSocio);
        log.info("Tipo de socio ID={} actualizado a: tipoSocio={}", id, actualizado.getTipoSocio());
        return actualizado;
    }

    public void eliminarTipoSocio(Integer id) {
        log.info("Iniciando eliminacion de tipo de socio ID={}", id);

        TipoSocio tipoSocio = obtenerTipoSocioPorId(id);
        tipoSocioRepository.delete(tipoSocio);

        log.info("Tipo de socio ID={} eliminado exitosamente", id);
    }
}