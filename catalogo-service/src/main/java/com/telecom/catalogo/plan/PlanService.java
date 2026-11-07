package com.telecom.catalogo.plan;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan crear(PlanRequest request) {
        Plan plan = new Plan(request.nombre(), request.descripcion(), request.precio());
        return planRepository.save(plan);
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
