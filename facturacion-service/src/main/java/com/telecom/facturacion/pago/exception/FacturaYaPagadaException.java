package com.telecom.facturacion.pago.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "factura.ya-pagada" en messages.properties / messages_en.properties
public class FacturaYaPagadaException extends ErrorDeNegocio {

    public FacturaYaPagadaException(Long id) {
        super(HttpStatus.CONFLICT, "factura.ya-pagada", id);
    }
}
