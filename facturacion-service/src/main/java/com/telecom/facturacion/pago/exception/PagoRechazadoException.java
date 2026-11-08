package com.telecom.facturacion.pago.exception;

public class PagoRechazadoException extends RuntimeException {

    public PagoRechazadoException(Long facturaId) {
        super("El pago de la factura " + facturaId + " fue rechazado. Probá con otro medio de pago");
    }
}
