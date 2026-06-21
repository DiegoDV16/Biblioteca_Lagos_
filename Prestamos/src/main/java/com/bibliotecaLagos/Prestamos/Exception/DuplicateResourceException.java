package com.bibliotecaLagos.Prestamos.Exception;

public class DuplicateResourceException
        extends RuntimeException {

    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }
}