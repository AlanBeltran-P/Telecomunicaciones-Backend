package com.telecom.facturacion.factura.dto;

import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaResponse(
        Long id,
        Long contratacionId,
        String planNombre,
        BigDecimal monto,
        LocalDate periodoDesde,
        LocalDate periodoHasta,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        EstadoFactura estado,
        LocalDate fechaPago,
        MedioDePago medioDePago) {
}
