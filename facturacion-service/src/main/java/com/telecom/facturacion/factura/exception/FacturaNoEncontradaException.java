package com.telecom.facturacion.factura.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "factura.no-encontrada" en messages.properties / messages_en.properties
public class FacturaNoEncontradaException extends ErrorDeNegocio {

    public FacturaNoEncontradaException(Long id) {
        super(HttpStatus.NOT_FOUND, "factura.no-encontrada", id);
    }
}
