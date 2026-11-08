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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioInternoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void otroServicioConsultaLosDatosDeUnCliente() throws Exception {
        Usuario cliente = usuarioRepository.save(
                new Usuario("María", "maria@mail.com", "hash", Rol.CLIENTE, "30111222", "11 4444-5555"));

        mockMvc.perform(get("/api/internos/usuarios/" + cliente.getId()).with(comoSistema()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@mail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.dni").value("30111222"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void unUsuarioInexistenteResponde404() throws Exception {
        mockMvc.perform(get("/api/internos/usuarios/999").with(comoSistema()))
                .andExpect(status().isNotFound());
    }

    @Test
    void unUsuarioNoPuedeUsarLasRutasInternas() throws Exception {
        mockMvc.perform(get("/api/internos/usuarios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor comoSistema() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_SISTEMA"));
    }
}
