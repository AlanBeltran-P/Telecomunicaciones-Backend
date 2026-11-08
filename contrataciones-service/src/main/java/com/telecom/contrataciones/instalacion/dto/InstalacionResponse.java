package com.telecom.contrataciones.instalacion.dto;

import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import java.time.LocalDate;

// La instalación es una etapa de la contratación: se identifica por el id de la contratación
public record InstalacionResponse(
        Long contratacionId,
        Long clienteId,
        String planNombre,
        String direccion,
        LocalDate fechaContratacion,
        Long tecnicoId,
        String tecnicoNombre,
        EstadoContratacion estado,
        LocalDate fechaInstalacion) {
}
