package com.telecom.facturacion.debito.exception;

public class DebitoNoActivoException extends RuntimeException {

    public DebitoNoActivoException() {
        super("No tenés el débito automático activado");
    }
}
