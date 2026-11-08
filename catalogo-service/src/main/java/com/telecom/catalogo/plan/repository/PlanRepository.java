package com.telecom.catalogo.plan.repository;

import com.telecom.catalogo.plan.entity.Plan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByActivoTrue();

    Optional<Plan> findByIdAndActivoTrue(Long id);
}
