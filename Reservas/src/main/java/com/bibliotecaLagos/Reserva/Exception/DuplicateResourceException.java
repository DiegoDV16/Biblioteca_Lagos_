package com.bibliotecaLagos.Reserva.Exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {

        super(mensaje);
    }
}