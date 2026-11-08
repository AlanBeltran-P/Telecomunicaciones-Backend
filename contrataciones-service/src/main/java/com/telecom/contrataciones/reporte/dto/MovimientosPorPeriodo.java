package com.telecom.contrataciones.reporte.dto;

// Un punto de la serie: "2026-11-08" si se agrupa por día, "2026-11" si se agrupa por mes
public record MovimientosPorPeriodo(String periodo, long altas, long bajas, long instalaciones) {
}
