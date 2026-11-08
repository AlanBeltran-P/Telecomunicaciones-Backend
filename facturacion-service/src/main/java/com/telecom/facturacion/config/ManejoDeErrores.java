package com.telecom.facturacion.config;

import com.telecom.facturacion.debito.exception.DebitoNoActivoException;
import com.telecom.facturacion.factura.exception.FacturaNoEncontradaException;
import com.telecom.facturacion.notificacion.exception.NotificacionNoEncontradaException;
import com.telecom.facturacion.pago.exception.FacturaYaPagadaException;
import com.telecom.facturacion.pago.exception.PagoRechazadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce las excepciones del negocio a respuestas HTTP con un mensaje para el frontend
@RestControllerAdvice
public class ManejoDeErrores {

    @ExceptionHandler({
            FacturaNoEncontradaException.class,
            DebitoNoActivoException.class,
            NotificacionNoEncontradaException.class})
    public ProblemDetail noEncontrada(RuntimeException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(FacturaYaPagadaException.class)
    public ProblemDetail yaPagada(FacturaYaPagadaException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    // 402 Payment Required: el proveedor rechazó el cobro
    @ExceptionHandler(PagoRechazadoException.class)
    public ProblemDetail pagoRechazado(PagoRechazadoException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, error.getMessage());
    }
}
