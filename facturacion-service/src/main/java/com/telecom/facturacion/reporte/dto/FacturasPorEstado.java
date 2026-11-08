package com.telecom.facturacion.reporte.dto;

// Estado actual de las facturas emitidas en el período
public record FacturasPorEstado(long pendientes, long pagadas, long vencidas) {
}
