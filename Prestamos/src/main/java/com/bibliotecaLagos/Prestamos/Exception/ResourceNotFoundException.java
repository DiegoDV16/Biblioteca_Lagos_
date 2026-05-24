package com.bibliotecaLagos.Prestamos.Exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}