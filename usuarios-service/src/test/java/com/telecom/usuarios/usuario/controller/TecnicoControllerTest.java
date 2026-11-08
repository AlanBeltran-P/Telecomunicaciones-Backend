package com.telecom.usuarios.usuario.controller;

import static org.hamcrest.Matchers.contains;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TecnicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void elEmpleadoVeSoloLosTecnicosActivos() throws Exception {
        usuarioRepository.save(new Usuario("Juan Gómez", "juan@telecom.com", "hash", Rol.TECNICO));
        Usuario desactivado = new Usuario("Pedro Ruiz", "pedro@telecom.com", "hash", Rol.TECNICO);
        desactivado.desactivar();
        usuarioRepository.save(desactivado);
        usuarioRepository.save(new Usuario("Ana Pérez", "ana@telecom.com", "hash", Rol.EMPLEADO));

        mockMvc.perform(get("/api/tecnicos").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", contains("juan@telecom.com")));
    }

    @Test
    void unClienteNoVeLosTecnicos() throws Exception {
        mockMvc.perform(get("/api/tecnicos").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }
}
