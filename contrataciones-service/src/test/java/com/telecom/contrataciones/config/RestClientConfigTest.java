package com.telecom.contrataciones.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;

@SpringBootTest
class RestClientConfigTest {

    @Autowired
    private RestClient.Builder builderPorDefecto;

    @Autowired
    @LoadBalanced
    private RestClient.Builder builderBalanceado;

    // Si fueran el mismo, Eureka intentaría "balancear" localhost y el servicio no se registraría
    @Test
    void elBuilderPorDefectoNoEsElBalanceado() {
        assertThat(builderPorDefecto).isNotSameAs(builderBalanceado);
    }
}
