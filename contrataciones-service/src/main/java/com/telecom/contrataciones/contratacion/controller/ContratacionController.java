package com.telecom.contrataciones.contratacion.controller;

import com.telecom.contrataciones.contratacion.dto.ContratacionResponse;
import com.telecom.contrataciones.contratacion.dto.ContratarPlanRequest;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.mapper.ContratacionMapper;
import com.telecom.contrataciones.contratacion.service.ContratacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contrataciones")
public class ContratacionController {

    private final ContratacionService contratacionService;
    private final ContratacionMapper contratacionMapper;

    public ContratacionController(ContratacionService contratacionService, ContratacionMapper contratacionMapper) {
        this.contratacionService = contratacionService;
        this.contratacionMapper = contratacionMapper;
    }

    // El cliente se toma del token, nunca del cuerpo del pedido
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacionResponse contratar(
            @Valid @RequestBody ContratarPlanRequest request,
            @AuthenticationPrincipal Jwt cliente) {
        Contratacion contratacion = contratacionService.contratar(cliente.getSubject(), request);
        return contratacionMapper.aContratacionResponse(contratacion);
    }
}
