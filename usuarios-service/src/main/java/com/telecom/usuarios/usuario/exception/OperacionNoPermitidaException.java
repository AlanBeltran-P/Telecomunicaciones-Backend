package com.telecom.usuarios.usuario.exception;

public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String motivo) {
        super(motivo);
    }
}
