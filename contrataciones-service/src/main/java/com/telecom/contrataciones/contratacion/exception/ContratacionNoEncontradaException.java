package com.telecom.contrataciones.contratacion.exception;

public class ContratacionNoEncontradaException extends RuntimeException {

    public ContratacionNoEncontradaException(Long id) {
        super("No existe la contratación " + id);
    }
}
