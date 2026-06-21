package com.bibliotecaLagos.Usuarios.Exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {

        super(mensaje);
    }
}