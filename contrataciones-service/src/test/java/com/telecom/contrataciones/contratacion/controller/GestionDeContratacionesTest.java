package com.telecom.contrataciones.contratacion.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.contrataciones.catalogo.client.CatalogoClient;
import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.exception.ClienteNoEncontradoException;
import com.telecom.contrataciones.contratacion.exception.UsuariosNoDisponibleException;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import com.telecom.contrataciones.usuario.client.UsuariosClient;
import com.telecom.contrataciones.usuario.dto.UsuarioDelSistema;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

// Historia 7: el empleado asigna o quita planes a un cliente
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GestionDeContratacionesTest {

    private static final Long CLIENTE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @MockitoBean
    private CatalogoClient catalogoClient;

    @MockitoBean
    private UsuariosClient usuariosClient;

    @BeforeEach
    void prepararServiciosSimulados() {
        when(catalogoClient.buscarPlan(1L))
                .thenReturn(new PlanDelCatalogo(1L, "Internet 300 MB", new BigDecimal("15000")));
        when(usuariosClient.buscarClienteActivo(CLIENTE_ID))
                .thenReturn(new UsuarioDelSistema(CLIENTE_ID, "María", "maria@mail.com", "CLIENTE", true));
    }

    @Test
    void elEmpleadoLeAsignaUnPlanAUnCliente() throws Exception {
        asignarPlan(CLIENTE_ID)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.planNombre").value("Internet 300 MB"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_INSTALACION"));
    }

    @Test
    void noSePuedeAsignarUnPlanAUnClienteQueNoExiste() throws Exception {
        when(usuariosClient.buscarClienteActivo(99L)).thenThrow(new ClienteNoEncontradoException(99L));

        asignarPlan(99L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe un cliente activo con id 99"));
    }

    @Test
    void siUsuariosNoRespondeResponde503() throws Exception {
        when(usuariosClient.buscarClienteActivo(CLIENTE_ID))
                .thenThrow(new UsuariosNoDisponibleException(new RuntimeException()));

        asignarPlan(CLIENTE_ID).andExpect(status().isServiceUnavailable());
    }

    @Test
    void elEmpleadoVeLasContratacionesDeUnCliente() throws Exception {
        guardarContratacion(CLIENTE_ID);
        guardarContratacion(20L);

        mockMvc.perform(comoEmpleado(get("/api/contrataciones/clientes/" + CLIENTE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clienteId").value(CLIENTE_ID));
    }

    @Test
    void elEmpleadoLeQuitaUnPlanAUnCliente() throws Exception {
        Contratacion contratacion = guardarContratacion(CLIENTE_ID);

        mockMvc.perform(comoEmpleado(put("/api/contrataciones/" + contratacion.getId() + "/baja")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("BAJA"))
                .andExpect(jsonPath("$.fechaBaja").value(LocalDate.now().toString()));
    }

    @Test
    void noSePuedeDarDeBajaDosVeces() throws Exception {
        Contratacion contratacion = guardarContratacion(CLIENTE_ID);
        contratacion.darDeBaja(LocalDate.now());

        mockMvc.perform(comoEmpleado(put("/api/contrataciones/" + contratacion.getId() + "/baja")))
                .andExpect(status().isConflict());
    }

    @Test
    void darDeBajaUnaContratacionInexistenteResponde404() throws Exception {
        mockMvc.perform(comoEmpleado(put("/api/contrataciones/999/baja")))
                .andExpect(status().isNotFound());
    }

    @Test
    void despuesDeLaBajaElClientePuedeVolverAContratarElPlan() throws Exception {
        Contratacion contratacion = guardarContratacion(CLIENTE_ID);
        contratacion.darDeBaja(LocalDate.now());

        asignarPlan(CLIENTE_ID).andExpect(status().isCreated());
    }

    @Test
    void unClienteNoPuedeQuitarPlanes() throws Exception {
        Contratacion contratacion = guardarContratacion(CLIENTE_ID);

        mockMvc.perform(put("/api/contrataciones/" + contratacion.getId() + "/baja")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }

    private ResultActions asignarPlan(Long clienteId) throws Exception {
        return mockMvc.perform(comoEmpleado(post("/api/contrataciones/clientes/" + clienteId))
                .content("""
                        {"planId": 1, "direccion": "Av. Siempre Viva 742"}
                        """));
    }

    private MockHttpServletRequestBuilder comoEmpleado(MockHttpServletRequestBuilder request) {
        return request
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO")))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private Contratacion guardarContratacion(Long clienteId) {
        return contratacionRepository.save(new Contratacion(
                clienteId, 1L, "Internet 300 MB", new BigDecimal("15000"), "Av. Siempre Viva 742", LocalDate.now()));
    }
}
