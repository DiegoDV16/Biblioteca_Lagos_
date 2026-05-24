package com.bibliotecaLagos.Multas.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultaResponseDTO {

    private Integer id;
    private Integer prestamoId;
    private Double monto;
    private Integer diasRetraso;
    private Boolean pagada;
}