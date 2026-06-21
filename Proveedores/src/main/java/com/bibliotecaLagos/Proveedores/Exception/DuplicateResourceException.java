package com.bibliotecaLagos.Proveedores.Exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }
}