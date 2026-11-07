package com.telecom.usuarios.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elAdministradorIniciaSesionYRecibeLaCookie() throws Exception {
        login("admin@telecom.com", "admin123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@telecom.com"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(cookie().exists(CookieDeSesion.NOMBRE))
                .andExpect(cookie().httpOnly(CookieDeSesion.NOMBRE, true))
                .andExpect(cookie().sameSite(CookieDeSesion.NOMBRE, "Strict"));
    }

    @Test
    void conContrasenaIncorrectaResponde401() throws Exception {
        login("admin@telecom.com", "otra")
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist(CookieDeSesion.NOMBRE));
    }

    @Test
    void conEmailInexistenteResponde401() throws Exception {
        login("nadie@telecom.com", "admin123")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conEmailInvalidoResponde400() throws Exception {
        login("no-es-un-email", "admin123")
                .andExpect(status().isBadRequest());
    }

    @Test
    void elLogoutBorraLaCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(CookieDeSesion.NOMBRE, 0));
    }

    private ResultActions login(String email, String contrasena) throws Exception {
        String cuerpo = """
                {"email": "%s", "contrasena": "%s"}
                """.formatted(email, contrasena);
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));
    }
}
