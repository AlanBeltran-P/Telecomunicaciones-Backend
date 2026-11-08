package com.telecom.facturacion.pago.dto;

import com.telecom.facturacion.factura.entity.MedioDePago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// tokenDePago: lo genera el frontend con el SDK del proveedor (Mercado Pago, PayPal o el procesador
// de tarjetas). Los datos de la tarjeta nunca llegan a este servicio.
public record PagarFacturaRequest(
        @NotNull MedioDePago medio,
        @NotBlank String tokenDePago) {
}
