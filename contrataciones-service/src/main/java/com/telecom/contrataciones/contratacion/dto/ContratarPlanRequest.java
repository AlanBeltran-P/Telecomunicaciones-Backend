package com.telecom.contrataciones.contratacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ContratarPlanRequest(
        @NotNull Long planId,
        @NotBlank String direccion,
        @NotBlank @Pattern(regexp = "\\d{7,8}", message = "debe tener 7 u 8 dígitos") String dni,
        @NotBlank @Pattern(regexp = "[0-9+()\\- ]{6,20}", message = "no es un teléfono válido") String telefono) {
}
