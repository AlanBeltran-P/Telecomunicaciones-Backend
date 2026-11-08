package com.telecom.facturacion.debito.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "debito.no-activo" en messages.properties / messages_en.properties
public class DebitoNoActivoException extends ErrorDeNegocio {

    public DebitoNoActivoException() {
        super(HttpStatus.NOT_FOUND, "debito.no-activo");
    }
}
