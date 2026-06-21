package com.bibliotecaLagos.Usuarios.Security;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class AuthService {

    private static final String SECRET_KEY = "mi_clave_secreta";
    private static final String USER = "admin";
    private static final String PASS = "1234";

    public ResponseEntity<ResponseDTO> login(LoginJWTDTO login) {
        if (!USER.equals(login.getUsuario()) || !PASS.equals(login.getPassword())) {
            return ResponseEntity.status(401).body(new ResponseDTO("Credenciales invalidas", null));
        }

        String token = JWT.create()
                .withSubject(login.getUsuario())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.HMAC256(SECRET_KEY));

        return ResponseEntity.ok(new ResponseDTO("Inicio de sesion exitoso", token));
    }
}
