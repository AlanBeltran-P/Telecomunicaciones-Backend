package com.telecom.contrataciones.contratacion.dto;

import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratacionResponse(
        Long id,
        Long planId,
        String planNombre,
        BigDecimal precioMensual,
        String direccion,
        EstadoContratacion estado,
        LocalDate fechaContratacion) {
}
