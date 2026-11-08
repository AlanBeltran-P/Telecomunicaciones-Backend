package com.telecom.usuarios.reporte.service;

import com.telecom.usuarios.reporte.dto.Agrupacion;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

// Arma las claves de los períodos de una serie ("2026-11-08" o "2026-11")
final class Periodos {

    private Periodos() {
        // Solo métodos estáticos
    }

    static String claveDe(LocalDate fecha, Agrupacion agrupacion) {
        return agrupacion == Agrupacion.MES ? YearMonth.from(fecha).toString() : fecha.toString();
    }

    // Todos los períodos del rango, en orden, para que la serie no tenga huecos
    static List<String> entre(LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        List<String> claves = new ArrayList<>();
        LocalDate fecha = agrupacion == Agrupacion.MES ? desde.withDayOfMonth(1) : desde;
        while (!fecha.isAfter(hasta)) {
            claves.add(claveDe(fecha, agrupacion));
            fecha = agrupacion == Agrupacion.MES ? fecha.plusMonths(1) : fecha.plusDays(1);
        }
        return claves;
    }
}
