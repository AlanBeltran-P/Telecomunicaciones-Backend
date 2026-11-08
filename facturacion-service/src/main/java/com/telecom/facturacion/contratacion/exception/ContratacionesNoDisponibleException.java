package com.telecom.facturacion.contratacion.exception;

public class ContratacionesNoDisponibleException extends RuntimeException {

    public ContratacionesNoDisponibleException(Throwable causa) {
        super("No se pudo consultar las contrataciones", causa);
    }
}
