package com.telecom.contrataciones.instalacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "instalacion.de-otro-tecnico" en messages.properties / messages_en.properties
public class InstalacionDeOtroTecnicoException extends ErrorDeNegocio {

    public InstalacionDeOtroTecnicoException(Long contratacionId) {
        super(HttpStatus.FORBIDDEN, "instalacion.de-otro-tecnico", contratacionId);
    }
}
