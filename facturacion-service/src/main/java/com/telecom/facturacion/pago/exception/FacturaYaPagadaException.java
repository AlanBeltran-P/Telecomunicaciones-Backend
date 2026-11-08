package com.telecom.facturacion.pago.exception;

public class FacturaYaPagadaException extends RuntimeException {

    public FacturaYaPagadaException(Long id) {
        super("La factura " + id + " ya está pagada");
    }
}
