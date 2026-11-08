package com.telecom.facturacion.factura.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.telecom.facturacion.contratacion.client.ContratacionesClient;
import com.telecom.facturacion.contratacion.dto.ContratacionFacturable;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

// Historia 10: las facturas se generan solas cada mes, a partir del mes siguiente a contratar
@SpringBootTest
@Transactional
class GeneradorDeFacturasTest {

    private static final LocalDate CONTRATADA_EL = LocalDate.of(2026, 11, 7);

    @Autowired
    private GeneradorDeFacturas generadorDeFacturas;

    @Autowired
    private FacturaRepository facturaRepository;

    @MockitoBean
    private ContratacionesClient contratacionesClient;

    @Test
    void antesDeCumplirseElPrimerMesNoSeFactura() {
        contratacionDel(CONTRATADA_EL);

        assertThat(generadorDeFacturas.generarHasta(LocalDate.of(2026, 12, 6))).isZero();
    }

    @Test
    void alCumplirseElMesSeEmiteLaPrimeraFactura() {
        contratacionDel(CONTRATADA_EL);

        generadorDeFacturas.generarHasta(LocalDate.of(2026, 12, 7));

        Factura factura = facturaRepository.findAll().getFirst();
        assertThat(factura.getClienteId()).isEqualTo(10L);
        assertThat(factura.getPlanNombre()).isEqualTo("Internet 300 MB");
        assertThat(factura.getMonto()).isEqualByComparingTo("15000.50");
        assertThat(factura.getPeriodoDesde()).isEqualTo(LocalDate.of(2026, 11, 7));
        assertThat(factura.getPeriodoHasta()).isEqualTo(LocalDate.of(2026, 12, 7));
        assertThat(factura.getFechaEmision()).isEqualTo(LocalDate.of(2026, 12, 7));
        assertThat(factura.getFechaVencimiento()).isEqualTo(LocalDate.of(2026, 12, 17));
        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    void siElProcesoNoCorrioVariosMesesSePoneAlDia() {
        contratacionDel(CONTRATADA_EL);

        assertThat(generadorDeFacturas.generarHasta(LocalDate.of(2027, 2, 7))).isEqualTo(3);
    }

    @Test
    void correrloDosVecesNoDuplicaFacturas() {
        contratacionDel(CONTRATADA_EL);

        generadorDeFacturas.generarHasta(LocalDate.of(2026, 12, 7));
        int segundaVez = generadorDeFacturas.generarHasta(LocalDate.of(2026, 12, 7));

        assertThat(segundaVez).isZero();
        assertThat(facturaRepository.count()).isEqualTo(1);
    }

    @Test
    void unaContratacionDel31NoCorreElDiaDeFacturacion() {
        contratacionDel(LocalDate.of(2027, 1, 31));

        generadorDeFacturas.generarHasta(LocalDate.of(2027, 3, 31));

        assertThat(facturaRepository.findAll())
                .extracting(Factura::getPeriodoHasta)
                .containsExactlyInAnyOrder(LocalDate.of(2027, 2, 28), LocalDate.of(2027, 3, 31));
    }

    @Test
    void despuesDeUnaReactivacionNoSeFacturanLosMesesSinServicio() {
        // Contratada el 07/11, suspendida y reactivada el 01/02: el mes 07/01-07/02 queda sin facturar
        when(contratacionesClient.listarFacturables()).thenReturn(List.of(new ContratacionFacturable(
                1L, 10L, "Internet 300 MB", new BigDecimal("15000.50"), CONTRATADA_EL, LocalDate.of(2027, 2, 1))));

        generadorDeFacturas.generarHasta(LocalDate.of(2027, 3, 7));

        assertThat(facturaRepository.findAll())
                .extracting(Factura::getPeriodoDesde)
                .containsExactly(LocalDate.of(2027, 2, 7));
    }

    private void contratacionDel(LocalDate fecha) {
        when(contratacionesClient.listarFacturables()).thenReturn(List.of(
                new ContratacionFacturable(1L, 10L, "Internet 300 MB", new BigDecimal("15000.50"), fecha, fecha)));
    }
}
