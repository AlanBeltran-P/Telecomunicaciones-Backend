package com.telecom.contrataciones.instalacion.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
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

// Historia 9: el técnico ve sus instalaciones y las marca como realizadas
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InstalacionesDelTecnicoTest {

    private static final Long TECNICO_ID = 30L;
    private static final Long OTRO_TECNICO_ID = 31L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @Test
    void elTecnicoVeSoloSusInstalacionesPendientes() throws Exception {
        guardarAsignadaA(TECNICO_ID, "Internet 300 MB");
        guardarAsignadaA(OTRO_TECNICO_ID, "Telefonía fija");
        Contratacion yaInstalada = guardarAsignadaA(TECNICO_ID, "TV");
        yaInstalada.marcarInstalada(LocalDate.now());

        mockMvc.perform(get("/api/instalaciones/mias").with(comoTecnico(TECNICO_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].planNombre").value("Internet 300 MB"))
                .andExpect(jsonPath("$[0].direccion").value("Av. Siempre Viva 742"));
    }

    @Test
    void elTecnicoMarcaUnaInstalacionComoRealizadaYElServicioQuedaActivo() throws Exception {
        Contratacion contratacion = guardarAsignadaA(TECNICO_ID, "Internet 300 MB");

        mockMvc.perform(put("/api/instalaciones/" + contratacion.getId() + "/realizada").with(comoTecnico(TECNICO_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.fechaInstalacion").value(LocalDate.now().toString()));

        assertThat(contratacionRepository.findById(contratacion.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoContratacion.ACTIVA);
    }

    @Test
    void unTecnicoNoPuedeMarcarLaInstalacionDeOtro() throws Exception {
        Contratacion contratacion = guardarAsignadaA(OTRO_TECNICO_ID, "Internet 300 MB");

        mockMvc.perform(put("/api/instalaciones/" + contratacion.getId() + "/realizada").with(comoTecnico(TECNICO_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    void noSePuedeMarcarDosVeces() throws Exception {
        Contratacion contratacion = guardarAsignadaA(TECNICO_ID, "Internet 300 MB");
        contratacion.marcarInstalada(LocalDate.now());

        mockMvc.perform(put("/api/instalaciones/" + contratacion.getId() + "/realizada").with(comoTecnico(TECNICO_ID)))
                .andExpect(status().isConflict());
    }

    @Test
    void unEmpleadoNoMarcaInstalaciones() throws Exception {
        Contratacion contratacion = guardarAsignadaA(TECNICO_ID, "Internet 300 MB");

        mockMvc.perform(put("/api/instalaciones/" + contratacion.getId() + "/realizada")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor comoTecnico(Long tecnicoId) {
        return jwt()
                .jwt(token -> token.subject("tecnico@telecom.com").claim("usuarioId", tecnicoId))
                .authorities(new SimpleGrantedAuthority("ROLE_TECNICO"));
    }

    private Contratacion guardarAsignadaA(Long tecnicoId, String plan) {
        Contratacion contratacion = new Contratacion(
                10L, 1L, plan, new BigDecimal("15000"), "Av. Siempre Viva 742", LocalDate.now());
        contratacion.asignarTecnico(tecnicoId, "Técnico " + tecnicoId);
        return contratacionRepository.save(contratacion);
    }
}
