package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "contratacion.no-encontrada" en messages.properties / messages_en.properties
public class ContratacionNoEncontradaException extends ErrorDeNegocio {

    public ContratacionNoEncontradaException(Long id) {
        super(HttpStatus.NOT_FOUND, "contratacion.no-encontrada", id);
    }
}
