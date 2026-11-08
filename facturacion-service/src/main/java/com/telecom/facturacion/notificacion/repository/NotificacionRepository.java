package com.telecom.facturacion.notificacion.repository;

import com.telecom.facturacion.notificacion.entity.Notificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByClienteIdOrderByFechaDesc(Long clienteId);

    Optional<Notificacion> findByIdAndClienteId(Long id, Long clienteId);
}
