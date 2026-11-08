package com.telecom.catalogo.plan.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.catalogo.plan.entity.Plan;
import com.telecom.catalogo.plan.repository.PlanRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanControllerTest {

    private static final String PLAN_JSON = """
            {"nombre": "Internet 300 MB", "descripcion": "Fibra óptica", "precio": 15000.50}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void elAdministradorCreaUnPlan() throws Exception {
        mockMvc.perform(comoAdministrador(post("/api/planes")).content(PLAN_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Internet 300 MB"))
                .andExpect(jsonPath("$.precio").value(15000.5));
    }

    @Test
    void noSePuedeCrearUnPlanConPrecioNegativo() throws Exception {
        String json = """
                {"nombre": "Plan inválido", "precio": -10}
                """;
        mockMvc.perform(comoAdministrador(post("/api/planes")).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void elAdministradorListaLosPlanesActivos() throws Exception {
        crearPlan("Internet 300 MB");
        crearPlan("Telefonía fija");

        mockMvc.perform(comoAdministrador(get("/api/planes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void elAdministradorEditaUnPlan() throws Exception {
        Plan plan = crearPlan("Internet 300 MB");
        String json = """
                {"nombre": "Internet 500 MB", "descripcion": "Fibra óptica", "precio": 20000}
                """;

        mockMvc.perform(comoAdministrador(put("/api/planes/" + plan.getId())).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Internet 500 MB"))
                .andExpect(jsonPath("$.precio").value(20000));
    }

    @Test
    void elPlanDadoDeBajaYaNoApareceEnElCatalogo() throws Exception {
        Plan plan = crearPlan("Internet 300 MB");

        mockMvc.perform(comoAdministrador(delete("/api/planes/" + plan.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(comoAdministrador(get("/api/planes/" + plan.getId())))
                .andExpect(status().isNotFound());
        mockMvc.perform(comoAdministrador(get("/api/planes")))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void elPlanInexistenteSeInformaEnElIdiomaPedido() throws Exception {
        mockMvc.perform(comoAdministrador(put("/api/planes/999")).content(PLAN_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe un plan activo con id 999"));

        mockMvc.perform(comoAdministrador(put("/api/planes/999")).content(PLAN_JSON)
                        .header("Accept-Language", "en"))
                .andExpect(jsonPath("$.detail").value("There is no active plan with id 999"));
    }

    @Test
    void editarUnPlanInexistenteResponde404() throws Exception {
        mockMvc.perform(comoAdministrador(put("/api/planes/999")).content(PLAN_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void unEmpleadoNoPuedeGestionarPlanes() throws Exception {
        mockMvc.perform(post("/api/planes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLAN_JSON))
                .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder comoAdministrador(MockHttpServletRequestBuilder request) {
        return request
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private Plan crearPlan(String nombre) {
        return planRepository.save(new Plan(nombre, null, new BigDecimal("10000")));
    }
}
