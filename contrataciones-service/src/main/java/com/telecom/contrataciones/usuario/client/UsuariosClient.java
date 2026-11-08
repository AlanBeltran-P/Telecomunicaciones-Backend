package com.telecom.contrataciones.usuario.client;

import com.telecom.contrataciones.contratacion.exception.ClienteNoEncontradoException;
import com.telecom.contrataciones.contratacion.exception.UsuariosNoDisponibleException;
import com.telecom.contrataciones.instalacion.exception.TecnicoNoEncontradoException;
import com.telecom.contrataciones.usuario.dto.UsuarioDelSistema;
import com.telecom.seguridad.TokenDeServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Consulta usuarios-service con el token de servicio (rol SISTEMA)
@Component
public class UsuariosClient {

    private static final String NOMBRE_DEL_SERVICIO = "contrataciones-service";

    private final RestClient restClient;
    private final String secreto;

    public UsuariosClient(@LoadBalanced RestClient.Builder restClientBuilder, @Value("${jwt.secreto}") String secreto) {
        this.restClient = restClientBuilder.clone().baseUrl("http://usuarios-service").build();
        this.secreto = secreto;
    }

    public UsuarioDelSistema buscarClienteActivo(Long clienteId) {
        UsuarioDelSistema usuario = buscar(clienteId, new ClienteNoEncontradoException(clienteId));
        if (!usuario.esClienteActivo()) {
            throw new ClienteNoEncontradoException(clienteId);
        }
        return usuario;
    }

    public UsuarioDelSistema buscarTecnicoActivo(Long tecnicoId) {
        UsuarioDelSistema usuario = buscar(tecnicoId, new TecnicoNoEncontradoException(tecnicoId));
        if (!usuario.esTecnicoActivo()) {
            throw new TecnicoNoEncontradoException(tecnicoId);
        }
        return usuario;
    }

    // siNoExiste: la excepción a lanzar si usuarios-service responde 404
    private UsuarioDelSistema buscar(Long id, RuntimeException siNoExiste) {
        try {
            return restClient.get()
                    .uri("/api/internos/usuarios/{id}", id)
                    .header(HttpHeaders.COOKIE, TokenDeServicio.comoCookie(secreto, NOMBRE_DEL_SERVICIO))
                    .retrieve()
                    .body(UsuarioDelSistema.class);
        } catch (HttpClientErrorException.NotFound error) {
            throw siNoExiste;
        } catch (RestClientException error) {
            throw new UsuariosNoDisponibleException(error);
        }
    }
}
