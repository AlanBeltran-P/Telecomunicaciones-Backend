package com.telecom.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscoveryServerApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void elPanelDeEurekaResponde() {
        String panel = RestClient.create()
                .get()
                .uri("http://localhost:" + port + "/")
                .retrieve()
                .body(String.class);

        assertThat(panel).contains("Eureka");
    }
}
