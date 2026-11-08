package com.telecom.facturacion.contratacion.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.telecom.facturacion.contratacion.dto.ContratacionFacturable;
import com.telecom.facturacion.contratacion.exception.ContratacionesNoDisponibleException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ContratacionesClientTest {

    private static final String URL = "http://contrataciones-service/api/internos/contrataciones/facturables";

    private MockRestServiceServer contrataciones;
    private ContratacionesClient contratacionesClient;

    @BeforeEach
    void prepararContratacionesSimulado() {
        RestClient.Builder builder = RestClient.builder();
        contrataciones = MockRestServiceServer.bindTo(builder).build();
        contratacionesClient = new ContratacionesClient(builder, "secreto-de-prueba-de-al-menos-32-caracteres");
    }

    @Test
    void devuelveLasContratacionesFacturablesYMandaElTokenDeServicio() {
        contrataciones.expect(requestTo(URL))
                .andExpect(header(HttpHeaders.COOKIE, startsWith("token=")))
                .andRespond(withSuccess("""
                        [{"id": 1, "clienteId": 10, "planNombre": "Internet 300 MB",
                          "precioMensual": 15000.50, "fechaContratacion": "2026-11-07",
                          "facturarDesde": "2026-11-07"}]
                        """, MediaType.APPLICATION_JSON));

        List<ContratacionFacturable> facturables = contratacionesClient.listarFacturables();

        assertThat(facturables).hasSize(1);
        assertThat(facturables.getFirst().fechaContratacion()).isEqualTo(LocalDate.of(2026, 11, 7));
    }

    @Test
    void pideLaSuspensionDeUnaContratacion() {
        contrataciones.expect(requestTo("http://contrataciones-service/api/internos/contrataciones/5/suspension"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header(HttpHeaders.COOKIE, startsWith("token=")))
                .andRespond(withSuccess());

        contratacionesClient.suspender(5L);

        contrataciones.verify();
    }

    @Test
    void siContratacionesFallaNoEstaDisponible() {
        contrataciones.expect(requestTo(URL)).andRespond(withServerError());

        assertThatThrownBy(() -> contratacionesClient.listarFacturables())
                .isInstanceOf(ContratacionesNoDisponibleException.class);
    }
}
