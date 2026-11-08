package com.telecom.usuarios.config;

import com.telecom.usuarios.auth.exception.CredencialesInvalidasException;
import com.telecom.usuarios.usuario.exception.EmailEnUsoException;
import com.telecom.usuarios.usuario.exception.OperacionNoPermitidaException;
import com.telecom.usuarios.usuario.exception.UsuarioNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce las excepciones del negocio a respuestas HTTP con un mensaje para el frontend
@RestControllerAdvice
public class ManejoDeErrores {

    // Mismo mensaje sin importar si falló el email o la contraseña
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ProblemDetail credencialesInvalidas(CredencialesInvalidasException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, error.getMessage());
    }

    @ExceptionHandler(EmailEnUsoException.class)
    public ProblemDetail emailEnUso(EmailEnUsoException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ProblemDetail operacionNoPermitida(OperacionNoPermitidaException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ProblemDetail usuarioNoEncontrado(UsuarioNoEncontradoException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }
}
