package com.telecom.usuarios.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.usuarios.config.CookieDeSesion;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    void unUsuarioDesactivadoNoPuedeIniciarSesion() throws Exception {
        Usuario empleado = new Usuario("Ana", "ana@telecom.com", passwordEncoder.encode("clave-segura"), Rol.EMPLEADO);
        empleado.desactivar();
        usuarioRepository.save(empleado);

        login("ana@telecom.com", "clave-segura")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conUnaCookieVencidaSePuedeVolverAIniciarSesion() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .cookie(new Cookie(CookieDeSesion.NOMBRE, "token-vencido"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@telecom.com", "contrasena": "admin123"}
                                """))
                .andExpect(status().isOk());
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
