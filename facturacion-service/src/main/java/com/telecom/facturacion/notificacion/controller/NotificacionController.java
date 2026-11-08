package com.telecom.facturacion.notificacion.controller;

import com.telecom.facturacion.notificacion.dto.NotificacionResponse;
import com.telecom.facturacion.notificacion.mapper.NotificacionMapper;
import com.telecom.facturacion.notificacion.service.NotificacionService;
import com.telecom.seguridad.SeguridadJwt;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Avisos del cliente que inició sesión (su id sale del token)
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final NotificacionMapper notificacionMapper;

    public NotificacionController(NotificacionService notificacionService, NotificacionMapper notificacionMapper) {
        this.notificacionService = notificacionService;
        this.notificacionMapper = notificacionMapper;
    }

    @GetMapping("/mias")
    public List<NotificacionResponse> listarMias(@AuthenticationPrincipal Jwt cliente) {
        return notificacionMapper.aNotificacionResponses(
                notificacionService.listarDelCliente(SeguridadJwt.usuarioId(cliente)));
    }

    @PutMapping("/{id}/leida")
    public NotificacionResponse marcarLeida(@PathVariable Long id, @AuthenticationPrincipal Jwt cliente) {
        return notificacionMapper.aNotificacionResponse(
                notificacionService.marcarLeida(id, SeguridadJwt.usuarioId(cliente)));
    }
}
