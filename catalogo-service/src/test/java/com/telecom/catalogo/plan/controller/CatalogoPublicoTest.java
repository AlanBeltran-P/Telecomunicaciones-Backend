package com.telecom.catalogo.plan.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.catalogo.config.CookieTokenResolver;
import com.telecom.catalogo.plan.entity.Plan;
import com.telecom.catalogo.plan.repository.PlanRepository;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogoPublicoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void unVisitanteVeLosPlanesActivosSinIniciarSesion() throws Exception {
        crearPlan("Internet 300 MB");
        Plan dadoDeBaja = crearPlan("Plan viejo");
        dadoDeBaja.darDeBaja();

        mockMvc.perform(get("/api/planes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Internet 300 MB"));
    }

    @Test
    void unVisitanteVeElDetalleDeUnPlan() throws Exception {
        Plan plan = crearPlan("Internet 300 MB");

        mockMvc.perform(get("/api/planes/" + plan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Internet 300 MB"));
    }

    @Test
    void conUnaCookieVencidaOInvalidaElCatalogoSigueVisible() throws Exception {
        Cookie cookieInvalida = new Cookie(CookieTokenResolver.NOMBRE_COOKIE, "token-vencido");

        mockMvc.perform(get("/api/planes").cookie(cookieInvalida))
                .andExpect(status().isOk());
    }

    @Test
    void unVisitanteNoPuedeCrearPlanes() throws Exception {
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Plan pirata", "precio": 1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private Plan crearPlan(String nombre) {
        return planRepository.save(new Plan(nombre, null, new BigDecimal("10000")));
    }
}
