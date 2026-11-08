package com.telecom.contrataciones.contratacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo que necesita facturacion-service para generar las facturas de cada mes
public record ContratacionFacturable(
        Long id,
        Long clienteId,
        String planNombre,
        BigDecimal precioMensual,
        LocalDate fechaContratacion) {
}
