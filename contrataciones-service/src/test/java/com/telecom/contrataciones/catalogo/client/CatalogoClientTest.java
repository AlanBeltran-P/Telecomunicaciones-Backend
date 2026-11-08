package com.telecom.contrataciones.catalogo.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.exception.CatalogoNoDisponibleException;
import com.telecom.contrataciones.contratacion.exception.PlanNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatalogoClientTest {

    private static final String URL_PLAN_1 = "http://catalogo-service/api/planes/1";

    private MockRestServiceServer catalogo;
    private CatalogoClient catalogoClient;

    @BeforeEach
    void prepararCatalogoSimulado() {
        RestClient.Builder builder = RestClient.builder();
        catalogo = MockRestServiceServer.bindTo(builder).build();
        catalogoClient = new CatalogoClient(builder);
    }

    @Test
    void devuelveElPlanDelCatalogo() {
        catalogo.expect(requestTo(URL_PLAN_1)).andRespond(withSuccess("""
                {"id": 1, "nombre": "Internet 300 MB", "descripcion": "Fibra", "precio": 15000.50}
                """, MediaType.APPLICATION_JSON));

        PlanDelCatalogo plan = catalogoClient.buscarPlan(1L);

        assertThat(plan.nombre()).isEqualTo("Internet 300 MB");
        assertThat(plan.precio()).isEqualByComparingTo("15000.50");
    }

    @Test
    void siElCatalogoResponde404ElPlanNoEstaDisponible() {
        catalogo.expect(requestTo(URL_PLAN_1)).andRespond(withResourceNotFound());

        assertThatThrownBy(() -> catalogoClient.buscarPlan(1L))
                .isInstanceOf(PlanNoDisponibleException.class);
    }

    @Test
    void siElCatalogoFallaNoEstaDisponible() {
        catalogo.expect(requestTo(URL_PLAN_1)).andRespond(withServerError());

        assertThatThrownBy(() -> catalogoClient.buscarPlan(1L))
                .isInstanceOf(CatalogoNoDisponibleException.class);
    }
}
