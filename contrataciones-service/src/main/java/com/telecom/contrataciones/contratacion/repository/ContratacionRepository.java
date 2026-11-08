package com.telecom.contrataciones.contratacion.repository;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratacionRepository extends JpaRepository<Contratacion, Long> {

    boolean existsByClienteIdAndPlanIdAndEstadoNot(Long clienteId, Long planId, EstadoContratacion estado);

    List<Contratacion> findByClienteIdOrderByFechaContratacionDesc(Long clienteId);
}
