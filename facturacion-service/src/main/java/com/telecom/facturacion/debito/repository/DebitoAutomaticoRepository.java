package com.telecom.facturacion.debito.repository;

import com.telecom.facturacion.debito.entity.DebitoAutomatico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitoAutomaticoRepository extends JpaRepository<DebitoAutomatico, Long> {

    Optional<DebitoAutomatico> findByClienteId(Long clienteId);
}
