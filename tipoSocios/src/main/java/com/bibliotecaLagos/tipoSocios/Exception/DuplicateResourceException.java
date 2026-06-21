package com.bibliotecaLagos.tipoSocios.Exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {

        super(mensaje);
    }
}