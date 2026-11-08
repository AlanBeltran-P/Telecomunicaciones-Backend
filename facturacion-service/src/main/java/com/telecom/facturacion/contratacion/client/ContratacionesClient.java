package com.telecom.facturacion.contratacion.client;

import com.telecom.facturacion.contratacion.dto.ContratacionFacturable;
import com.telecom.facturacion.contratacion.exception.ContratacionesNoDisponibleException;
import com.telecom.seguridad.TokenDeServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Consulta contrataciones-service con el token de servicio (rol SISTEMA)
@Component
public class ContratacionesClient {

    private static final String NOMBRE_DEL_SERVICIO = "facturacion-service";

    private final RestClient restClient;
    private final String secreto;

    public ContratacionesClient(
            @LoadBalanced RestClient.Builder restClientBuilder,
            @Value("${jwt.secreto}") String secreto) {
        this.restClient = restClientBuilder.clone().baseUrl("http://contrataciones-service").build();
        this.secreto = secreto;
    }

    public void suspender(Long contratacionId) {
        try {
            restClient.put()
                    .uri("/api/internos/contrataciones/{id}/suspension", contratacionId)
                    .header(HttpHeaders.COOKIE, TokenDeServicio.comoCookie(secreto, NOMBRE_DEL_SERVICIO))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException error) {
            throw new ContratacionesNoDisponibleException(error);
        }
    }

    public List<ContratacionFacturable> listarFacturables() {
        try {
            return restClient.get()
                    .uri("/api/internos/contrataciones/facturables")
                    .header(HttpHeaders.COOKIE, TokenDeServicio.comoCookie(secreto, NOMBRE_DEL_SERVICIO))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ContratacionFacturable>>() { });
        } catch (RestClientException error) {
            throw new ContratacionesNoDisponibleException(error);
        }
    }
}
