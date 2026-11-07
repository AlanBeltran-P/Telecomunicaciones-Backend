package com.telecom.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest(properties = "eureka.client.enabled=false")
class ApiGatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void laRutaDeAuthVaAUsuariosService() {
        assertThat(uriDeLaRuta("usuarios")).isEqualTo("lb://usuarios-service");
    }

    @Test
    void laRutaDePlanesVaACatalogoService() {
        assertThat(uriDeLaRuta("catalogo")).isEqualTo("lb://catalogo-service");
    }

    private String uriDeLaRuta(String id) {
        List<Route> rutas = routeLocator.getRoutes().collectList().block();
        return rutas.stream()
                .filter(ruta -> ruta.getId().equals(id))
                .findFirst()
                .orElseThrow()
                .getUri()
                .toString();
    }
}
