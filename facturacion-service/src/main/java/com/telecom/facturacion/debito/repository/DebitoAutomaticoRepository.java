package com.telecom.facturacion.debito.repository;

import com.telecom.facturacion.debito.entity.DebitoAutomatico;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitoAutomaticoRepository extends JpaRepository<DebitoAutomatico, Long> {

    Optional<DebitoAutomatico> findByClienteId(Long clienteId);

    // Para los reportes: altas de débito automático entre dos fechas (inclusive)
    long countByFechaAltaBetween(LocalDate desde, LocalDate hasta);
}
