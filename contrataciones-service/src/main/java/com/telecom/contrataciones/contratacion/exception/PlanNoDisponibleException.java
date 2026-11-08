package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "plan.no-disponible" en messages.properties / messages_en.properties
public class PlanNoDisponibleException extends ErrorDeNegocio {

    public PlanNoDisponibleException(Long planId) {
        super(HttpStatus.NOT_FOUND, "plan.no-disponible", planId);
    }
}
