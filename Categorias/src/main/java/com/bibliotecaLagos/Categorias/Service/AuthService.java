package com.bibliotecaLagos.Categorias.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bibliotecaLagos.Categorias.DTO.LoginJWTDTO;
import com.bibliotecaLagos.Categorias.DTO.ResponseDTO;

@Service
public class AuthService {

    private String secretKey = "mi_clave_secreta";
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public ResponseDTO validar(LoginJWTDTO requestJwtDTO) {
        ResponseDTO responseDTO = new ResponseDTO();

        if ("admin".equals(requestJwtDTO.getUsername())
                && "1234".equals(requestJwtDTO.getPassword())) {
            try {
                log.info("El usuario {} se ha logueado", requestJwtDTO.getUsername());

                Algorithm algorithm = Algorithm.HMAC256(secretKey);
                long expTime = System.currentTimeMillis() + (15 * 60 * 1000);

                String token = JWT.create()
                        .withSubject(requestJwtDTO.getUsername())
                        .withExpiresAt(new java.util.Date(expTime))
                        .withClaim("roles", List.of("ROLE_ADMIN", "ROLE_USER"))
                        .sign(algorithm);

                responseDTO.setRepuestaInt(0);
                responseDTO.setToken(token);
                return responseDTO;
            } catch (Exception e) {
                responseDTO.setRepuestaInt(1);
                responseDTO.setToken("error al generar token");
                return responseDTO;
            }
        } else {
            responseDTO.setRepuestaInt(2);
            responseDTO.setToken("credenciales invalidas");
            return responseDTO;
        }
    }
}
