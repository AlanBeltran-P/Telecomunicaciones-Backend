package com.telecom.usuarios.cliente.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "cliente.no-encontrado" en messages.properties / messages_en.properties
public class ClienteNoEncontradoException extends ErrorDeNegocio {

    public ClienteNoEncontradoException(Long id) {
        super(HttpStatus.NOT_FOUND, "cliente.no-encontrado", id);
    }
}
