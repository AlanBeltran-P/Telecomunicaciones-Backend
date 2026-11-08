package com.telecom.facturacion.pago.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "pago.rechazado" en messages.properties / messages_en.properties
public class PagoRechazadoException extends ErrorDeNegocio {

    public PagoRechazadoException(Long facturaId) {
        super(HttpStatus.PAYMENT_REQUIRED, "pago.rechazado", facturaId);
    }
}
