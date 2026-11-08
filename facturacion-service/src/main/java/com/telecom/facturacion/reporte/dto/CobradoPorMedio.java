package com.telecom.facturacion.reporte.dto;

import com.telecom.facturacion.factura.entity.MedioDePago;
import java.math.BigDecimal;

// Pagos del período con un medio de pago (siempre vienen los cuatro, aunque sea en cero)
public record CobradoPorMedio(MedioDePago medio, long pagos, BigDecimal monto) {
}
