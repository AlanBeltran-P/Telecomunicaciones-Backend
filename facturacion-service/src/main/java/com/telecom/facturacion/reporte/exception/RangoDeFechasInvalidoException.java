package com.telecom.facturacion.reporte.exception;

import com.telecom.errores.ErrorDeNegocio;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class RangoDeFechasInvalidoException extends ErrorDeNegocio {

    public RangoDeFechasInvalidoException(LocalDate desde, LocalDate hasta) {
        super(HttpStatus.BAD_REQUEST, "reporte.rango-invalido", desde, hasta);
    }
}
