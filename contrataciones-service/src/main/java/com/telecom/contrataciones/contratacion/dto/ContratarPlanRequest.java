package com.telecom.contrataciones.contratacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DNI y teléfono están en el perfil del cliente (usuarios-service)
public record ContratarPlanRequest(
        @NotNull Long planId,
        @NotBlank String direccion) {
}
