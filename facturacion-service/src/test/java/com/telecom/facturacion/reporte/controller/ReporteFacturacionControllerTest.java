package com.telecom.facturacion.reporte.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.telecom.facturacion.debito.entity.DebitoAutomatico;
import com.telecom.facturacion.debito.repository.DebitoAutomaticoRepository;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReporteFacturacionControllerTest {

    private static final String NOVIEMBRE_DESDE = "2026-11-01";
    private static final String NOVIEMBRE_HASTA = "2026-11-30";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DebitoAutomaticoRepository debitoRepository;

    // Noviembre 2026: se emiten 3 facturas (una pagada, una pendiente y una vencida)
    // y se cobra además una factura emitida en octubre
    @BeforeEach
    void guardarFacturas() {
        guardar(1L, "18500", LocalDate.of(2026, 11, 2))
                .registrarPago(MedioDePago.MERCADO_PAGO, "ref-1", LocalDate.of(2026, 11, 5));
        guardar(2L, "27900", LocalDate.of(2026, 11, 2));
        guardar(3L, "34200", LocalDate.of(2026, 11, 3)).vencer();
        guardar(4L, "18500", LocalDate.of(2026, 10, 7))
                .registrarPago(MedioDePago.TARJETA_CREDITO, "ref-4", LocalDate.of(2026, 11, 1));

        debitoRepository.save(new DebitoAutomatico(10L, MedioDePago.TARJETA_CREDITO, "tok-1", LocalDate.of(2026, 11, 4)));
        debitoRepository.save(new DebitoAutomatico(20L, MedioDePago.PAYPAL, "tok-2", LocalDate.of(2026, 10, 1)));
    }

    @Test
    void sumaLoFacturadoLoCobradoYLaDeuda() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facturado").value(80600))
                .andExpect(jsonPath("$.cobrado").value(37000))
                .andExpect(jsonPath("$.deudaActual").value(62100));
    }

    @Test
    void cuentaElEstadoDeLasFacturasEmitidas() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.facturasEmitidas.pendientes").value(1))
                .andExpect(jsonPath("$.facturasEmitidas.pagadas").value(1))
                .andExpect(jsonPath("$.facturasEmitidas.vencidas").value(1));
    }

    @Test
    void separaLoCobradoPorMedioDePagoSiempreConLosCuatroMedios() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.cobradoPorMedio", hasSize(4)))
                .andExpect(jsonPath("$.cobradoPorMedio[0].medio").value("TARJETA_DEBITO"))
                .andExpect(jsonPath("$.cobradoPorMedio[0].pagos").value(0))
                .andExpect(jsonPath("$.cobradoPorMedio[1].medio").value("TARJETA_CREDITO"))
                .andExpect(jsonPath("$.cobradoPorMedio[1].monto").value(18500))
                .andExpect(jsonPath("$.cobradoPorMedio[2].medio").value("MERCADO_PAGO"))
                .andExpect(jsonPath("$.cobradoPorMedio[2].pagos").value(1));
    }

    @Test
    void cuentaLosDebitosAutomaticos() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.debitosAutomaticosNuevos").value(1))
                .andExpect(jsonPath("$.debitosAutomaticosActivos").value(2));
    }

    @Test
    void laSeriePorDiaSeparaFacturadoYCobrado() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_DESDE, NOVIEMBRE_HASTA, "DIA"))
                .andExpect(jsonPath("$.serie", hasSize(30)))
                .andExpect(jsonPath("$.serie[0].periodo").value("2026-11-01"))
                .andExpect(jsonPath("$.serie[0].facturado").value(0))
                .andExpect(jsonPath("$.serie[0].cobrado").value(18500))
                .andExpect(jsonPath("$.serie[1].facturado").value(46400));
    }

    @Test
    void laSeriePorMesAgrupaCadaMes() throws Exception {
        mockMvc.perform(reporte("2026-10-01", NOVIEMBRE_HASTA, "MES"))
                .andExpect(jsonPath("$.serie", hasSize(2)))
                .andExpect(jsonPath("$.serie[0].periodo").value("2026-10"))
                .andExpect(jsonPath("$.serie[0].facturado").value(18500))
                .andExpect(jsonPath("$.serie[0].cobrado").value(0))
                .andExpect(jsonPath("$.serie[1].facturado").value(80600))
                .andExpect(jsonPath("$.serie[1].cobrado").value(37000));
    }

    @Test
    void siDesdeEsPosteriorAHastaResponde400EnElIdiomaPedido() throws Exception {
        mockMvc.perform(reporte(NOVIEMBRE_HASTA, NOVIEMBRE_DESDE, "DIA").header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("The start date (2026-11-30) cannot be after the end date (2026-11-01)"));
    }

    @Test
    void unClienteNoPuedeVerElReporte() throws Exception {
        mockMvc.perform(get("/api/facturas/reporte")
                        .param("desde", NOVIEMBRE_DESDE)
                        .param("hasta", NOVIEMBRE_HASTA)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }

    // Una contratación distinta por factura: la factura es única por contratación y mes
    private Factura guardar(Long contratacionId, String monto, LocalDate emision) {
        return facturaRepository.save(new Factura(
                10L, contratacionId, "Fibra 300", new BigDecimal(monto),
                emision, emision.plusMonths(1), emision, emision.plusDays(10)));
    }

    private MockHttpServletRequestBuilder reporte(String desde, String hasta, String agrupacion) {
        return get("/api/facturas/reporte")
                .param("desde", desde)
                .param("hasta", hasta)
                .param("agrupacion", agrupacion)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
    }
}
