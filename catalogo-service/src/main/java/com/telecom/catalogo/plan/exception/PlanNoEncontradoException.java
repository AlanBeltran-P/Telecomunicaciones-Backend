package com.telecom.catalogo.plan.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "plan.no-encontrado" en messages.properties / messages_en.properties
public class PlanNoEncontradoException extends ErrorDeNegocio {

    public PlanNoEncontradoException(Long id) {
        super(HttpStatus.NOT_FOUND, "plan.no-encontrado", id);
    }
}
