package com.telecom.catalogo.plan.service;

import com.telecom.catalogo.plan.dto.PlanRequest;
import com.telecom.catalogo.plan.entity.Plan;
import com.telecom.catalogo.plan.exception.PlanNoEncontradoException;
import com.telecom.catalogo.plan.mapper.PlanMapper;
import com.telecom.catalogo.plan.repository.PlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    public PlanService(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @Transactional
    public Plan crear(PlanRequest request) {
        return planRepository.save(planMapper.aPlan(request));
    }

    @Transactional(readOnly = true)
    public List<Plan> listarActivos() {
        return planRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Plan buscarActivo(Long id) {
        return planRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PlanNoEncontradoException(id));
    }

    @Transactional
    public Plan actualizar(Long id, PlanRequest request) {
        Plan plan = buscarActivo(id);
        plan.actualizar(request.nombre(), request.descripcion(), request.precio());
        return plan;
    }

    @Transactional
    public void darDeBaja(Long id) {
        buscarActivo(id).darDeBaja();
    }
}
