package com.telecom.usuarios.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerTest {

    // Lo crea AdministradorInicial con la configuración de test
    private static final String EMAIL_ADMINISTRADOR = "admin@telecom.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void elAdministradorDaDeAltaUnEmpleado() throws Exception {
        crear("Ana Pérez", "ana@telecom.com", "EMPLEADO")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Ana Pérez"))
                .andExpect(jsonPath("$.rol").value("EMPLEADO"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void elAdministradorDaDeAltaUnTecnico() throws Exception {
        crear("Juan Gómez", "juan@telecom.com", "TECNICO")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("TECNICO"));
    }

    @Test
    void laContrasenaSeGuardaEncriptada() throws Exception {
        crear("Ana Pérez", "ana@telecom.com", "EMPLEADO");

        Usuario guardado = usuarioRepository.findByEmail("ana@telecom.com").orElseThrow();
        assertThat(guardado.getContrasena()).startsWith("$2a$").isNotEqualTo("clave-segura");
    }

    @Test
    void noSePuedeDarDeAltaUnClienteDesdeLaGestionDeUsuarios() throws Exception {
        crear("Cliente", "cliente@mail.com", "CLIENTE")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El rol CLIENTE no se asigna desde la gestión de usuarios"));
    }

    @Test
    void noSePuedeRepetirUnEmail() throws Exception {
        crear("Ana Pérez", "ana@telecom.com", "EMPLEADO");

        crear("Otra Ana", "ana@telecom.com", "TECNICO")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("El email ana@telecom.com ya está en uso"));
    }

    @Test
    void conUnaContrasenaCortaResponde400() throws Exception {
        String cuerpo = """
                {"nombre": "Ana", "email": "ana@telecom.com", "contrasena": "corta", "rol": "EMPLEADO"}
                """;
        mockMvc.perform(comoAdministrador(post("/api/usuarios")).content(cuerpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void elListadoMuestraAlPersonalPeroNoALosClientes() throws Exception {
        crear("Ana Pérez", "ana@telecom.com", "EMPLEADO");
        usuarioRepository.save(new Usuario("Cliente", "cliente@mail.com", "hash", Rol.CLIENTE));

        mockMvc.perform(comoAdministrador(get("/api/usuarios")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(EMAIL_ADMINISTRADOR, "ana@telecom.com")));
    }

    @Test
    void elAdministradorCambiaUnEmpleadoATecnico() throws Exception {
        Usuario empleado = guardar("ana@telecom.com", Rol.EMPLEADO);

        mockMvc.perform(comoAdministrador(put("/api/usuarios/" + empleado.getId() + "/rol"))
                        .content("""
                                {"rol": "TECNICO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("TECNICO"));
    }

    @Test
    void cambiarElRolDeUnUsuarioInexistenteResponde404() throws Exception {
        mockMvc.perform(comoAdministrador(put("/api/usuarios/999/rol"))
                        .content("""
                                {"rol": "TECNICO"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void elAdministradorDesactivaAUnEmpleado() throws Exception {
        Usuario empleado = guardar("ana@telecom.com", Rol.EMPLEADO);

        mockMvc.perform(comoAdministrador(put("/api/usuarios/" + empleado.getId() + "/desactivar")))
                .andExpect(status().isNoContent());

        assertThat(usuarioRepository.findById(empleado.getId()).orElseThrow().isActivo()).isFalse();
    }

    @Test
    void unAdministradorNoPuedeDesactivarseASiMismo() throws Exception {
        Usuario administrador = usuarioRepository.findByEmail(EMAIL_ADMINISTRADOR).orElseThrow();

        mockMvc.perform(comoAdministrador(put("/api/usuarios/" + administrador.getId() + "/desactivar")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unAdministradorNoPuedeCambiarseSuPropioRol() throws Exception {
        Usuario administrador = usuarioRepository.findByEmail(EMAIL_ADMINISTRADOR).orElseThrow();

        mockMvc.perform(comoAdministrador(put("/api/usuarios/" + administrador.getId() + "/rol"))
                        .content("""
                                {"rol": "EMPLEADO"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unEmpleadoNoPuedeGestionarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinSesionResponde401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions crear(String nombre, String email, String rol) throws Exception {
        String cuerpo = """
                {"nombre": "%s", "email": "%s", "contrasena": "clave-segura", "rol": "%s"}
                """.formatted(nombre, email, rol);
        return mockMvc.perform(comoAdministrador(post("/api/usuarios")).content(cuerpo));
    }

    private MockHttpServletRequestBuilder comoAdministrador(MockHttpServletRequestBuilder request) {
        return request
                .with(jwt()
                        .jwt(token -> token.subject(EMAIL_ADMINISTRADOR))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private Usuario guardar(String email, Rol rol) {
        return usuarioRepository.save(new Usuario("Nombre", email, "hash", rol));
    }
}
