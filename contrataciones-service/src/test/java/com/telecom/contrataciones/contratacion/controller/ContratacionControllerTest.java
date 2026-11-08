package com.telecom.contrataciones.contratacion.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.catalogo.client.CatalogoClient;
import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.exception.CatalogoNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanNoDisponibleException;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContratacionControllerTest {

    private static final Long CLIENTE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    // Simula catalogo-service: el test no depende de que esté levantado
    @MockitoBean
    private CatalogoClient catalogoClient;

    @BeforeEach
    void catalogoConDosPlanes() {
        when(catalogoClient.buscarPlan(1L))
                .thenReturn(new PlanDelCatalogo(1L, "Internet 300 MB", new BigDecimal("15000.50")));
        when(catalogoClient.buscarPlan(2L))
                .thenReturn(new PlanDelCatalogo(2L, "Telefonía fija", new BigDecimal("5000")));
    }

    @Test
    void elClienteContrataUnPlanYQuedaPendienteDeInstalacion() throws Exception {
        contratar(1L)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.planNombre").value("Internet 300 MB"))
                .andExpect(jsonPath("$.precioMensual").value(15000.5))
                .andExpect(jsonPath("$.direccion").value("Av. Siempre Viva 742"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_INSTALACION"))
                .andExpect(jsonPath("$.fechaContratacion").value(LocalDate.now().toString()));
    }

    @Test
    void laContratacionQuedaAsociadaAlClienteDelToken() throws Exception {
        contratar(1L);

        Contratacion guardada = contratacionRepository.findAll().getFirst();
        assertThat(guardada.getClienteId()).isEqualTo(CLIENTE_ID);
        assertThat(guardada.getDireccion()).isEqualTo("Av. Siempre Viva 742");
    }

    @Test
    void puedeContratarDistintosPlanes() throws Exception {
        contratar(1L).andExpect(status().isCreated());

        contratar(2L).andExpect(status().isCreated());
    }

    @Test
    void noPuedeContratarDosVecesElMismoPlan() throws Exception {
        contratar(1L);

        contratar(1L)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Ya tenés contratado el plan Internet 300 MB"));
    }

    @Test
    void enInglesElErrorSaleEnIngles() throws Exception {
        contratar(1L);

        mockMvc.perform(post("/api/contrataciones").with(comoCliente())
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1L)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("You already have the plan Internet 300 MB"));
    }

    @Test
    void unPlanInexistenteODadoDeBajaResponde404() throws Exception {
        when(catalogoClient.buscarPlan(99L)).thenThrow(new PlanNoDisponibleException(99L));

        contratar(99L).andExpect(status().isNotFound());
    }

    @Test
    void siElCatalogoNoRespondeResponde503() throws Exception {
        when(catalogoClient.buscarPlan(1L)).thenThrow(new CatalogoNoDisponibleException(new RuntimeException()));

        contratar(1L).andExpect(status().isServiceUnavailable());
    }

    @Test
    void sinDireccionResponde400() throws Exception {
        String cuerpo = """
                {"planId": 1, "direccion": ""}
                """;
        mockMvc.perform(post("/api/contrataciones").with(comoCliente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unEmpleadoNoContrataDesdeEsteEndpoint() throws Exception {
        mockMvc.perform(post("/api/contrataciones")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1L)))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinSesionResponde401() throws Exception {
        mockMvc.perform(post("/api/contrataciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1L)))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions contratar(Long planId) throws Exception {
        return mockMvc.perform(post("/api/contrataciones").with(comoCliente())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo(planId)));
    }

    private String cuerpo(Long planId) {
        return """
                {"planId": %d, "direccion": "Av. Siempre Viva 742"}
                """.formatted(planId);
    }

    private RequestPostProcessor comoCliente() {
        return jwt()
                .jwt(token -> token.subject("maria@mail.com").claim("usuarioId", CLIENTE_ID))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
