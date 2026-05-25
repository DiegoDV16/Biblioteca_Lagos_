package com.bibliotecaLagos.Roles.Exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {

        super(mensaje);
    }
}