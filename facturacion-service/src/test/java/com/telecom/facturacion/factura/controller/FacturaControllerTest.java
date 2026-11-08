package com.telecom.facturacion.factura.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaRepository facturaRepository;

    @Test
    void elClienteVeSusFacturasDeLaMasReciente() throws Exception {
        guardarFactura(10L, LocalDate.of(2026, 11, 7));
        guardarFactura(10L, LocalDate.of(2026, 12, 7));
        guardarFactura(20L, LocalDate.of(2026, 11, 7));

        mockMvc.perform(get("/api/facturas/mias").with(comoCliente(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].periodoDesde").value("2026-12-07"))
                .andExpect(jsonPath("$[0].fechaVencimiento").value("2027-01-17"))
                .andExpect(jsonPath("$[0].monto").value(15000))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void unEmpleadoNoUsaEsteEndpoint() throws Exception {
        mockMvc.perform(get("/api/facturas/mias").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinSesionResponde401() throws Exception {
        mockMvc.perform(get("/api/facturas/mias")).andExpect(status().isUnauthorized());
    }

    private RequestPostProcessor comoCliente(Long clienteId) {
        return jwt()
                .jwt(token -> token.subject("cliente@mail.com").claim("usuarioId", clienteId))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    private void guardarFactura(Long clienteId, LocalDate desde) {
        LocalDate hasta = desde.plusMonths(1);
        facturaRepository.save(new Factura(
                // Una contratación distinta por cliente: la factura es única por contratación y mes
                clienteId, clienteId * 100, "Internet 300 MB", new BigDecimal("15000"), desde, hasta, hasta, hasta.plusDays(10)));
    }
}
