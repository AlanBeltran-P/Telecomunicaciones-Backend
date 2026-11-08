package com.telecom.facturacion.notificacion.exception;

public class NotificacionNoEncontradaException extends RuntimeException {

    public NotificacionNoEncontradaException(Long id) {
        super("No existe la notificación " + id);
    }
}
