package com.telecom.catalogo.plan;

import java.math.BigDecimal;

public record PlanResponse(Long id, String nombre, String descripcion, BigDecimal precio) {

    public static PlanResponse desde(Plan plan) {
        return new PlanResponse(plan.getId(), plan.getNombre(), plan.getDescripcion(), plan.getPrecio());
    }
}
