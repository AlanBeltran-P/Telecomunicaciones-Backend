package com.telecom.facturacion.notificacion.dto;

import java.time.LocalDateTime;

public record NotificacionResponse(Long id, String asunto, String mensaje, LocalDateTime fecha, boolean leida) {
}
