package com.telecom.usuarios.usuario.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
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
class PerfilTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void elClienteVeSusPropiosDatos() throws Exception {
        Usuario cliente = usuarioRepository.save(
                new Usuario("Ana Pérez", "ana@mail.com", "hash", Rol.CLIENTE, "30123456", "+54 11 5555-1234"));

        mockMvc.perform(comoUsuario(cliente.getId(), "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nombre").value("Ana Pérez"))
                .andExpect(jsonPath("$.email").value("ana@mail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.dni").value("30123456"))
                .andExpect(jsonPath("$.telefono").value("+54 11 5555-1234"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void unTecnicoTambienVeSuPerfil() throws Exception {
        Usuario tecnico = usuarioRepository.save(new Usuario("Carlos Gómez", "carlos@telecom.com", "hash", Rol.TECNICO));

        mockMvc.perform(comoUsuario(tecnico.getId(), "TECNICO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("TECNICO"))
                .andExpect(jsonPath("$.dni").doesNotExist());
    }

    @Test
    void siElUsuarioYaNoExisteResponde404() throws Exception {
        mockMvc.perform(comoUsuario(999L, "CLIENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinSesionResponde401() throws Exception {
        mockMvc.perform(get("/api/usuarios/yo"))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequestBuilder comoUsuario(Long id, String rol) {
        return get("/api/usuarios/yo")
                .with(jwt()
                        .jwt(token -> token.subject("usuario@mail.com").claim("usuarioId", id))
                        .authorities(new SimpleGrantedAuthority("ROLE_" + rol)));
    }
}
