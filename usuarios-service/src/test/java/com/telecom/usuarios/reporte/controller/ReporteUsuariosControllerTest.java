package com.telecom.usuarios.reporte.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
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
class ReporteUsuariosControllerTest {

    private static final LocalDate HOY = LocalDate.now();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void guardarUsuarios() {
        usuarioRepository.save(new Usuario("Ana", "ana@mail.com", "hash", Rol.CLIENTE, "30123456", "1155551234"));
        usuarioRepository.save(new Usuario("Marcos", "marcos@mail.com", "hash", Rol.CLIENTE, "28987654", "3514449876"));
        usuarioRepository.save(new Usuario("Lucía", "lucia@telecom.com", "hash", Rol.EMPLEADO));
    }

    @Test
    void cuentaLosClientesQueSeDieronDeAltaEnElPeriodo() throws Exception {
        mockMvc.perform(reporte(HOY, HOY, "DIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientesNuevos").value(2))
                .andExpect(jsonPath("$.clientesActivos").value(2))
                .andExpect(jsonPath("$.altasDeClientes", hasSize(1)))
                .andExpect(jsonPath("$.altasDeClientes[0].periodo").value(HOY.toString()))
                .andExpect(jsonPath("$.altasDeClientes[0].cantidad").value(2));
    }

    @Test
    void elPersonalActivoSeCuentaPorRol() throws Exception {
        // El administrador inicial lo crea AdministradorInicial con la configuración de test
        mockMvc.perform(reporte(HOY, HOY, "DIA"))
                .andExpect(jsonPath("$.personalActivo.administradores").value(1))
                .andExpect(jsonPath("$.personalActivo.empleados").value(1))
                .andExpect(jsonPath("$.personalActivo.tecnicos").value(0));
    }

    @Test
    void unPeriodoSinAltasDevuelveLaSerieEnCero() throws Exception {
        LocalDate desde = HOY.minusDays(10);
        LocalDate hasta = HOY.minusDays(8);

        mockMvc.perform(reporte(desde, hasta, "DIA"))
                .andExpect(jsonPath("$.clientesNuevos").value(0))
                .andExpect(jsonPath("$.altasDeClientes", hasSize(3)))
                .andExpect(jsonPath("$.altasDeClientes[0].periodo").value(desde.toString()))
                .andExpect(jsonPath("$.altasDeClientes[2].cantidad").value(0));
    }

    @Test
    void agrupadoPorMesDevuelveUnPuntoPorMes() throws Exception {
        LocalDate desde = HOY.minusMonths(2).withDayOfMonth(1);
        LocalDate hasta = HOY.withDayOfMonth(HOY.lengthOfMonth());

        mockMvc.perform(reporte(desde, hasta, "MES"))
                .andExpect(jsonPath("$.altasDeClientes", hasSize(3)))
                .andExpect(jsonPath("$.altasDeClientes[2].periodo").value(HOY.toString().substring(0, 7)))
                .andExpect(jsonPath("$.altasDeClientes[2].cantidad").value(2));
    }

    @Test
    void siDesdeEsPosteriorAHastaResponde400() throws Exception {
        mockMvc.perform(reporte(HOY, HOY.minusDays(1), "DIA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "La fecha desde (%s) no puede ser posterior a la fecha hasta (%s)".formatted(HOY, HOY.minusDays(1))));
    }

    @Test
    void unEmpleadoNoPuedeVerElReporte() throws Exception {
        mockMvc.perform(get("/api/usuarios/reporte")
                        .param("desde", HOY.toString())
                        .param("hasta", HOY.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLEADO"))))
                .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder reporte(LocalDate desde, LocalDate hasta, String agrupacion) {
        return get("/api/usuarios/reporte")
                .param("desde", desde.toString())
                .param("hasta", hasta.toString())
                .param("agrupacion", agrupacion)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
    }
}
