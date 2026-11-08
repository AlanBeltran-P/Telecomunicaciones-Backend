package com.telecom.errores;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce los errores al idioma del header Accept-Language (español si no viene)
@RestControllerAdvice
public class ManejoDeErroresDeNegocio {

    private final MessageSource mensajes;

    public ManejoDeErroresDeNegocio(MessageSource mensajes) {
        this.mensajes = mensajes;
    }

    @ExceptionHandler(ErrorDeNegocio.class)
    public ProblemDetail errorDeNegocio(ErrorDeNegocio error, Locale idioma) {
        String detalle = mensajes.getMessage(error.getCodigo(), error.getArgumentos(), error.getCodigo(), idioma);
        return ProblemDetail.forStatusAndDetail(error.getEstado(), detalle);
    }

    // Además del mensaje general, devuelve "errores": un mensaje por cada campo inválido
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail datosInvalidos(MethodArgumentNotValidException error, Locale idioma) {
        String detalle = mensajes.getMessage("datos.invalidos", null, "Datos inválidos", idioma);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalle);
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError campo : error.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(campo.getField(), campo.getDefaultMessage());
        }
        problema.setProperty("errores", errores);
        return problema;
    }
}
