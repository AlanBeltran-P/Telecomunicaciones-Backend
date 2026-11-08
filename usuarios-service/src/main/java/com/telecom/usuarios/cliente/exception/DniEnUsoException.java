package com.telecom.usuarios.cliente.exception;

public class DniEnUsoException extends RuntimeException {

    public DniEnUsoException(String dni) {
        super("El DNI " + dni + " ya está registrado");
    }
}
