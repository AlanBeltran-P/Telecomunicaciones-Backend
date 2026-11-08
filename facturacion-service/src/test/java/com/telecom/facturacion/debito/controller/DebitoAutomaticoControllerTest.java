package com.telecom.facturacion.debito.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

// Historia 12: el cliente activa, consulta y desactiva el débito automático
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DebitoAutomaticoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elClienteActivaElDebitoAutomatico() throws Exception {
        activar("TARJETA_CREDITO")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medio").value("TARJETA_CREDITO"))
                .andExpect(jsonPath("$.fechaAlta").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.tokenDePago").doesNotExist());
    }

    @Test
    void activarloDeNuevoCambiaElMedioDePago() throws Exception {
        activar("TARJETA_CREDITO");
        activar("MERCADO_PAGO");

        mockMvc.perform(get("/api/facturas/debito-automatico").with(comoCliente()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medio").value("MERCADO_PAGO"));
    }

    @Test
    void sinDebitoActivoLaConsultaResponde404() throws Exception {
        mockMvc.perform(get("/api/facturas/debito-automatico").with(comoCliente()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No tenés el débito automático activado"));
    }

    @Test
    void elClienteDesactivaElDebitoAutomatico() throws Exception {
        activar("PAYPAL");

        mockMvc.perform(delete("/api/facturas/debito-automatico").with(comoCliente()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/facturas/debito-automatico").with(comoCliente()))
                .andExpect(status().isNotFound());
    }

    private ResultActions activar(String medio) throws Exception {
        return mockMvc.perform(put("/api/facturas/debito-automatico")
                .with(comoCliente())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"medio\": \"" + medio + "\", \"tokenDePago\": \"tok_recurrente\"}"));
    }

    private RequestPostProcessor comoCliente() {
        return jwt()
                .jwt(token -> token.subject("cliente@mail.com").claim("usuarioId", 10L))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
