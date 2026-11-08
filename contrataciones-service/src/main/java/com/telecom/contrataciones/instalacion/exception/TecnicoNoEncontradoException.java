package com.telecom.contrataciones.instalacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "tecnico.no-encontrado" en messages.properties / messages_en.properties
public class TecnicoNoEncontradoException extends ErrorDeNegocio {

    public TecnicoNoEncontradoException(Long tecnicoId) {
        super(HttpStatus.NOT_FOUND, "tecnico.no-encontrado", tecnicoId);
    }
}
