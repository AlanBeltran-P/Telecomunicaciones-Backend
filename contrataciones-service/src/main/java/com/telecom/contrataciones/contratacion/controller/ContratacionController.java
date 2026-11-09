package com.telecom.contrataciones.contratacion.controller;

import com.telecom.contrataciones.contratacion.dto.ContratacionResponse;
import com.telecom.contrataciones.contratacion.dto.ContratarPlanRequest;
import com.telecom.contrataciones.contratacion.mapper.ContratacionMapper;
import com.telecom.contrataciones.contratacion.service.ContratacionService;
import com.telecom.seguridad.SeguridadJwt;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // El cliente contrata para sí mismo: su id sale del token, nunca del cuerpo del pedido
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacionResponse contratar(
            @Valid @RequestBody ContratarPlanRequest request,
            @AuthenticationPrincipal Jwt cliente) {
        return contratacionMapper.aContratacionResponse(
                contratacionService.contratar(SeguridadJwt.usuarioId(cliente), request));
    }

    // El cliente ve sus contrataciones: su id sale del token
    @GetMapping("/mias")
    public List<ContratacionResponse> listarMias(@AuthenticationPrincipal Jwt cliente) {
        return contratacionMapper.aContratacionResponses(
                contratacionService.listarDelCliente(SeguridadJwt.usuarioId(cliente)));
    }

    // Un empleado le asigna un plan a un cliente (en sucursal o por teléfono)
    @PostMapping("/clientes/{clienteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacionResponse contratarParaCliente(
            @PathVariable Long clienteId,
            @Valid @RequestBody ContratarPlanRequest request) {
        return contratacionMapper.aContratacionResponse(contratacionService.contratarParaCliente(clienteId, request));
    }

    @GetMapping("/clientes/{clienteId}")
    public List<ContratacionResponse> listarDelCliente(@PathVariable Long clienteId) {
        return contratacionMapper.aContratacionResponses(contratacionService.listarDelCliente(clienteId));
    }

    // Historia 14: contrataciones dadas de baja por falta de pago
    @GetMapping("/suspendidas")
    public List<ContratacionResponse> listarSuspendidas() {
        return contratacionMapper.aContratacionResponses(contratacionService.listarSuspendidas());
    }

    @PutMapping("/{id}/reactivacion")
    public ContratacionResponse reactivar(@PathVariable Long id) {
        return contratacionMapper.aContratacionResponse(contratacionService.reactivar(id));
    }

    // Quitar un plan: la contratación queda en BAJA (no se borra)
    @PutMapping("/{id}/baja")
    public ContratacionResponse darDeBaja(@PathVariable Long id) {
        return contratacionMapper.aContratacionResponse(contratacionService.darDeBaja(id));
    }
}
