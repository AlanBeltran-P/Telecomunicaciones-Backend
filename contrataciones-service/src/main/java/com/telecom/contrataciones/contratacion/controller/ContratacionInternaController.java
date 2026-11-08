package com.telecom.contrataciones.contratacion.controller;

import com.telecom.contrataciones.contratacion.dto.ContratacionFacturable;
import com.telecom.contrataciones.contratacion.dto.ContratacionResponse;
import com.telecom.contrataciones.contratacion.mapper.ContratacionMapper;
import com.telecom.contrataciones.contratacion.service.ContratacionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Consultas para otros microservicios (rol SISTEMA); el Gateway no expone estas rutas
@RestController
@RequestMapping("/api/internos/contrataciones")
public class ContratacionInternaController {

    private final ContratacionService contratacionService;
    private final ContratacionMapper contratacionMapper;

    public ContratacionInternaController(ContratacionService contratacionService, ContratacionMapper contratacionMapper) {
        this.contratacionService = contratacionService;
        this.contratacionMapper = contratacionMapper;
    }

    @PutMapping("/{id}/suspension")
    public ContratacionResponse suspenderPorFaltaDePago(@PathVariable Long id) {
        return contratacionMapper.aContratacionResponse(contratacionService.suspenderPorFaltaDePago(id));
    }

    @GetMapping("/facturables")
    public List<ContratacionFacturable> listarFacturables() {
        return contratacionMapper.aContratacionesFacturables(contratacionService.listarFacturables());
    }
}
