package com.telecom.contrataciones.instalacion.exception;

public class TecnicoNoEncontradoException extends RuntimeException {

    public TecnicoNoEncontradoException(Long tecnicoId) {
        super("No existe un técnico activo con id " + tecnicoId);
    }
}
