package com.telecom.contrataciones.contratacion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.contrataciones.contratacion.dto.ContratacionResponse;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ContratacionMapperTest {

    private final ContratacionMapper contratacionMapper = Mappers.getMapper(ContratacionMapper.class);

    @Test
    void laRespuestaLlevaElPlanElPrecioYElEstado() {
        Contratacion contratacion = new Contratacion(
                10L, 1L, "Internet 300 MB", new BigDecimal("15000.50"),
                "Av. Siempre Viva 742", LocalDate.of(2026, 11, 7));

        ContratacionResponse respuesta = contratacionMapper.aContratacionResponse(contratacion);

        assertThat(respuesta.clienteId()).isEqualTo(10L);
        assertThat(respuesta.planNombre()).isEqualTo("Internet 300 MB");
        assertThat(respuesta.precioMensual()).isEqualByComparingTo("15000.50");
        assertThat(respuesta.direccion()).isEqualTo("Av. Siempre Viva 742");
        assertThat(respuesta.estado()).isEqualTo(EstadoContratacion.PENDIENTE_INSTALACION);
        assertThat(respuesta.fechaContratacion()).isEqualTo(LocalDate.of(2026, 11, 7));
    }
}
