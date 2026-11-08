package com.telecom.usuarios.auth.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "credenciales.invalidas" en messages.properties / messages_en.properties
public class CredencialesInvalidasException extends ErrorDeNegocio {

    public CredencialesInvalidasException() {
        super(HttpStatus.UNAUTHORIZED, "credenciales.invalidas");
    }
}
