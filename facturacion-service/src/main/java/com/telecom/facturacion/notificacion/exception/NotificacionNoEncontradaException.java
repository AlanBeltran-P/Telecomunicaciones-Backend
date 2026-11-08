package com.telecom.facturacion.notificacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "notificacion.no-encontrada" en messages.properties / messages_en.properties
public class NotificacionNoEncontradaException extends ErrorDeNegocio {

    public NotificacionNoEncontradaException(Long id) {
        super(HttpStatus.NOT_FOUND, "notificacion.no-encontrada", id);
    }
}
