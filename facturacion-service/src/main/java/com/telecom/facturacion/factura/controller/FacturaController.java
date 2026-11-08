package com.telecom.facturacion.factura.controller;

import com.telecom.facturacion.factura.dto.FacturaResponse;
import com.telecom.facturacion.factura.mapper.FacturaMapper;
import com.telecom.facturacion.factura.service.FacturaService;
import com.telecom.seguridad.SeguridadJwt;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final FacturaMapper facturaMapper;

    public FacturaController(FacturaService facturaService, FacturaMapper facturaMapper) {
        this.facturaService = facturaService;
        this.facturaMapper = facturaMapper;
    }

    // El cliente ve sus facturas: su id sale del token
    @GetMapping("/mias")
    public List<FacturaResponse> listarMias(@AuthenticationPrincipal Jwt cliente) {
        return facturaMapper.aFacturaResponses(facturaService.listarDelCliente(SeguridadJwt.usuarioId(cliente)));
    }
}
