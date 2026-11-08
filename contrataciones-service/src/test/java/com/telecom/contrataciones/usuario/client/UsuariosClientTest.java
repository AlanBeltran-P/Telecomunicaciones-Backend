package com.telecom.contrataciones.usuario.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.telecom.contrataciones.contratacion.exception.ClienteNoEncontradoException;
import com.telecom.contrataciones.contratacion.exception.UsuariosNoDisponibleException;
import com.telecom.contrataciones.usuario.dto.UsuarioDelSistema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class UsuariosClientTest {

    private static final String URL_USUARIO_10 = "http://usuarios-service/api/internos/usuarios/10";

    private MockRestServiceServer usuarios;
    private UsuariosClient usuariosClient;

    @BeforeEach
    void prepararUsuariosSimulado() {
        RestClient.Builder builder = RestClient.builder();
        usuarios = MockRestServiceServer.bindTo(builder).build();
        usuariosClient = new UsuariosClient(builder, "secreto-de-prueba-de-al-menos-32-caracteres");
    }

    @Test
    void devuelveAlClienteYMandaElTokenDeServicio() {
        usuarios.expect(requestTo(URL_USUARIO_10))
                .andExpect(header(HttpHeaders.COOKIE, startsWith("token=")))
                .andRespond(withSuccess(usuario("CLIENTE", true), MediaType.APPLICATION_JSON));

        UsuarioDelSistema cliente = usuariosClient.buscarClienteActivo(10L);

        assertThat(cliente.email()).isEqualTo("maria@mail.com");
    }

    @Test
    void unUsuarioQueNoEsClienteNoSirve() {
        usuarios.expect(requestTo(URL_USUARIO_10))
                .andRespond(withSuccess(usuario("TECNICO", true), MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> usuariosClient.buscarClienteActivo(10L))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void unClienteDesactivadoNoSirve() {
        usuarios.expect(requestTo(URL_USUARIO_10))
                .andRespond(withSuccess(usuario("CLIENTE", false), MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> usuariosClient.buscarClienteActivo(10L))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void siNoExisteLanzaClienteNoEncontrado() {
        usuarios.expect(requestTo(URL_USUARIO_10)).andRespond(withResourceNotFound());

        assertThatThrownBy(() -> usuariosClient.buscarClienteActivo(10L))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void siUsuariosFallaNoEstaDisponible() {
        usuarios.expect(requestTo(URL_USUARIO_10)).andRespond(withServerError());

        assertThatThrownBy(() -> usuariosClient.buscarClienteActivo(10L))
                .isInstanceOf(UsuariosNoDisponibleException.class);
    }

    private String usuario(String rol, boolean activo) {
        return """
                {"id": 10, "nombre": "María", "email": "maria@mail.com", "rol": "%s", "activo": %s}
                """.formatted(rol, activo);
    }
}
