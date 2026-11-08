package com.telecom.contrataciones.contratacion.entity;

public enum EstadoContratacion {
    PENDIENTE_INSTALACION,
    ACTIVA,
    // Quitada por el cliente o un empleado
    BAJA,
    // Dada de baja automáticamente por una factura impaga: un empleado la puede reactivar
    SUSPENDIDA_POR_FALTA_DE_PAGO
}
