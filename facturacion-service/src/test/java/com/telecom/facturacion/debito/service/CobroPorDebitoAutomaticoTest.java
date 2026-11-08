package com.telecom.facturacion.debito.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.facturacion.debito.dto.ActivarDebitoRequest;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// Historia 12: el proceso diario cobra las facturas de quienes tienen débito automático
@SpringBootTest
@Transactional
class CobroPorDebitoAutomaticoTest {

    @Autowired
    private DebitoAutomaticoService debitoService;

    @Autowired
    private FacturaRepository facturaRepository;

    @Test
    void cobraLasFacturasAdeudadasDeQuienTieneDebitoActivo() {
        debitoService.activar(10L, new ActivarDebitoRequest(MedioDePago.TARJETA_DEBITO, "tok_recurrente"));
        Factura factura = guardarFactura(10L, 1000L);

        assertThat(debitoService.cobrarFacturasAdeudadas()).isEqualTo(1);

        Factura cobrada = facturaRepository.findById(factura.getId()).orElseThrow();
        assertThat(cobrada.getEstado()).isEqualTo(EstadoFactura.PAGADA);
        assertThat(cobrada.getMedioDePago()).isEqualTo(MedioDePago.TARJETA_DEBITO);
    }

    @Test
    void noCobraAQuienNoTieneDebitoActivo() {
        Factura factura = guardarFactura(20L, 2000L);

        assertThat(debitoService.cobrarFacturasAdeudadas()).isZero();
        assertThat(facturaRepository.findById(factura.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    void siElCobroSeRechazaLaFacturaQuedaPendienteParaReintentar() {
        debitoService.activar(10L, new ActivarDebitoRequest(MedioDePago.PAYPAL, "rechazado"));
        Factura factura = guardarFactura(10L, 1000L);

        assertThat(debitoService.cobrarFacturasAdeudadas()).isZero();
        assertThat(facturaRepository.findById(factura.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    void noVuelveACobrarUnaFacturaYaPagada() {
        debitoService.activar(10L, new ActivarDebitoRequest(MedioDePago.TARJETA_DEBITO, "tok_recurrente"));
        guardarFactura(10L, 1000L);

        debitoService.cobrarFacturasAdeudadas();

        assertThat(debitoService.cobrarFacturasAdeudadas()).isZero();
    }

    private Factura guardarFactura(Long clienteId, Long contratacionId) {
        LocalDate desde = LocalDate.of(2026, 11, 7);
        LocalDate hasta = desde.plusMonths(1);
        return facturaRepository.save(new Factura(
                clienteId, contratacionId, "Internet 300 MB", new BigDecimal("15000"),
                desde, hasta, hasta, hasta.plusDays(10)));
    }
}
