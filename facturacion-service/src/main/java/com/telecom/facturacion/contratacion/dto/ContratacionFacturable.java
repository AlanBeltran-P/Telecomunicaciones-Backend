package com.telecom.facturacion.contratacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo que devuelve contrataciones-service en /api/internos/contrataciones/facturables
public record ContratacionFacturable(
        Long id,
        Long clienteId,
        String planNombre,
        BigDecimal precioMensual,
        LocalDate fechaContratacion,
        // Después de una reactivación, no se facturan los meses anteriores a esta fecha
        LocalDate facturarDesde) {
}
