package com.telecom.contrataciones.contratacion.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
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
class ContratacionInternaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @Test
    void facturacionVeLasContratacionesPendientesYActivasPeroNoLasDadasDeBaja() throws Exception {
        guardar("Internet 300 MB");
        guardar("Telefonía fija").marcarInstalada(LocalDate.now());
        guardar("TV").darDeBaja(LocalDate.now());

        mockMvc.perform(get("/api/internos/contrataciones/facturables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SISTEMA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].planNombre", containsInAnyOrder("Internet 300 MB", "Telefonía fija")))
                .andExpect(jsonPath("$[0].precioMensual").value(15000))
                .andExpect(jsonPath("$[0].clienteId").value(10));
    }

    @Test
    void unaContratacionSuspendidaNoSeFactura() throws Exception {
        guardar("Internet 300 MB").suspenderPorFaltaDePago(LocalDate.now());

        mockMvc.perform(get("/api/internos/contrataciones/facturables").with(comoSistema()))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void unaContratacionReactivadaSeFacturaDesdeLaReactivacion() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.suspenderPorFaltaDePago(LocalDate.of(2027, 1, 10));
        contratacion.reactivar(LocalDate.of(2027, 2, 1));

        mockMvc.perform(get("/api/internos/contrataciones/facturables").with(comoSistema()))
                .andExpect(jsonPath("$[0].fechaContratacion").value("2026-11-07"))
                .andExpect(jsonPath("$[0].facturarDesde").value("2027-02-01"));
    }

    @Test
    void facturacionSuspendeUnaContratacionPorFaltaDePago() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");

        mockMvc.perform(put("/api/internos/contrataciones/" + contratacion.getId() + "/suspension").with(comoSistema()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SUSPENDIDA_POR_FALTA_DE_PAGO"))
                .andExpect(jsonPath("$.fechaSuspension").value(LocalDate.now().toString()));
    }

    @Test
    void suspenderUnaDadaDeBajaNoLaCambia() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.darDeBaja(LocalDate.now());

        mockMvc.perform(put("/api/internos/contrataciones/" + contratacion.getId() + "/suspension").with(comoSistema()))
                .andExpect(jsonPath("$.estado").value("BAJA"));
    }

    @Test
    void unUsuarioNoPuedeUsarLasRutasInternas() throws Exception {
        mockMvc.perform(get("/api/internos/contrataciones/facturables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor comoSistema() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_SISTEMA"));
    }

    private Contratacion guardar(String plan) {
        return contratacionRepository.save(new Contratacion(
                10L, 1L, plan, new BigDecimal("15000"), "Av. Siempre Viva 742", LocalDate.of(2026, 11, 7)));
    }
}
