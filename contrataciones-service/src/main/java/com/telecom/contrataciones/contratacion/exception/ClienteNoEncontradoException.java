package com.telecom.contrataciones.contratacion.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long clienteId) {
        super("No existe un cliente activo con id " + clienteId);
    }
}
