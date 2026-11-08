package com.telecom.contrataciones.reporte.dto;

import java.time.LocalDate;
import java.util.List;

// Todo es del período, salvo instalacionesPendientes, que es a hoy
public record ReporteContratacionesResponse(
        LocalDate desde,
        LocalDate hasta,
        long contratacionesNuevas,
        long bajas,
        long suspendidas,
        long reactivadas,
        long instalacionesRealizadas,
        long instalacionesPendientes,
        List<CantidadPorNombre> contratacionesPorPlan,
        List<CantidadPorNombre> instalacionesPorTecnico,
        List<MovimientosPorPeriodo> serie) {
}
