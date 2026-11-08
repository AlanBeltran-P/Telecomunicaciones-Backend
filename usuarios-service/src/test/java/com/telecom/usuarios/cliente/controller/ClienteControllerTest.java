package com.telecom.usuarios.cliente.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void elEmpleadoDaDeAltaUnCliente() throws Exception {
        crear("María López", "maria@mail.com", "30111222")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("María López"))
                .andExpect(jsonPath("$.dni").value("30111222"))
                .andExpect(jsonPath("$.telefono").value("11 4444-5555"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void elClienteQuedaConRolClienteYPuedeIniciarSesion() throws Exception {
        crear("María López", "maria@mail.com", "30111222");

        Usuario cliente = usuarioRepository.findByEmail("maria@mail.com").orElseThrow();
        assertThat(cliente.getRol()).isEqualTo(Rol.CLIENTE);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "maria@mail.com", "contrasena": "clave-segura"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void noSePuedeRepetirElEmail() throws Exception {
        crear("María López", "maria@mail.com", "30111222");

        crear("Otra María", "maria@mail.com", "30999888")
                .andExpect(status().isConflict());
    }

    @Test
    void noSePuedeRepetirElDni() throws Exception {
        crear("María López", "maria@mail.com", "30111222");

        crear("Otra persona", "otra@mail.com", "30111222")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("El DNI 30111222 ya está registrado"));
    }

    @Test
    void enInglesElErrorSaleEnIngles() throws Exception {
        crear("María López", "maria@mail.com", "30111222");

        mockMvc.perform(comoEmpleado(post("/api/clientes"))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .content("""
                                {"nombre": "Otra", "email": "otra@mail.com", "contrasena": "clave-segura",
                                 "dni": "30111222", "telefono": "11 4444-5555"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("The ID number 30111222 is already registered"));
    }

    @Test
    void laValidacionDelDniSeTraduce() throws Exception {
        mockMvc.perform(comoEmpleado(post("/api/clientes"))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .content("""
                                {"nombre": "María", "email": "maria@mail.com", "contrasena": "clave-segura",
                                 "dni": "ABC", "telefono": "11 4444-5555"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.dni").value("must have 7 or 8 digits"));
    }

    @Test
    void conUnDniInvalidoResponde400() throws Exception {
        crear("María López", "maria@mail.com", "ABC")
                .andExpect(status().isBadRequest());
    }

    @Test
    void sinBusquedaListaSoloLosClientes() throws Exception {
        crear("María López", "maria@mail.com", "30111222");
        crear("Juan Pérez", "juan@mail.com", "30333444");

        mockMvc.perform(comoEmpleado(get("/api/clientes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("maria@mail.com", "juan@mail.com")));
    }

    @Test
    void buscaPorParteDelNombreSinImportarMayusculas() throws Exception {
        crear("María López", "maria@mail.com", "30111222");
        crear("Juan Pérez", "juan@mail.com", "30333444");

        mockMvc.perform(comoEmpleado(get("/api/clientes").param("busqueda", "lóPEZ")))
                .andExpect(jsonPath("$[*].email", contains("maria@mail.com")));
    }

    @Test
    void buscaPorParteDelEmail() throws Exception {
        crear("María López", "maria@mail.com", "30111222");
        crear("Juan Pérez", "juan@otro.com", "30333444");

        mockMvc.perform(comoEmpleado(get("/api/clientes").param("busqueda", "otro.com")))
                .andExpect(jsonPath("$[*].email", contains("juan@otro.com")));
    }

    @Test
    void buscaPorDniExacto() throws Exception {
        crear("María López", "maria@mail.com", "30111222");
        crear("Juan Pérez", "juan@mail.com", "30333444");

        mockMvc.perform(comoEmpleado(get("/api/clientes").param("busqueda", "30333444")))
                .andExpect(jsonPath("$[*].email", contains("juan@mail.com")));
    }

    @Test
    void laBusquedaNoDevuelvePersonal() throws Exception {
        mockMvc.perform(comoEmpleado(get("/api/clientes").param("busqueda", "admin")))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void elEmpleadoVeUnCliente() throws Exception {
        Usuario cliente = guardarCliente("maria@mail.com", "30111222");

        mockMvc.perform(comoEmpleado(get("/api/clientes/" + cliente.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@mail.com"));
    }

    @Test
    void unUsuarioDelPersonalNoSeVeComoCliente() throws Exception {
        Usuario administrador = usuarioRepository.findByEmail("admin@telecom.com").orElseThrow();

        mockMvc.perform(comoEmpleado(get("/api/clientes/" + administrador.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void elEmpleadoEditaLosDatosDeUnCliente() throws Exception {
        Usuario cliente = guardarCliente("maria@mail.com", "30111222");

        editar(cliente.getId(), "María L. Gómez", "maria.gomez@mail.com", "30111222")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("María L. Gómez"))
                .andExpect(jsonPath("$.email").value("maria.gomez@mail.com"));
    }

    @Test
    void alEditarNoPuedeUsarElEmailDeOtroUsuario() throws Exception {
        Usuario cliente = guardarCliente("maria@mail.com", "30111222");

        editar(cliente.getId(), "María", "admin@telecom.com", "30111222")
                .andExpect(status().isConflict());
    }

    @Test
    void editarUnClienteInexistenteResponde404() throws Exception {
        editar(999L, "Nadie", "nadie@mail.com", "30111222")
                .andExpect(status().isNotFound());
    }

    @Test
    void unAdministradorNoGestionaClientes() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinSesionResponde401() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions crear(String nombre, String email, String dni) throws Exception {
        String cuerpo = """
                {"nombre": "%s", "email": "%s", "contrasena": "clave-segura", "dni": "%s", "telefono": "11 4444-5555"}
                """.formatted(nombre, email, dni);
        return mockMvc.perform(comoEmpleado(post("/api/clientes")).content(cuerpo));
    }

    private ResultActions editar(Long id, String nombre, String email, String dni) throws Exception {
        String cuerpo = """
                {"nombre": "%s", "email": "%s", "dni": "%s", "telefono": "11 4444-5555"}
                """.formatted(nombre, email, dni);
        return mockMvc.perform(comoEmpleado(put("/api/clientes/" + id)).content(cuerpo));
    }

    private MockHttpServletRequestBuilder comoEmpleado(MockHttpServletRequestBuilder request) {
        return request
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO")))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private Usuario guardarCliente(String email, String dni) {
        return usuarioRepository.save(new Usuario("María", email, "hash", Rol.CLIENTE, dni, "11 4444-5555"));
    }
}
