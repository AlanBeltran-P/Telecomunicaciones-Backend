package com.telecom.facturacion.factura.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.telecom.facturacion.contratacion.client.ContratacionesClient;
import com.telecom.facturacion.contratacion.exception.ContratacionesNoDisponibleException;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import com.telecom.facturacion.notificacion.client.EnviadorDeEmail;
import com.telecom.facturacion.notificacion.entity.Notificacion;
import com.telecom.facturacion.notificacion.repository.NotificacionRepository;
import com.telecom.facturacion.usuario.client.UsuariosClient;
import com.telecom.facturacion.usuario.dto.UsuarioDelSistema;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

// Historia 13: aviso al vencer (en el sistema y por email) y baja del plan a las 2 semanas
@SpringBootTest
@Transactional
class ControlDeVencimientosTest {

    private static final Long CLIENTE_ID = 10L;
    private static final Long CONTRATACION_ID = 5L;
    private static final LocalDate VENCE_EL = LocalDate.of(2026, 12, 17);

    @Autowired
    private ControlDeVencimientos controlDeVencimientos;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @MockitoBean
    private ContratacionesClient contratacionesClient;

    @MockitoBean
    private UsuariosClient usuariosClient;

    @MockitoBean
    private EnviadorDeEmail enviadorDeEmail;

    @BeforeEach
    void clienteConEmail() {
        when(usuariosClient.buscar(CLIENTE_ID))
                .thenReturn(new UsuarioDelSistema(CLIENTE_ID, "María", "maria@mail.com", "CLIENTE", true));
    }

    @Test
    void elDiaDelVencimientoTodaviaNoEstaVencida() {
        Factura factura = guardarFactura();

        controlDeVencimientos.controlarHasta(VENCE_EL);

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
        assertThat(notificacionRepository.count()).isZero();
    }

    @Test
    void alDiaSiguienteVenceYSeAvisaEnElSistemaYPorEmail() {
        Factura factura = guardarFactura();

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(1));

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.VENCIDA);
        Notificacion aviso = notificacionRepository.findByClienteIdOrderByFechaDesc(CLIENTE_ID).getFirst();
        assertThat(aviso.getAsunto()).isEqualTo("Tu factura venció");
        assertThat(aviso.getMensaje()).contains("17/12/2026", "31/12/2026");
        verify(enviadorDeEmail).enviar(eq("maria@mail.com"), eq("Tu factura venció"), contains("Internet 300 MB"));
    }

    @Test
    void unaFacturaPagadaNoVence() {
        Factura factura = guardarFactura();
        factura.registrarPago(MedioDePago.PAYPAL, "ref", VENCE_EL);

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(5));

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PAGADA);
    }

    @Test
    void antesDeLasDosSemanasNoSeDaDeBaja() {
        guardarFactura();

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(13));

        verify(contratacionesClient, never()).suspender(anyLong());
    }

    @Test
    void aLasDosSemanasDeVencidaSeDaDeBajaElPlanYSeAvisa() {
        Factura factura = guardarFactura();
        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(1));

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(14));

        verify(contratacionesClient).suspender(CONTRATACION_ID);
        assertThat(factura.isBajaSolicitada()).isTrue();
        assertThat(notificacionRepository.findByClienteIdOrderByFechaDesc(CLIENTE_ID))
                .extracting(Notificacion::getAsunto)
                .contains("Dimos de baja tu plan");
    }

    @Test
    void laBajaSePideUnaSolaVez() {
        guardarFactura();
        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(1));

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(14));
        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(15));

        verify(contratacionesClient, times(1)).suspender(CONTRATACION_ID);
    }

    @Test
    void siContratacionesNoRespondeSeReintentaAlDiaSiguiente() {
        Factura factura = guardarFactura();
        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(1));
        doThrow(new ContratacionesNoDisponibleException(new RuntimeException()))
                .when(contratacionesClient).suspender(CONTRATACION_ID);

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(14));

        assertThat(factura.isBajaSolicitada()).isFalse();
    }

    @Test
    void siElEmailFallaElAvisoEnElSistemaQuedaIgual() {
        guardarFactura();
        doThrow(new RuntimeException("SMTP caído")).when(enviadorDeEmail).enviar(eq("maria@mail.com"), eq("Tu factura venció"), contains("Internet"));

        controlDeVencimientos.controlarHasta(VENCE_EL.plusDays(1));

        assertThat(notificacionRepository.count()).isEqualTo(1);
    }

    private Factura guardarFactura() {
        LocalDate desde = LocalDate.of(2026, 11, 7);
        LocalDate hasta = desde.plusMonths(1);
        return facturaRepository.save(new Factura(
                CLIENTE_ID, CONTRATACION_ID, "Internet 300 MB", new BigDecimal("15000"),
                desde, hasta, hasta, VENCE_EL));
    }
}
