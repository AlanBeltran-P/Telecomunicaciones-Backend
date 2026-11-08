package com.telecom.usuarios.reporte.dto;

import java.time.LocalDate;
import java.util.List;

// clientesNuevos y altasDeClientes son del período; clientesActivos y personalActivo, de hoy
public record ReporteUsuariosResponse(
        LocalDate desde,
        LocalDate hasta,
        long clientesNuevos,
        long clientesActivos,
        PersonalPorRol personalActivo,
        List<CantidadPorPeriodo> altasDeClientes) {
}
