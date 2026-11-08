package com.telecom.contrataciones.config;

import com.telecom.contrataciones.contratacion.exception.CatalogoNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanYaContratadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce las excepciones del negocio a respuestas HTTP con un mensaje para el frontend
@RestControllerAdvice
public class ManejoDeErrores {

    @ExceptionHandler(PlanNoDisponibleException.class)
    public ProblemDetail planNoDisponible(PlanNoDisponibleException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(PlanYaContratadoException.class)
    public ProblemDetail planYaContratado(PlanYaContratadoException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(CatalogoNoDisponibleException.class)
    public ProblemDetail catalogoNoDisponible(CatalogoNoDisponibleException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, error.getMessage());
    }
}
