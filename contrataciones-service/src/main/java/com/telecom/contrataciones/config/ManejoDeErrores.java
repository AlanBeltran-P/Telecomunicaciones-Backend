package com.telecom.contrataciones.config;

import com.telecom.contrataciones.contratacion.exception.CatalogoNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.ClienteNoEncontradoException;
import com.telecom.contrataciones.contratacion.exception.ContratacionNoEncontradaException;
import com.telecom.contrataciones.contratacion.exception.ContratacionYaDadaDeBajaException;
import com.telecom.contrataciones.contratacion.exception.PlanNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanYaContratadoException;
import com.telecom.contrataciones.contratacion.exception.UsuariosNoDisponibleException;
import com.telecom.contrataciones.instalacion.exception.InstalacionDeOtroTecnicoException;
import com.telecom.contrataciones.instalacion.exception.InstalacionNoPendienteException;
import com.telecom.contrataciones.instalacion.exception.TecnicoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce las excepciones del negocio a respuestas HTTP con un mensaje para el frontend
@RestControllerAdvice
public class ManejoDeErrores {

    @ExceptionHandler({
            PlanNoDisponibleException.class,
            ClienteNoEncontradoException.class,
            ContratacionNoEncontradaException.class,
            TecnicoNoEncontradoException.class})
    public ProblemDetail noEncontrado(RuntimeException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler({
            PlanYaContratadoException.class,
            ContratacionYaDadaDeBajaException.class,
            InstalacionNoPendienteException.class})
    public ProblemDetail conflicto(RuntimeException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(InstalacionDeOtroTecnicoException.class)
    public ProblemDetail prohibido(InstalacionDeOtroTecnicoException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, error.getMessage());
    }

    // Otro microservicio no respondió
    @ExceptionHandler({CatalogoNoDisponibleException.class, UsuariosNoDisponibleException.class})
    public ProblemDetail servicioNoDisponible(RuntimeException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, error.getMessage());
    }
}
