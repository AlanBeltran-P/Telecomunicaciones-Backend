package com.telecom.contrataciones.contratacion.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "usuarios.no-disponible" en messages.properties / messages_en.properties
public class UsuariosNoDisponibleException extends ErrorDeNegocio {

    public UsuariosNoDisponibleException(Throwable causa) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "usuarios.no-disponible", causa);
    }
}
