package com.telecom.facturacion.debito.dto;

import com.telecom.facturacion.factura.entity.MedioDePago;
import java.time.LocalDate;

// Nunca devuelve el token de pago
public record DebitoAutomaticoResponse(MedioDePago medio, LocalDate fechaAlta) {
}
