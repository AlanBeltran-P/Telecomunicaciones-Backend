package com.telecom.catalogo.plan;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PlanMapperTest {

    private final PlanMapper planMapper = Mappers.getMapper(PlanMapper.class);

    @Test
    void elPedidoSeConvierteEnUnPlanActivo() {
        PlanRequest request = new PlanRequest("Internet 300 MB", "Fibra óptica", new BigDecimal("15000.50"));

        Plan plan = planMapper.aPlan(request);

        assertThat(plan.getNombre()).isEqualTo("Internet 300 MB");
        assertThat(plan.getDescripcion()).isEqualTo("Fibra óptica");
        assertThat(plan.getPrecio()).isEqualByComparingTo("15000.50");
        assertThat(plan.isActivo()).isTrue();
        assertThat(plan.getId()).isNull();
    }

    @Test
    void elPlanSeConvierteEnLaRespuesta() {
        Plan plan = new Plan("Telefonía fija", null, new BigDecimal("5000"));

        PlanResponse respuesta = planMapper.aPlanResponse(plan);

        assertThat(respuesta.nombre()).isEqualTo("Telefonía fija");
        assertThat(respuesta.descripcion()).isNull();
        assertThat(respuesta.precio()).isEqualByComparingTo("5000");
    }

    @Test
    void unaListaDePlanesSeConvierteEnUnaListaDeRespuestas() {
        List<Plan> planes = List.of(
                new Plan("Internet 300 MB", null, new BigDecimal("15000")),
                new Plan("Telefonía fija", null, new BigDecimal("5000")));

        List<PlanResponse> respuestas = planMapper.aPlanResponses(planes);

        assertThat(respuestas).extracting(PlanResponse::nombre)
                .containsExactly("Internet 300 MB", "Telefonía fija");
    }
}
