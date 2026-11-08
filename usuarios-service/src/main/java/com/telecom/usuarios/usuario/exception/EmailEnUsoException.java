package com.telecom.usuarios.usuario.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "email.en-uso" en messages.properties / messages_en.properties
public class EmailEnUsoException extends ErrorDeNegocio {

    public EmailEnUsoException(String email) {
        super(HttpStatus.CONFLICT, "email.en-uso", email);
    }
}
