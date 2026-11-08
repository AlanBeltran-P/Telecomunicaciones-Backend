package com.telecom.facturacion.pago.dto;

// referencia: identificador del cobro en el proveedor, para conciliar o reclamar
public record ResultadoDelCobro(boolean aprobado, String referencia) {

    public static ResultadoDelCobro aprobado(String referencia) {
        return new ResultadoDelCobro(true, referencia);
    }

    public static ResultadoDelCobro rechazado() {
        return new ResultadoDelCobro(false, null);
    }
}
