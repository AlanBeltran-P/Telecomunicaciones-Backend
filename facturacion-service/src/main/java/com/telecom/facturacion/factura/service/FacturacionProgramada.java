package com.telecom.facturacion.factura.service;

import com.telecom.facturacion.debito.service.DebitoAutomaticoService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Corre todos los días (por defecto a las 2 de la mañana, ver facturacion.cron):
// primero genera las facturas nuevas y después cobra las de quienes tienen débito automático
@Component
public class FacturacionProgramada {

    private static final Logger log = LoggerFactory.getLogger(FacturacionProgramada.class);

    private final GeneradorDeFacturas generadorDeFacturas;
    private final DebitoAutomaticoService debitoAutomaticoService;

    public FacturacionProgramada(
            GeneradorDeFacturas generadorDeFacturas,
            DebitoAutomaticoService debitoAutomaticoService) {
        this.generadorDeFacturas = generadorDeFacturas;
        this.debitoAutomaticoService = debitoAutomaticoService;
    }

    // Cada paso tiene su propio manejo de errores: si uno falla, los demás igual corren
    @Scheduled(cron = "${facturacion.cron}")
    public void procesarElDia() {
        generarLasDeHoy();
        cobrarConDebitoAutomatico();
    }

    private void generarLasDeHoy() {
        try {
            int generadas = generadorDeFacturas.generarHasta(LocalDate.now());
            log.info("Facturación diaria: {} facturas nuevas", generadas);
        } catch (RuntimeException error) {
            // Si falla (por ejemplo, contrataciones-service caído), mañana se pone al día
            log.error("Falló la facturación diaria", error);
        }
    }

    private void cobrarConDebitoAutomatico() {
        try {
            int cobradas = debitoAutomaticoService.cobrarFacturasAdeudadas();
            log.info("Débito automático: {} facturas cobradas", cobradas);
        } catch (RuntimeException error) {
            log.error("Falló el cobro por débito automático", error);
        }
    }
}
