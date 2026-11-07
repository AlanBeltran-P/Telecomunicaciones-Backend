package com.telecom.catalogo.plan;

import java.math.BigDecimal;

public record PlanResponse(Long id, String nombre, String descripcion, BigDecimal precio) {
}
