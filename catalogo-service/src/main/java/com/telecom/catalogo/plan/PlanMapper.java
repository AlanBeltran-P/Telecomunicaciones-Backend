package com.telecom.catalogo.plan;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PlanMapper {

    // Usa el constructor público de Plan: el id y "activo" los maneja la entidad
    Plan aPlan(PlanRequest request);

    PlanResponse aPlanResponse(Plan plan);

    List<PlanResponse> aPlanResponses(List<Plan> planes);
}
