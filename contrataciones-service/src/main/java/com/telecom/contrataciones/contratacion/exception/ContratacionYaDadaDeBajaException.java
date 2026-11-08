package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "contratacion.ya-dada-de-baja" en messages.properties / messages_en.properties
public class ContratacionYaDadaDeBajaException extends ErrorDeNegocio {

    public ContratacionYaDadaDeBajaException(Long id) {
        super(HttpStatus.CONFLICT, "contratacion.ya-dada-de-baja", id);
    }
}
