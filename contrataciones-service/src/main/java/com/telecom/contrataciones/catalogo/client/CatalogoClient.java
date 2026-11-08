package com.telecom.contrataciones.catalogo.client;

import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.exception.CatalogoNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanNoDisponibleException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Consulta catalogo-service a través de Eureka (por nombre, no por dirección fija)
@Component
public class CatalogoClient {

    private final RestClient restClient;

    public CatalogoClient(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://catalogo-service").build();
    }

    // Solo devuelve planes activos: catalogo-service responde 404 para los dados de baja
    public PlanDelCatalogo buscarPlan(Long planId) {
        try {
            return restClient.get()
                    .uri("/api/planes/{id}", planId)
                    .retrieve()
                    .body(PlanDelCatalogo.class);
        } catch (HttpClientErrorException.NotFound error) {
            throw new PlanNoDisponibleException(planId);
        } catch (RestClientException error) {
            throw new CatalogoNoDisponibleException(error);
        }
    }
}
