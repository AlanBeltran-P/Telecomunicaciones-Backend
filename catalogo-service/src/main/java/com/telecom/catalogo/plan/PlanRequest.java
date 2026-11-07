package com.telecom.catalogo.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull @Positive BigDecimal precio) {
}
