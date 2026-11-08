package com.telecom.usuarios.usuario.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Varias reglas distintas: cada una tiene su propio código en messages.properties
public class OperacionNoPermitidaException extends ErrorDeNegocio {

    public OperacionNoPermitidaException(String codigo, Object... argumentos) {
        super(HttpStatus.BAD_REQUEST, codigo, argumentos);
    }
}
