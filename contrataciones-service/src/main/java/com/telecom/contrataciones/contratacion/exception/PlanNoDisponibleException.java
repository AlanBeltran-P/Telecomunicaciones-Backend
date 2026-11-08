package com.telecom.contrataciones.contratacion.exception;

public class PlanNoDisponibleException extends RuntimeException {

    public PlanNoDisponibleException(Long planId) {
        super("El plan " + planId + " no existe o ya no está disponible");
    }
}
