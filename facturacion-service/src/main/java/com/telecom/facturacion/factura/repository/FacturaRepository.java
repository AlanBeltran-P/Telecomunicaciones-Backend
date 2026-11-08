package com.telecom.facturacion.factura.repository;

import com.telecom.facturacion.factura.entity.Factura;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    boolean existsByContratacionIdAndPeriodoDesde(Long contratacionId, LocalDate periodoDesde);

    List<Factura> findByClienteIdOrderByFechaEmisionDesc(Long clienteId);
}
