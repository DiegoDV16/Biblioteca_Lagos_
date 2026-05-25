package com.bibliotecaLagos.Reserva.Exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {

        super(mensaje);
    }
}