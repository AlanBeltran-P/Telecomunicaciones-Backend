package com.telecom.usuarios.usuario;

public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String motivo) {
        super(motivo);
    }
}
