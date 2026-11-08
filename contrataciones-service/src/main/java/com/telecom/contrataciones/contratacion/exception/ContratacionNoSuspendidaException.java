package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "contratacion.no-suspendida" en messages.properties / messages_en.properties
public class ContratacionNoSuspendidaException extends ErrorDeNegocio {

    public ContratacionNoSuspendidaException(Long id) {
        super(HttpStatus.CONFLICT, "contratacion.no-suspendida", id);
    }
}
