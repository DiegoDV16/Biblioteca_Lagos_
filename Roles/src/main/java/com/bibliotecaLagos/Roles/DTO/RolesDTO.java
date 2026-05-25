package com.bibliotecaLagos.Roles.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesDTO {
    
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 50, message = "El rol debe tener entre 3 y 50 caracteres")
    private String nombre;
}