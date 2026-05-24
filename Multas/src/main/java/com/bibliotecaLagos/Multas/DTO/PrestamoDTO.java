package com.bibliotecaLagos.Multas.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDTO {

    private Integer id;
    private String fechaDevolucion;
    private String fechaEntrega;
}