package com.bibliotecaLagos.Multas.Controller;
import com.bibliotecaLagos.Multas.DTO.MultaResponseDTO;
import com.bibliotecaLagos.Multas.Model.Multas;
import com.bibliotecaLagos.Multas.Service.MultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/multas")
@RequiredArgsConstructor
public class MultasController {

    private final MultaService multaService;


    @PostMapping("/generar/{prestamoId}")
    public MultaResponseDTO generar(@PathVariable Integer prestamoId) {
            Multas multa = multaService.generarMulta(prestamoId);

             return MultaResponseDTO.builder()
            .id(multa.getId())
            .prestamoId(multa.getPrestamoId())
            .monto(multa.getMonto())
            .diasRetraso(multa.getDiasRetraso())
            .pagada(multa.getPagada())
            .build();
    }




    @GetMapping
    public List<Multas> listar() {
        return multaService.listar();
    }

    @PutMapping("/pagar/{id}")
    public Multas pagar(@PathVariable Integer id) {
        return multaService.pagar(id);
    }
}