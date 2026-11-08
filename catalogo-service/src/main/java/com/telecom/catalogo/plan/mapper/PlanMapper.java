package com.telecom.catalogo.plan.mapper;

import com.telecom.catalogo.plan.dto.PlanRequest;
import com.telecom.catalogo.plan.dto.PlanResponse;
import com.telecom.catalogo.plan.entity.Plan;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PlanMapper {

    // Usa el constructor público de Plan: el id y "activo" los maneja la entidad
    Plan aPlan(PlanRequest request);

    PlanResponse aPlanResponse(Plan plan);

    List<PlanResponse> aPlanResponses(List<Plan> planes);
}
