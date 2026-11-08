package com.telecom.contrataciones.instalacion.exception;

public class InstalacionNoPendienteException extends RuntimeException {

    public InstalacionNoPendienteException(Long contratacionId) {
        super("La contratación " + contratacionId + " no tiene una instalación pendiente");
    }
}
