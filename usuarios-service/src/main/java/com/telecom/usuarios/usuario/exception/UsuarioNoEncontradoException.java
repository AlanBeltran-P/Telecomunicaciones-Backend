package com.telecom.usuarios.usuario.exception;

import com.telecom.errores.ErrorDeNegocio;
import org.springframework.http.HttpStatus;

// Mensaje: "usuario.no-encontrado" en messages.properties / messages_en.properties
public class UsuarioNoEncontradoException extends ErrorDeNegocio {

    public UsuarioNoEncontradoException(Long id) {
        super(HttpStatus.NOT_FOUND, "usuario.no-encontrado", id);
    }
}
