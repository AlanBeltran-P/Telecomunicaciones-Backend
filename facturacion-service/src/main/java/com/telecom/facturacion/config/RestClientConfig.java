package com.telecom.facturacion.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Builder por defecto: lo usan Eureka y el Config Server para hablar con localhost
    @Bean
    @Primary
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Builder para llamar a otros microservicios por nombre ("http://contrataciones-service"), resuelto con Eureka
    @Bean
    @LoadBalanced
    RestClient.Builder restClientBuilderBalanceado() {
        return RestClient.builder();
    }
}
