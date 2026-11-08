package com.telecom.contrataciones.contratacion.exception;

public class PlanYaContratadoException extends RuntimeException {

    public PlanYaContratadoException(String planNombre) {
        super("Ya tenés contratado el plan " + planNombre);
    }
}
