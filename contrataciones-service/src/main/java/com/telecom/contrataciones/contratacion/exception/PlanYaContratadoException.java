package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "plan.ya-contratado" en messages.properties / messages_en.properties
public class PlanYaContratadoException extends ErrorDeNegocio {

    public PlanYaContratadoException(String planNombre) {
        super(HttpStatus.CONFLICT, "plan.ya-contratado", planNombre);
    }
}
