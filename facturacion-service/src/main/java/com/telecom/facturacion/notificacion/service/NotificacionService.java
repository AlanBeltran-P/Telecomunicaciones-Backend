package com.telecom.facturacion.notificacion.service;

import com.telecom.facturacion.notificacion.client.EnviadorDeEmail;
import com.telecom.facturacion.notificacion.entity.Notificacion;
import com.telecom.facturacion.notificacion.exception.NotificacionNoEncontradaException;
import com.telecom.facturacion.notificacion.repository.NotificacionRepository;
import com.telecom.facturacion.usuario.client.UsuariosClient;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;
    private final UsuariosClient usuariosClient;
    private final EnviadorDeEmail enviadorDeEmail;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            UsuariosClient usuariosClient,
            EnviadorDeEmail enviadorDeEmail) {
        this.notificacionRepository = notificacionRepository;
        this.usuariosClient = usuariosClient;
        this.enviadorDeEmail = enviadorDeEmail;
    }

    // Avisa dentro del sistema y por email. Si el email falla, el aviso dentro del sistema queda igual.
    @Transactional
    public void notificar(Long clienteId, String asunto, String mensaje) {
        notificacionRepository.save(new Notificacion(clienteId, asunto, mensaje, LocalDateTime.now()));
        try {
            String email = usuariosClient.buscar(clienteId).email();
            enviadorDeEmail.enviar(email, asunto, mensaje);
        } catch (RuntimeException error) {
            log.error("No se pudo enviar el email al cliente {}: {}", clienteId, asunto, error);
        }
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarDelCliente(Long clienteId) {
        return notificacionRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    @Transactional
    public Notificacion marcarLeida(Long id, Long clienteId) {
        Notificacion notificacion = notificacionRepository.findByIdAndClienteId(id, clienteId)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
        notificacion.marcarLeida();
        return notificacion;
    }
}
