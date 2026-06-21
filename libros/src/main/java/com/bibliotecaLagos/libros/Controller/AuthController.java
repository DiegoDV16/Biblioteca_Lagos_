package com.bibliotecaLagos.libros.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecaLagos.libros.DTO.LoginJWTDTO;
import com.bibliotecaLagos.libros.DTO.ResponseDTO;
import com.bibliotecaLagos.libros.Service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginJWTDTO request) {
        ResponseDTO response = authService.validar(request);
        switch (response.getRepuestaInt()) {
            case 0:
                return ResponseEntity.ok(Map.of("token", response.getToken()));
            case 1:
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response.getToken());
            case 2:
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response.getToken());
            default:
                break;
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("error no capturado");
    }
}
