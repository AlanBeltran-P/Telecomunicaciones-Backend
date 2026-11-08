package com.telecom.usuarios.reporte.dto;

// Un punto de la serie: "2026-11-08" si se agrupa por día, "2026-11" si se agrupa por mes
public record CantidadPorPeriodo(String periodo, long cantidad) {
}
