package com.telecom.contrataciones.contratacion.repository;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratacionRepository extends JpaRepository<Contratacion, Long> {

    boolean existsByClienteEmailAndPlanId(String clienteEmail, Long planId);
}
