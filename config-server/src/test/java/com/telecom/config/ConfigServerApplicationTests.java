package com.telecom.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void todosLosServiciosSeRegistranEnEurekaConSuIp() {
        assertThat(configuracionDe("usuarios-service")).contains("\"eureka.instance.prefer-ip-address\":true");
    }

    @Test
    void elGatewayEnviaAuthAUsuariosYPlanesACatalogo() {
        String configuracion = configuracionDe("api-gateway");

        assertThat(configuracion).contains("lb://usuarios-service", "/api/auth/**", "/api/usuarios/**", "/api/clientes/**", "/api/tecnicos/**");
        assertThat(configuracion).doesNotContain("/api/internos");
        assertThat(configuracion).contains("lb://catalogo-service", "/api/planes/**");
        assertThat(configuracion).contains("lb://contrataciones-service", "/api/contrataciones/**", "/api/instalaciones/**");
        assertThat(configuracion).contains("lb://facturacion-service", "/api/facturas/**");
    }

    @Test
    void cadaServicioUsaSuPropiaBaseDeDatos() {
        assertThat(configuracionDe("usuarios-service")).contains("5432/usuarios");
        assertThat(configuracionDe("catalogo-service")).contains("5432/catalogo");
        assertThat(configuracionDe("contrataciones-service")).contains("5432/contrataciones");
        assertThat(configuracionDe("facturacion-service")).contains("5432/facturacion");
    }

    private String configuracionDe(String servicio) {
        return RestClient.create()
                .get()
                .uri("http://localhost:" + port + "/" + servicio + "/default")
                .retrieve()
                .body(String.class);
    }
}
