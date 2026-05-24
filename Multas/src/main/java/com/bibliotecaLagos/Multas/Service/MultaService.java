package com.bibliotecaLagos.Multas.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bibliotecaLagos.Multas.Client.PrestamoClient;
import com.bibliotecaLagos.Multas.DTO.PrestamoDTO;
import com.bibliotecaLagos.Multas.Model.Multas;
import com.bibliotecaLagos.Multas.Repository.MultasRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MultaService {

    private final MultasRepository multasRepository;
    private final PrestamoClient prestamoClient;

    private static final double MULTA_POR_DIA = 1000;
    
    public Multas generarMulta(Integer prestamoId) {

        PrestamoDTO prestamo = (PrestamoDTO) prestamoClient.obtenerPrestamo(prestamoId);

        if (prestamo == null) {
            throw new RuntimeException("El préstamo no existe");
        }
    
        if (prestamo.getFechaEntrega() == null) {
            throw new RuntimeException("El libro aún no ha sido entregado");
        }

        LocalDate fechaDevolucion = LocalDate.parse(prestamo.getFechaDevolucion());
        LocalDate fechaEntrega = LocalDate.parse(prestamo.getFechaEntrega());

        if (!fechaEntrega.isAfter(fechaDevolucion)) {
            throw new RuntimeException("No hay retraso, no se genera multa");
        }

        long diasRetraso = ChronoUnit.DAYS.between(fechaDevolucion, fechaEntrega);

        double monto = diasRetraso * MULTA_POR_DIA;

        if (multasRepository.findByPrestamoId(prestamoId).isPresent()) {
            throw new RuntimeException("Ya existe una multa para este préstamo");
        }

        Multas multas = Multas.builder()
                .prestamoId(prestamoId)
                .diasRetraso((int) diasRetraso)
                .monto(monto)
                .pagada(false)
                .build();

        return multasRepository.save(multas);
    }

    public List<Multas> listar() {
        return multasRepository.findAll();
    }

    public Multas pagar(Integer id) {

        Multas multas = multasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa no encontrada"));

        multas.setPagada(true);

        return multasRepository.save(multas);
    }
}