package com.telecom.contrataciones.instalacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "instalacion.no-pendiente" en messages.properties / messages_en.properties
public class InstalacionNoPendienteException extends ErrorDeNegocio {

    public InstalacionNoPendienteException(Long contratacionId) {
        super(HttpStatus.CONFLICT, "instalacion.no-pendiente", contratacionId);
    }
}
