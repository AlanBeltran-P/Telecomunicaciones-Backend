package com.telecom.usuarios.cliente.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "dni.en-uso" en messages.properties / messages_en.properties
public class DniEnUsoException extends ErrorDeNegocio {

    public DniEnUsoException(String dni) {
        super(HttpStatus.CONFLICT, "dni.en-uso", dni);
    }
}
