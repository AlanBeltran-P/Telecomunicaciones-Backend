package com.telecom.contrataciones.contratacion.exception;

public class ContratacionNoSuspendidaException extends RuntimeException {

    public ContratacionNoSuspendidaException(Long id) {
        super("La contratación " + id + " no está dada de baja por falta de pago");
    }
}
