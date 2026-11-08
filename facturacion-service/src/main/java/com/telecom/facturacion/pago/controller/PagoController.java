package com.telecom.facturacion.pago.controller;

import com.telecom.facturacion.factura.dto.FacturaResponse;
import com.telecom.facturacion.factura.mapper.FacturaMapper;
import com.telecom.facturacion.pago.dto.PagarFacturaRequest;
import com.telecom.facturacion.pago.service.PagoService;
import com.telecom.seguridad.SeguridadJwt;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas")
public class PagoController {

    private final PagoService pagoService;
    private final FacturaMapper facturaMapper;

    public PagoController(PagoService pagoService, FacturaMapper facturaMapper) {
        this.pagoService = pagoService;
        this.facturaMapper = facturaMapper;
    }

    // Devuelve la factura ya pagada
    @PostMapping("/{facturaId}/pagos")
    public FacturaResponse pagar(
            @PathVariable Long facturaId,
            @Valid @RequestBody PagarFacturaRequest request,
            @AuthenticationPrincipal Jwt cliente) {
        return facturaMapper.aFacturaResponse(
                pagoService.pagar(facturaId, SeguridadJwt.usuarioId(cliente), request));
    }
}
