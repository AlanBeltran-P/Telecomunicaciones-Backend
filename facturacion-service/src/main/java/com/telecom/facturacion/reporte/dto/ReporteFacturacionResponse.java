package com.telecom.facturacion.reporte.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Montos en pesos. deudaActual y debitosAutomaticosActivos son a hoy; el resto, del período
public record ReporteFacturacionResponse(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal facturado,
        BigDecimal cobrado,
        BigDecimal deudaActual,
        FacturasPorEstado facturasEmitidas,
        List<CobradoPorMedio> cobradoPorMedio,
        long debitosAutomaticosNuevos,
        long debitosAutomaticosActivos,
        List<MontosPorPeriodo> serie) {
}
