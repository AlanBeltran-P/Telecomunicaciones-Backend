package com.telecom.facturacion.debito.dto;

import com.telecom.facturacion.factura.entity.MedioDePago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// tokenDePago: token reutilizable que genera el frontend con el SDK del proveedor
public record ActivarDebitoRequest(
        @NotNull MedioDePago medio,
        @NotBlank String tokenDePago) {
}
