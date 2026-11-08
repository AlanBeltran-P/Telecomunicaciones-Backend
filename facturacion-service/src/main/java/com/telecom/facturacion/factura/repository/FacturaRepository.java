package com.telecom.facturacion.factura.repository;

import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    boolean existsByContratacionIdAndPeriodoDesde(Long contratacionId, LocalDate periodoDesde);

    List<Factura> findByClienteIdOrderByFechaEmisionDesc(Long clienteId);

    // Así un cliente nunca puede operar sobre la factura de otro
    Optional<Factura> findByIdAndClienteId(Long id, Long clienteId);

    List<Factura> findByClienteIdAndEstadoIn(Long clienteId, List<EstadoFactura> estados);

    List<Factura> findByEstadoAndFechaVencimientoBefore(EstadoFactura estado, LocalDate fecha);

    List<Factura> findByEstadoAndBajaSolicitadaFalseAndFechaVencimientoBefore(EstadoFactura estado, LocalDate fecha);
}
