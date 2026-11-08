package com.telecom.usuarios.reporte.controller;

import com.telecom.usuarios.reporte.dto.Agrupacion;
import com.telecom.usuarios.reporte.dto.ReporteUsuariosResponse;
import com.telecom.usuarios.reporte.service.ReporteUsuariosService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Solo el administrador (lo cubre /api/usuarios/** en SecurityConfig).
// Un día, un mes o un año es el mismo pedido con otro rango: ?desde=2026-11-01&hasta=2026-11-30
@RestController
@RequestMapping("/api/usuarios/reporte")
public class ReporteUsuariosController {

    private final ReporteUsuariosService reporteService;

    public ReporteUsuariosController(ReporteUsuariosService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ReporteUsuariosResponse generar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "DIA") Agrupacion agrupacion) {
        return reporteService.generar(desde, hasta, agrupacion);
    }
}
