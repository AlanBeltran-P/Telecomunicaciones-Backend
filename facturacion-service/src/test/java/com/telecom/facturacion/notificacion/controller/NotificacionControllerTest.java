package com.telecom.facturacion.notificacion.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.facturacion.notificacion.entity.Notificacion;
import com.telecom.facturacion.notificacion.repository.NotificacionRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

// Historia 13: el cliente ve los avisos dentro del sistema
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Test
    void elClienteVeSusAvisosDelMasReciente() throws Exception {
        guardar(10L, "Tu factura venció", LocalDateTime.of(2026, 12, 18, 2, 0));
        guardar(10L, "Dimos de baja tu plan", LocalDateTime.of(2026, 12, 31, 2, 0));
        guardar(20L, "De otro cliente", LocalDateTime.of(2026, 12, 18, 2, 0));

        mockMvc.perform(get("/api/notificaciones/mias").with(comoCliente(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].asunto").value("Dimos de baja tu plan"))
                .andExpect(jsonPath("$[0].leida").value(false));
    }

    @Test
    void elClienteMarcaUnAvisoComoLeido() throws Exception {
        Notificacion aviso = guardar(10L, "Tu factura venció", LocalDateTime.now());

        mockMvc.perform(put("/api/notificaciones/" + aviso.getId() + "/leida").with(comoCliente(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));
    }

    @Test
    void noPuedeMarcarElAvisoDeOtroCliente() throws Exception {
        Notificacion deOtro = guardar(20L, "De otro cliente", LocalDateTime.now());

        mockMvc.perform(put("/api/notificaciones/" + deOtro.getId() + "/leida").with(comoCliente(10L)))
                .andExpect(status().isNotFound());
    }

    private RequestPostProcessor comoCliente(Long clienteId) {
        return jwt()
                .jwt(token -> token.subject("cliente@mail.com").claim("usuarioId", clienteId))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    private Notificacion guardar(Long clienteId, String asunto, LocalDateTime fecha) {
        return notificacionRepository.save(new Notificacion(clienteId, asunto, "Mensaje", fecha));
    }
}
