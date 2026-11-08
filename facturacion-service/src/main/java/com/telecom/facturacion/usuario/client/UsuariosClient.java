package com.telecom.facturacion.usuario.client;

import com.telecom.facturacion.usuario.dto.UsuarioDelSistema;
import com.telecom.seguridad.TokenDeServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// Consulta usuarios-service con el token de servicio (rol SISTEMA)
@Component
public class UsuariosClient {

    private static final String NOMBRE_DEL_SERVICIO = "facturacion-service";

    private final RestClient restClient;
    private final String secreto;

    public UsuariosClient(@LoadBalanced RestClient.Builder restClientBuilder, @Value("${jwt.secreto}") String secreto) {
        this.restClient = restClientBuilder.clone().baseUrl("http://usuarios-service").build();
        this.secreto = secreto;
    }

    // Puede lanzar RestClientException si usuarios-service no responde: lo maneja quien llama
    public UsuarioDelSistema buscar(Long usuarioId) {
        return restClient.get()
                .uri("/api/internos/usuarios/{id}", usuarioId)
                .header(HttpHeaders.COOKIE, TokenDeServicio.comoCookie(secreto, NOMBRE_DEL_SERVICIO))
                .retrieve()
                .body(UsuarioDelSistema.class);
    }
}
