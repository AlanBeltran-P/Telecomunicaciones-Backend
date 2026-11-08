package com.telecom.usuarios.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.usuarios.config.CookieDeSesion;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistroTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void unVisitanteSeRegistraYQuedaConLaSesionIniciada() throws Exception {
        registrar("María López", "maria@mail.com", "clave-segura", "30111222")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@mail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(cookie().exists(CookieDeSesion.NOMBRE))
                .andExpect(cookie().httpOnly(CookieDeSesion.NOMBRE, true));
    }

    @Test
    void laCuentaSeGuardaComoClienteConLaContrasenaEncriptada() throws Exception {
        registrar("María López", "maria@mail.com", "clave-segura", "30111222");

        Usuario cliente = usuarioRepository.findByEmail("maria@mail.com").orElseThrow();
        assertThat(cliente.getNombre()).isEqualTo("María López");
        assertThat(cliente.getRol()).isEqualTo(Rol.CLIENTE);
        assertThat(cliente.isActivo()).isTrue();
        assertThat(cliente.getContrasena()).startsWith("$2a$");
    }

    @Test
    void despuesDeRegistrarsePuedeIniciarSesion() throws Exception {
        registrar("María López", "maria@mail.com", "clave-segura", "30111222");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "maria@mail.com", "contrasena": "clave-segura"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void noSePuedeRegistrarUnEmailQueYaExiste() throws Exception {
        registrar("Otro", "admin@telecom.com", "clave-segura", "30111222")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("El email admin@telecom.com ya está en uso"))
                .andExpect(cookie().doesNotExist(CookieDeSesion.NOMBRE));
    }

    @Test
    void conDatosInvalidosResponde400() throws Exception {
        registrar("", "no-es-un-email", "corta", "ABC")
                .andExpect(status().isBadRequest());
    }

    @Test
    void elRegistroGuardaElDniYElTelefono() throws Exception {
        registrar("María López", "maria@mail.com", "clave-segura", "30111222");

        Usuario cliente = usuarioRepository.findByEmail("maria@mail.com").orElseThrow();
        assertThat(cliente.getDni()).isEqualTo("30111222");
        assertThat(cliente.getTelefono()).isEqualTo("11 4444-5555");
    }

    @Test
    void noSePuedeRegistrarUnDniQueYaExiste() throws Exception {
        registrar("María López", "maria@mail.com", "clave-segura", "30111222");

        registrar("Otra persona", "otra@mail.com", "clave-segura", "30111222")
                .andExpect(status().isConflict());
    }

    // El helper suma el DNI; el teléfono es fijo
    private ResultActions registrar(String nombre, String email, String contrasena, String dni) throws Exception {
        String cuerpo = """
                {"nombre": "%s", "email": "%s", "contrasena": "%s", "dni": "%s", "telefono": "11 4444-5555"}
                """.formatted(nombre, email, contrasena, dni);
        return mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));
    }
}
