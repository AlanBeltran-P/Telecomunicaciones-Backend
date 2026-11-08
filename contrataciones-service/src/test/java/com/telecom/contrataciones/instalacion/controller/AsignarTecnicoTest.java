package com.telecom.contrataciones.instalacion.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import com.telecom.contrataciones.instalacion.exception.TecnicoNoEncontradoException;
import com.telecom.contrataciones.usuario.client.UsuariosClient;
import com.telecom.contrataciones.usuario.dto.UsuarioDelSistema;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

// Historia 8: el empleado ve las instalaciones pendientes y elige el técnico
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AsignarTecnicoTest {

    private static final Long TECNICO_ID = 30L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @MockitoBean
    private UsuariosClient usuariosClient;

    @Test
    void elEmpleadoVeLasInstalacionesPendientesDeLaMasAntigua() throws Exception {
        guardarContratacion("Internet 300 MB", LocalDate.now());
        guardarContratacion("Telefonía fija", LocalDate.now().minusDays(3));
        Contratacion dadaDeBaja = guardarContratacion("TV", LocalDate.now());
        dadaDeBaja.darDeBaja(LocalDate.now());

        mockMvc.perform(comoEmpleado(get("/api/instalaciones/pendientes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].planNombre").value("Telefonía fija"))
                .andExpect(jsonPath("$[0].direccion").value("Av. Siempre Viva 742"));
    }

    @Test
    void elEmpleadoEligeElTecnicoDeUnaInstalacion() throws Exception {
        Contratacion contratacion = guardarContratacion("Internet 300 MB", LocalDate.now());
        when(usuariosClient.buscarTecnicoActivo(TECNICO_ID))
                .thenReturn(new UsuarioDelSistema(TECNICO_ID, "Juan Gómez", "juan@telecom.com", "TECNICO", true));

        asignar(contratacion.getId(), TECNICO_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contratacionId").value(contratacion.getId()))
                .andExpect(jsonPath("$.tecnicoId").value(TECNICO_ID))
                .andExpect(jsonPath("$.tecnicoNombre").value("Juan Gómez"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_INSTALACION"));
    }

    @Test
    void unTecnicoInexistenteODesactivadoResponde404() throws Exception {
        Contratacion contratacion = guardarContratacion("Internet 300 MB", LocalDate.now());
        when(usuariosClient.buscarTecnicoActivo(99L)).thenThrow(new TecnicoNoEncontradoException(99L));

        asignar(contratacion.getId(), 99L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe un técnico activo con id 99"));
    }

    @Test
    void noSeAsignaTecnicoAUnaContratacionDadaDeBaja() throws Exception {
        Contratacion contratacion = guardarContratacion("Internet 300 MB", LocalDate.now());
        contratacion.darDeBaja(LocalDate.now());

        asignar(contratacion.getId(), TECNICO_ID).andExpect(status().isConflict());
    }

    @Test
    void unaContratacionInexistenteResponde404() throws Exception {
        asignar(999L, TECNICO_ID).andExpect(status().isNotFound());
    }

    @Test
    void unTecnicoNoPuedeAsignarseInstalaciones() throws Exception {
        mockMvc.perform(get("/api/instalaciones/pendientes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_TECNICO"))))
                .andExpect(status().isForbidden());
    }

    private ResultActions asignar(Long contratacionId, Long tecnicoId) throws Exception {
        return mockMvc.perform(comoEmpleado(put("/api/instalaciones/" + contratacionId + "/tecnico"))
                .content("{\"tecnicoId\": " + tecnicoId + "}"));
    }

    private MockHttpServletRequestBuilder comoEmpleado(MockHttpServletRequestBuilder request) {
        return request
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO")))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private Contratacion guardarContratacion(String plan, LocalDate fecha) {
        return contratacionRepository.save(new Contratacion(
                10L, 1L, plan, new BigDecimal("15000"), "Av. Siempre Viva 742", fecha));
    }
}
