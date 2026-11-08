package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "catalogo.no-disponible" en messages.properties / messages_en.properties
public class CatalogoNoDisponibleException extends ErrorDeNegocio {

    public CatalogoNoDisponibleException(Throwable causa) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "catalogo.no-disponible", causa);
    }
}
