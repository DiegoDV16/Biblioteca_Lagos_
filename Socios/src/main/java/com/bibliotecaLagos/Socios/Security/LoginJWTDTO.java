package com.bibliotecaLagos.Socios.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginJWTDTO {
    private String usuario;
    private String password;
}
