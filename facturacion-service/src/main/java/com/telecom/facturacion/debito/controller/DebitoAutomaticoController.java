package com.telecom.facturacion.debito.controller;

import com.telecom.facturacion.debito.dto.ActivarDebitoRequest;
import com.telecom.facturacion.debito.dto.DebitoAutomaticoResponse;
import com.telecom.facturacion.debito.mapper.DebitoAutomaticoMapper;
import com.telecom.facturacion.debito.service.DebitoAutomaticoService;
import com.telecom.seguridad.SeguridadJwt;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Débito automático del cliente que inició sesión (su id sale del token)
@RestController
@RequestMapping("/api/facturas/debito-automatico")
public class DebitoAutomaticoController {

    private final DebitoAutomaticoService debitoService;
    private final DebitoAutomaticoMapper debitoMapper;

    public DebitoAutomaticoController(DebitoAutomaticoService debitoService, DebitoAutomaticoMapper debitoMapper) {
        this.debitoService = debitoService;
        this.debitoMapper = debitoMapper;
    }

    @GetMapping
    public DebitoAutomaticoResponse consultar(@AuthenticationPrincipal Jwt cliente) {
        return debitoMapper.aDebitoAutomaticoResponse(debitoService.consultar(SeguridadJwt.usuarioId(cliente)));
    }

    @PutMapping
    public DebitoAutomaticoResponse activar(
            @Valid @RequestBody ActivarDebitoRequest request,
            @AuthenticationPrincipal Jwt cliente) {
        return debitoMapper.aDebitoAutomaticoResponse(debitoService.activar(SeguridadJwt.usuarioId(cliente), request));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@AuthenticationPrincipal Jwt cliente) {
        debitoService.desactivar(SeguridadJwt.usuarioId(cliente));
    }
}
