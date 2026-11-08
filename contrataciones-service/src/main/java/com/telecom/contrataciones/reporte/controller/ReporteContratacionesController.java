package com.telecom.contrataciones.reporte.controller;

import com.telecom.contrataciones.reporte.dto.Agrupacion;
import com.telecom.contrataciones.reporte.dto.ReporteContratacionesResponse;
import com.telecom.contrataciones.reporte.service.ReporteContratacionesService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Solo el administrador (ver SecurityConfig).
// Un día, un mes o un año es el mismo pedido con otro rango: ?desde=2026-11-01&hasta=2026-11-30
@RestController
@RequestMapping("/api/contrataciones/reporte")
public class ReporteContratacionesController {

    private final ReporteContratacionesService reporteService;

    public ReporteContratacionesController(ReporteContratacionesService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ReporteContratacionesResponse generar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "DIA") Agrupacion agrupacion) {
        return reporteService.generar(desde, hasta, agrupacion);
    }
}
