package com.telecom.contrataciones.reporte.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReporteContratacionesControllerTest {

    private static final String NOVIEMBRE_DESDE = "2026-11-01";
    private static final String NOVIEMBRE_HASTA = "2026-11-30";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    private Contratacion reactivada;

    // Noviembre 2026: 3 contrataciones nuevas, 2 instaladas, 1 baja, 1 suspendida y reactivada
    @BeforeEach
    void guardarContrataciones() {
        Contratacion instalada = guardar("Fibra 300", LocalDate.of(2026, 11, 2));
        instalada.asignarTecnico(1L, "Carlos Gómez");
        instalada.marcarInstalada(LocalDate.of(2026, 11, 5));

        guardar("Fibra 300", LocalDate.of(2026, 11, 2));

        reactivada = guardar("Fibra 1000", LocalDate.of(2026, 11, 3));
        reactivada.asignarTecnico(2L, "Julieta Sosa");
        reactivada.marcarInstalada(LocalDate.of(2026, 11, 4));
        reactivada.suspenderPorFaltaDePago(LocalDate.of(2026, 11, 20));
        reactivada.reactivar(LocalDate.of(2026, 11, 25));

        // Contratada en octubre, dada de baja en noviembre
        guardar("Fibra + TV", LocalDate.of(2026, 10, 15)).darDeBaja(LocalDate.of(2026, 11, 10));
    }

    @Test
    void resumeLosMovimientosDelPeriodo() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contratacionesNuevas").value(3))
                .andExpect(jsonPath("$.bajas").value(1))
                .andExpect(jsonPath("$.suspendidas").value(1))
                .andExpect(jsonPath("$.reactivadas").value(1))
                .andExpect(jsonPath("$.instalacionesRealizadas").value(2))
                .andExpect(jsonPath("$.instalacionesPendientes").value(1));
    }

    @Test
    void cuentaLasContratacionesPorPlanDeMayorAMenor() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.contratacionesPorPlan", hasSize(2)))
                .andExpect(jsonPath("$.contratacionesPorPlan[0].nombre").value("Fibra 300"))
                .andExpect(jsonPath("$.contratacionesPorPlan[0].cantidad").value(2))
                .andExpect(jsonPath("$.contratacionesPorPlan[1].nombre").value("Fibra 1000"));
    }

    @Test
    void cuentaLasInstalacionesDeCadaTecnico() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.instalacionesPorTecnico", hasSize(2)))
                .andExpect(jsonPath("$.instalacionesPorTecnico[0].nombre").value("Carlos Gómez"))
                .andExpect(jsonPath("$.instalacionesPorTecnico[0].cantidad").value(1));
    }

    @Test
    void laSeriePorDiaTieneUnPuntoPorCadaDia() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.serie", hasSize(30)))
                .andExpect(jsonPath("$.serie[1].periodo").value("2026-11-02"))
                .andExpect(jsonPath("$.serie[1].altas").value(2))
                .andExpect(jsonPath("$.serie[4].instalaciones").value(1))
                .andExpect(jsonPath("$.serie[9].bajas").value(1))
                .andExpect(jsonPath("$.serie[0].altas").value(0));
    }

    @Test
    void laSeriePorMesAgrupaCadaMes() throws Exception {
        mockMvc.perform(reporte("2026-10-01", NOVIEMBRE_HASTA, "MES"))
                .andExpect(jsonPath("$.serie", hasSize(2)))
                .andExpect(jsonPath("$.serie[0].periodo").value("2026-10"))
                .andExpect(jsonPath("$.serie[0].altas").value(1))
                .andExpect(jsonPath("$.serie[1].altas").value(3));
    }

    @Test
    void alReactivarSeGuardaLaFechaYNoSePierdeLaSuspension() {
        Contratacion guardada = contratacionRepository.findById(reactivada.getId()).orElseThrow();

        assertThat(guardada.getFechaSuspension()).isNull();
        assertThat(guardada.getFechaUltimaSuspension()).isEqualTo(LocalDate.of(2026, 11, 20));
        assertThat(guardada.getFechaReactivacion()).isEqualTo(LocalDate.of(2026, 11, 25));
    }

    @Test
    void siDesdeEsPosteriorAHastaResponde400() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_HASTA, NOVIEMBRE_DESDE, "DIA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("La fecha desde (2026-11-30) no puede ser posterior a la fecha hasta (2026-11-01)"));
    }

    @Test
    void unEmpleadoNoPuedeVerElReporte() throws Exception {
        mockMvc.perform(get("/api/contrataciones/reporte")
                        .param("desde", NOVIEMBRE_DESDE)
                        .param("hasta", NOVIEMBRE_HASTA)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    private Contratacion guardar(String plan, LocalDate fecha) {
        return contratacionRepository.save(
                new Contratacion(10L, 1L, plan, new BigDecimal("18500"), "Av. Corrientes 1234", fecha));
    }

    private MockHttpServletRequestBuilder reporte(String desde, String hasta, String agrupacion) {
        return get("/api/contrataciones/reporte")
                .param("desde", desde)
                .param("hasta", hasta)
                .param("agrupacion", agrupacion)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
    }
}
