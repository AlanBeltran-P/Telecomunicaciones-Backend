package com.telecom.contrataciones.contratacion.repository;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratacionRepository extends JpaRepository<Contratacion, Long> {

    boolean existsByClienteIdAndPlanIdAndEstadoNot(Long clienteId, Long planId, EstadoContratacion estado);

    List<Contratacion> findByClienteIdOrderByFechaContratacionDesc(Long clienteId);

    List<Contratacion> findByEstadoOrderByFechaContratacionAsc(EstadoContratacion estado);

    List<Contratacion> findByEstadoIn(List<EstadoContratacion> estados);

    // Para los reportes: lo que pasó entre dos fechas (inclusive)
    List<Contratacion> findByFechaContratacionBetween(LocalDate desde, LocalDate hasta);

    List<Contratacion> findByFechaBajaBetween(LocalDate desde, LocalDate hasta);

    List<Contratacion> findByFechaUltimaSuspensionBetween(LocalDate desde, LocalDate hasta);

    List<Contratacion> findByFechaReactivacionBetween(LocalDate desde, LocalDate hasta);

    List<Contratacion> findByFechaInstalacionBetween(LocalDate desde, LocalDate hasta);

    long countByEstado(EstadoContratacion estado);

    List<Contratacion> findByTecnicoIdAndEstadoOrderByFechaContratacionAsc(
            Long tecnicoId, EstadoContratacion estado);
}
