package com.telecom.facturacion.reporte.dto;

import java.math.BigDecimal;

// Un punto de la serie: "2026-11-08" si se agrupa por día, "2026-11" si se agrupa por mes
public record MontosPorPeriodo(String periodo, BigDecimal facturado, BigDecimal cobrado) {
}
