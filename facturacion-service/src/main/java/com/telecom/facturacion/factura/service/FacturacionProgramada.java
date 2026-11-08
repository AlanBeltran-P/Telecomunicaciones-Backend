package com.telecom.facturacion.factura.service;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Corre todos los días (por defecto a las 2 de la mañana, ver facturacion.cron)
@Component
public class FacturacionProgramada {

    private static final Logger log = LoggerFactory.getLogger(FacturacionProgramada.class);

    private final GeneradorDeFacturas generadorDeFacturas;

    public FacturacionProgramada(GeneradorDeFacturas generadorDeFacturas) {
        this.generadorDeFacturas = generadorDeFacturas;
    }

    @Scheduled(cron = "${facturacion.cron}")
    public void generarLasDeHoy() {
        try {
            int generadas = generadorDeFacturas.generarHasta(LocalDate.now());
            log.info("Facturación diaria: {} facturas nuevas", generadas);
        } catch (RuntimeException error) {
            // Si falla (por ejemplo, contrataciones-service caído), mañana se pone al día
            log.error("Falló la facturación diaria", error);
        }
    }
}
