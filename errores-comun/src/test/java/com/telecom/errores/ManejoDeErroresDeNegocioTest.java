package com.telecom.errores;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Historia 15: los mensajes salen en inglés o en español según Accept-Language
@SpringBootTest(classes = AplicacionDePrueba.class)
@AutoConfigureMockMvc
class ManejoDeErroresDeNegocioTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinIdiomaElMensajeSaleEnEspanol() throws Exception {
        mockMvc.perform(get("/planes/1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe un plan activo con id 1000"));
    }

    @Test
    void enInglesElMensajeSaleEnIngles() throws Exception {
        mockMvc.perform(get("/planes/1000").header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("There is no active plan with id 1000"));
    }

    @Test
    void unIdiomaNoSoportadoSaleEnEspanol() throws Exception {
        mockMvc.perform(get("/planes/1").header(HttpHeaders.ACCEPT_LANGUAGE, "fr"))
                .andExpect(jsonPath("$.detail").value("No existe un plan activo con id 1"));
    }

    @Test
    void losErroresDeValidacionSeTraducenPorCampo() throws Exception {
        mockMvc.perform(post("/planes")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid data"))
                .andExpect(jsonPath("$.errores.nombre").value("is required"));
    }

    @Test
    void losErroresDeValidacionEnEspanol() throws Exception {
        mockMvc.perform(post("/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(jsonPath("$.detail").value("Datos inválidos"))
                .andExpect(jsonPath("$.errores.nombre").value("es obligatorio"));
    }
}
