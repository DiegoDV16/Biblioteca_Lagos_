package com.bibliotecaLagos.Multas.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bibliotecaLagos.Multas.Client.PrestamoClient;
import com.bibliotecaLagos.Multas.Model.Multas;
import com.bibliotecaLagos.Multas.Repository.MultasRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MultasService {

    private final MultasRepository multaRepository;
    private final PrestamoClient prestamoClient;

    private final double MULTA_POR_DIA = 1000; // CLP

    public Multas generarMulta(Integer prestamoId) {

        var prestamo = prestamoClient.obtenerPrestamo(prestamoId);

        LocalDate fechaDevolucion = LocalDate.parse((String) prestamo.get("fechaDevolucion"));
        LocalDate fechaEntrega = LocalDate.parse((String) prestamo.get("fechaEntrega"));

        if (fechaEntrega == null || !fechaEntrega.isAfter(fechaDevolucion)) {
            throw new RuntimeException("No hay retraso");
        }

        long diasRetraso = ChronoUnit.DAYS.between(fechaDevolucion, fechaEntrega);

        double monto = diasRetraso * MULTA_POR_DIA;

        Multas multa = Multas.builder()
                .prestamoId(prestamoId)
                .diasRetraso((int) diasRetraso)
                .monto(monto)
                .pagada(false)
                .build();

        return multaRepository.save(multa);
    }

    public List<Multas> listar() {
        return multaRepository.findAll();
    }

    public Multas pagar(Integer id) {
        Multas multa = multaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa no encontrada"));

        multa.setPagada(true);
        return multaRepository.save(multa);
    }
}