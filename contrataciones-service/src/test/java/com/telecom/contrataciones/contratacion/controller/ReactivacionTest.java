package com.telecom.contrataciones.contratacion.controller;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

// Historia 14: el empleado ve las bajas por falta de pago y reactiva el plan
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReactivacionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @Test
    void elEmpleadoVeSoloLasContratacionesSuspendidasPorFaltaDePago() throws Exception {
        guardar("Internet 300 MB").suspenderPorFaltaDePago(LocalDate.of(2026, 12, 31));
        guardar("Telefonía fija").darDeBaja(LocalDate.now());
        guardar("TV");

        mockMvc.perform(get("/api/contrataciones/suspendidas").with(comoEmpleado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].planNombre").value("Internet 300 MB"))
                .andExpect(jsonPath("$[0].clienteId").value(10))
                .andExpect(jsonPath("$[0].fechaSuspension").value("2026-12-31"));
    }

    @Test
    void alReactivarUnaContratacionInstaladaVuelveAEstarActiva() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.marcarInstalada(LocalDate.of(2026, 11, 10));
        contratacion.suspenderPorFaltaDePago(LocalDate.of(2026, 12, 31));

        reactivar(contratacion)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.fechaSuspension").doesNotExist());
    }

    @Test
    void alReactivarUnaSinInstalarVuelveAQuedarPendiente() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.suspenderPorFaltaDePago(LocalDate.of(2026, 12, 31));

        reactivar(contratacion).andExpect(jsonPath("$.estado").value("PENDIENTE_INSTALACION"));
    }

    @Test
    void desdeLaReactivacionSeVuelveAFacturar() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.suspenderPorFaltaDePago(LocalDate.of(2026, 12, 31));

        reactivar(contratacion);

        mockMvc.perform(get("/api/internos/contrataciones/facturables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SISTEMA"))))
                .andExpect(jsonPath("$[0].facturarDesde").value(LocalDate.now().toString()));
    }

    @Test
    void noSeReactivaUnaQueNoEstaSuspendida() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");

        reactivar(contratacion)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(
                        "La contratación " + contratacion.getId() + " no está dada de baja por falta de pago"));
    }

    @Test
    void unClienteNoPuedeReactivar() throws Exception {
        Contratacion contratacion = guardar("Internet 300 MB");
        contratacion.suspenderPorFaltaDePago(LocalDate.now());

        mockMvc.perform(put("/api/contrataciones/" + contratacion.getId() + "/reactivacion")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }

    private ResultActions reactivar(Contratacion contratacion) throws Exception {
        return mockMvc.perform(put("/api/contrataciones/" + contratacion.getId() + "/reactivacion").with(comoEmpleado()));
    }

    private RequestPostProcessor comoEmpleado() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"));
    }

    private Contratacion guardar(String plan) {
        return contratacionRepository.save(new Contratacion(
                10L, 1L, plan, new BigDecimal("15000"), "Av. Siempre Viva 742", LocalDate.of(2026, 11, 7)));
    }
}
