package com.bibliotecaLagos.Multas.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.bibliotecaLagos.Multas.DTO.MultaDTO;
import com.bibliotecaLagos.Multas.DTO.PrestamoDTO;
import com.bibliotecaLagos.Multas.Exception.DuplicateResourceException;
import com.bibliotecaLagos.Multas.Exception.ResourceNotFoundException;
import com.bibliotecaLagos.Multas.Model.Multa;
import com.bibliotecaLagos.Multas.Repository.MultaRepository;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class MultaService {

    private static final Logger log = LoggerFactory.getLogger(MultaService.class);

    @Autowired
    private MultaRepository multaRepository;

    @Autowired
    @Qualifier("webClientPrestamos")
    private WebClient webClientPrestamos;

    public List<Multa> obtenerMultas() {
        log.info("Iniciando consulta de todas las multas");
        List<Multa> multas = multaRepository.findAll();
        log.info("Consulta completada: {} multas encontradas", multas.size());
        return multas;
    }

    public Multa buscarPorId(Integer id) {
        log.info("Buscando multa por ID: {}", id);
        return multaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Multa con ID {} no encontrada", id);
                    return new ResourceNotFoundException("Multa no encontrada");
                });
    }

    public Multa crearMulta(MultaDTO dto) {
        log.info("Iniciando creacion de multa: prestamoId={}, monto={}", dto.getPrestamoId(), dto.getMonto());

        if (multaRepository.findByPrestamoId(dto.getPrestamoId()).isPresent()) {
            log.warn("Ya existe una multa para el prestamo ID={}", dto.getPrestamoId());
            throw new DuplicateResourceException("Ya existe una multa para este prestamo");
        }

        log.info("Validando prestamo ID={} con microservicio prestamos", dto.getPrestamoId());
        PrestamoDTO prestamo = webClientPrestamos.get()
                .uri("/{id}", dto.getPrestamoId())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> {
                            log.warn("Prestamo ID={} no encontrado en microservicio", dto.getPrestamoId());
                            return Mono.error(new ResourceNotFoundException("Prestamo no encontrado"));
                        })
                .bodyToMono(PrestamoDTO.class)
                .block();
        log.info("Prestamo validado: ID={}", prestamo.getId());

        Multa multa = new Multa();
        multa.setPrestamoId(prestamo.getId());
        multa.setMonto(dto.getMonto());
        multa.setDiasRetraso(dto.getDiasRetraso());
        multa.setPagada(dto.getPagada() != null ? dto.getPagada() : false);

        Multa guardada = multaRepository.save(multa);
        log.info("Multa creada exitosamente: ID={}", guardada.getId());
        return guardada;
    }

    public Multa actualizarMulta(Integer id, MultaDTO dto) {
        log.info("Iniciando actualizacion de multa ID={}", id);
        Multa multa = buscarPorId(id);
        multa.setMonto(dto.getMonto());
        multa.setDiasRetraso(dto.getDiasRetraso());
        multa.setPagada(dto.getPagada());
        Multa actualizada = multaRepository.save(multa);
        log.info("Multa ID={} actualizada exitosamente", id);
        return actualizada;
    }

    public void eliminarMulta(Integer id) {
        log.info("Iniciando eliminacion de multa ID={}", id);
        Multa multa = buscarPorId(id);
        multaRepository.delete(multa);
        log.info("Multa ID={} eliminada exitosamente", id);
    }
}