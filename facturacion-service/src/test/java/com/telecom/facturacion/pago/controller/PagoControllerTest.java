package com.telecom.facturacion.pago.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

// Historia 11: el cliente paga una factura con tarjeta, Mercado Pago o PayPal (pasarela simulada)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PagoControllerTest {

    private static final Long CLIENTE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaRepository facturaRepository;

    @ParameterizedTest
    @EnumSource(MedioDePago.class)
    void elClientePagaSuFacturaConCualquierMedio(MedioDePago medio) throws Exception {
        Factura factura = guardarFactura(CLIENTE_ID);

        pagar(factura.getId(), medio.name(), "tok_123", CLIENTE_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADA"))
                .andExpect(jsonPath("$.medioDePago").value(medio.name()))
                .andExpect(jsonPath("$.fechaPago").value(LocalDate.now().toString()));

        Factura pagada = facturaRepository.findById(factura.getId()).orElseThrow();
        assertThat(pagada.getReferenciaDePago()).startsWith("SIM-");
    }

    @Test
    void unPagoRechazadoResponde402YLaFacturaSigueSinPagar() throws Exception {
        Factura factura = guardarFactura(CLIENTE_ID);

        pagar(factura.getId(), "TARJETA_CREDITO", "rechazado", CLIENTE_ID)
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.detail").value(
                        "El pago de la factura " + factura.getId() + " fue rechazado. Probá con otro medio de pago"));

        assertThat(facturaRepository.findById(factura.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    void noSePuedePagarDosVeces() throws Exception {
        Factura factura = guardarFactura(CLIENTE_ID);
        pagar(factura.getId(), "PAYPAL", "tok_123", CLIENTE_ID);

        pagar(factura.getId(), "PAYPAL", "tok_123", CLIENTE_ID)
                .andExpect(status().isConflict());
    }

    @Test
    void unClienteNoPuedePagarLaFacturaDeOtro() throws Exception {
        Factura deOtro = guardarFactura(20L);

        pagar(deOtro.getId(), "PAYPAL", "tok_123", CLIENTE_ID)
                .andExpect(status().isNotFound());
    }

    @Test
    void unMedioDePagoInvalidoResponde400() throws Exception {
        Factura factura = guardarFactura(CLIENTE_ID);

        pagar(factura.getId(), "BITCOIN", "tok_123", CLIENTE_ID)
                .andExpect(status().isBadRequest());
    }

    private ResultActions pagar(Long facturaId, String medio, String token, Long clienteId) throws Exception {
        return mockMvc.perform(post("/api/facturas/" + facturaId + "/pagos")
                .with(comoCliente(clienteId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"medio\": \"" + medio + "\", \"tokenDePago\": \"" + token + "\"}"));
    }

    private RequestPostProcessor comoCliente(Long clienteId) {
        return jwt()
                .jwt(token -> token.subject("cliente@mail.com").claim("usuarioId", clienteId))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    private Factura guardarFactura(Long clienteId) {
        LocalDate desde = LocalDate.of(2026, 11, 7);
        LocalDate hasta = desde.plusMonths(1);
        return facturaRepository.save(new Factura(
                clienteId, clienteId * 100, "Internet 300 MB", new BigDecimal("15000"),
                desde, hasta, hasta, hasta.plusDays(10)));
    }
}
