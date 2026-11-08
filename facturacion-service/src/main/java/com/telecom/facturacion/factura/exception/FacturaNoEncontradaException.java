package com.telecom.facturacion.factura.exception;

public class FacturaNoEncontradaException extends RuntimeException {

    public FacturaNoEncontradaException(Long id) {
        super("No existe la factura " + id);
    }
}
