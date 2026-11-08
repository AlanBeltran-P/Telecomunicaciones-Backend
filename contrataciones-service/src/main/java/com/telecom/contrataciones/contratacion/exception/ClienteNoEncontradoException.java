package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "cliente.no-encontrado" en messages.properties / messages_en.properties
public class ClienteNoEncontradoException extends ErrorDeNegocio {

    public ClienteNoEncontradoException(Long clienteId) {
        super(HttpStatus.NOT_FOUND, "cliente.no-encontrado", clienteId);
    }
}
