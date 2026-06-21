package com.bibliotecaLagos.Multas.Service;

import java.util.List;
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

    @Autowired
    private MultaRepository multaRepository;

    @Autowired
    @Qualifier("webClientPrestamos")
    private WebClient webClientPrestamos;

    public List<Multa> obtenerMultas() {

        return multaRepository.findAll();
    }

    public Multa buscarPorId(Integer id) {

        return multaRepository.findById(id)
        .orElseThrow(() ->
        new ResourceNotFoundException(
                "Multa no encontrada"
        ));
    }

    public Multa crearMulta(MultaDTO dto) {

        if(multaRepository.findByPrestamoId(dto.getPrestamoId()).isPresent()) {

            throw new DuplicateResourceException(
             "Ya existe una multa para este prestamo"
            );
        }

        PrestamoDTO prestamo = webClientPrestamos.get()
        .uri("/{id}", dto.getPrestamoId())
        .retrieve()
        .onStatus(
        HttpStatusCode::is4xxClientError,
        response -> Mono.error(
        new ResourceNotFoundException(
                "Prestamo no encontrado"
        )))
        .bodyToMono(PrestamoDTO.class)
        .block();

        Multa multa = new Multa();

        multa.setPrestamoId(prestamo.getId());
        multa.setMonto(dto.getMonto());
        multa.setDiasRetraso(dto.getDiasRetraso());
        multa.setPagada(dto.getPagada() != null
        ? dto.getPagada()
        : false);

        return multaRepository.save(multa);
    }

    public Multa actualizarMulta(Integer id, MultaDTO dto) {

        Multa multa = buscarPorId(id);
        multa.setMonto(dto.getMonto());
        multa.setDiasRetraso(dto.getDiasRetraso());
        multa.setPagada(dto.getPagada());

        return multaRepository.save(multa);
    }

    public void eliminarMulta(Integer id) {

        Multa multa = buscarPorId(id);
        multaRepository.delete(multa);
    }
}